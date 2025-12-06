package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class TrashCompactor implements AdventDay {

    private static final String INPUT_FILE = "trash_compactor";

    @PartLoader
    public void solveOne() {
        final HomeworkTemplate homework = new WrongHomework(this.readLines(INPUT_FILE));
        System.out.println("[1] Grand Total: " + homework.getTotalValue());
    }

    @PartLoader(part = 2)
    public void solveTwo() {
        final HomeworkTemplate homework = new Homework(this);
        System.out.println("[2] Grand Total: " + homework.getTotalValue());
    }

    @Override
    public int day() {
        return 6;
    }

    private static abstract class HomeworkTemplate {
        protected final List<MathOperation> operations = new ArrayList<>();

        public long getTotalValue() {
            long totalValue = 0;
            for (final MathOperation operation : this.operations) {
                totalValue += operation.getTotalValue();
            }

            return totalValue;
        }
    }

    private static class Homework extends HomeworkTemplate {

        public Homework(AdventDay parent) {
            final Map<Integer, List<Long>> mappedValues = new HashMap<>();
            final Map<Integer, Operation> mappedOperations = new HashMap<>();
            final char[][] lines = parent.readAsCharArray(INPUT_FILE);

            int index = 0;
            for (int column = lines[0].length - 1; column >= 0; column--) {
                boolean isSpaceSeparator = true;
                for (char[] line : lines) {
                    final char character = line[column];
                    if (character != ' ') {
                        isSpaceSeparator = false;
                        break;
                    }
                }

                if (isSpaceSeparator) {
                    index++;
                    continue;
                }

                final StringBuilder columnBuilder = new StringBuilder();
                for (char[] line : lines) {
                    final char character = line[column];
                    final Operation operation = Operation.getBySymbol(character);
                    if (operation != null) {
                        mappedOperations.put(index, operation);
                        continue;
                    }

                    columnBuilder.append(character);
                }
                
                mappedValues.compute(index, ($, values) -> {
                    if (values == null) {
                        values = new ArrayList<>();
                    }

                    values.add(Long.parseLong(columnBuilder.toString().trim()));
                    return values;
                });
            }

            for (int operationIndex = 0; operationIndex < mappedOperations.size(); operationIndex++) {
                this.operations.add(new MathOperation(mappedOperations.get(operationIndex), mappedValues.get(operationIndex)));
            }
        }
    }

    private static class WrongHomework extends HomeworkTemplate {

        public WrongHomework(List<String> input) {
            final Map<Integer, List<Long>> mappedValues = new HashMap<>();
            final Map<Integer, Operation> mappedOperations = new HashMap<>();

            for (final String line : input) {
                final String[] parts = line.split(" ");

                int index = 0;
                for (final String part : parts) {
                    if (part.isBlank()) {
                        continue;
                    }

                    final Operation operation = Operation.getBySymbol(part);
                    if (operation != null) {
                        mappedOperations.put(index++, operation);
                        continue;
                    }

                    mappedValues.compute(index, ($, values) -> {
                        if (values == null) {
                            values = new ArrayList<>();
                        }

                        values.add(Long.parseLong(part));
                        return values;
                    });

                    index++;
                }
            }

            for (int index = 0; index < mappedOperations.size(); index++) {
                this.operations.add(new MathOperation(mappedOperations.get(index), mappedValues.get(index)));
            }
        }
    }

    private static class MathOperation {
        private final List<Long> numbers;
        private final Operation operation;

        public MathOperation(Operation operation, List<Long> numbers) {
            this.numbers = numbers;
            this.operation = operation;
        }

        public long getTotalValue() {
            long value = 0;
            for (final long number : this.numbers) {
                value = this.operation.apply(value, number);
            }

            return value;
        }

        @Override
        public String toString() {
            return "MP[op=%s,values=%s]".formatted(this.operation, this.numbers);
        }
    }

    private enum Operation {

        ADD('+', Long::sum),
        MULTIPLY('*', (first, second) -> {
            if (first == 0) {
                return second;
            }

            return first * second;
        });

        public static final Map<Character, Operation> SYMBOL_MAPPED = new HashMap<>() {{
            for (final Operation operation : Operation.values()) {
                this.put(operation.symbol, operation);
            }
        }};

        private final char symbol;
        private final BiFunction<Long, Long, Long> modifier;

        Operation(char symbol, BiFunction<Long, Long, Long> modifier) {
            this.symbol = symbol;
            this.modifier = modifier;
        }

        public static Operation getBySymbol(String symbol) {
            if (symbol.length() > 1 || symbol.isBlank()) {
                return null;
            }

            return SYMBOL_MAPPED.get(symbol.charAt(0));
        }

        public static Operation getBySymbol(char symbol) {
            return SYMBOL_MAPPED.get(symbol);
        }

        public long apply(long value, long number) {
            return this.modifier.apply(value, number);
        }
    }
}