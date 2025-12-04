package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintingDepartment implements AdventDay {

    private static final String INPUT_FILE = "printing_department";

    @PartLoader
    public void solveOne() {
        final Warehouse warehouse = new Warehouse(this.readLines(INPUT_FILE));

        System.out.println("[1] Accessible Rolls: " + warehouse.countAccessible(false));
    }

    @PartLoader
    public void solveTwo() {
        final Warehouse warehouse = new Warehouse(this.readLines(INPUT_FILE));

        System.out.println("[2] Accessible Rolls: " + warehouse.countAllAccessible());
    }

    @Override
    public int day() {
        return 4;
    }

    private static class Warehouse {
        private final List<PaperRoll> paperRolls;
        private final ObjectType[][] grid;

        public Warehouse(List<String> input) {
            this.paperRolls = new ArrayList<>();
            this.grid = new ObjectType[input.size()][input.get(0).length()];
            for (int i = 0; i < this.grid.length; i++) {
                Arrays.fill(this.grid[i], ObjectType.NONE);
            }

            int column = 0;
            for (final String line : input) {
                int row = 0;
                for (final char symbol : line.toCharArray()) {
                    final ObjectType type = ObjectType.SYMBOL_MAPPED.get(symbol);
                    if (type == ObjectType.NONE) {
                        row++;
                        continue;
                    }

                    this.grid[column][row] = type;
                    this.paperRolls.add(new PaperRoll(column, row));
                    row++;
                }

                column++;
            }
        }

        private int countAllAccessible() {
            int totalAccessible = 0;

            int accessible;
            while (true) {
                accessible = this.countAccessible(true);
                if (accessible <= 0) {
                    break;
                }

                totalAccessible += accessible;
            }

            return totalAccessible;
        }

        private int countAccessible(boolean remove) {
            int accessible = 0;
            for (final PaperRoll roll : new ArrayList<>(this.paperRolls)) {
                final int rollColumn = roll.column();
                final int rollRow = roll.row();

                final int startingColumn = Math.max(0, rollColumn - 1);
                final int startingRow = Math.max(0, rollRow - 1);
                final int endingColumn = Math.min(this.grid.length - 1, rollColumn + 1);
                final int endingRow = Math.min(this.grid[0].length - 1, rollRow + 1);

                int filledPositions = 0;
                for (int column = startingColumn; column <= endingColumn; column++) {
                    for (int row = startingRow; row <= endingRow; row++) {
                        if (column == rollColumn && row == rollRow) {
                            continue;
                        }

                        final ObjectType type = this.grid[column][row];
                        if (type == ObjectType.NONE) {
                            continue;
                        }

                        filledPositions++;
                    }
                }

                if (filledPositions >= 4) {
                    continue;
                }

                if (remove) {
                    this.grid[rollColumn][rollRow] = ObjectType.NONE;
                    this.paperRolls.remove(roll);
                }

                accessible++;
            }

            return accessible;
        }
    }

    private record PaperRoll(int column, int row) {

        @Override
        public String toString() {
            return "Roll[column=%s,row=%s]".formatted(this.column, this.row);
        }
    }

    private enum ObjectType {

        NONE('.'),
        ROLL('@');

        private static final Map<Character, ObjectType> SYMBOL_MAPPED = new HashMap<>() {{
             for (final ObjectType type : ObjectType.values()) {
                 this.put(type.symbol, type);
             }
        }};

        private final char symbol;

        ObjectType(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return this.symbol;
        }
    }
}