package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class SecretEntrance implements AdventDay {
    private final Counter counter = new Counter();

    private static final String INPUT_FILE = "secret_entrance";

    @PartLoader
    public void solve() {
        System.out.println("[1]: " + this.counter.getCount());
    }

    @PartLoader(part = 2)
    public void solveSecond() {
        System.out.println("[2]: " + this.counter.getSecondCount());
    }

    @Override
    public int day() {
        return 1;
    }

    private class Counter {
        private int currentDialPosition;
        private int occurrenceAtZero;
        private int rollingOccurrenceAtZero;

        private static final int STARTING_DIAL_POSITION = 50;
        private static final int[] DIAL_POSITIONS = IntStream.range(0, 100).toArray();

        public Counter() {
            this.currentDialPosition = STARTING_DIAL_POSITION;

            this.executeActions();
        }

        public int getCount() {
            return this.occurrenceAtZero;
        }

        public int getSecondCount() {
            return this.rollingOccurrenceAtZero + this.occurrenceAtZero;
        }

        private void executeActions() {
            for (final String action : SecretEntrance.this.readLines(INPUT_FILE)) {
                if (action.isEmpty()) {
                    continue;
                }

                final Direction direction = Direction.SYMBOL_MAPPED.get(action.substring(0, 1));
                final int distance = Integer.parseInt(action.substring(1));

                System.out.println("[" + direction.symbol + "] " + distance + ": " + this.currentDialPosition + " (" + this.occurrenceAtZero + ")");

                this.currentDialPosition = this.getNextDialPosition(direction, distance);
                if (this.currentDialPosition == 0) {
                    this.occurrenceAtZero++;
                }
            }
        }

        private int getNextDialPosition(Direction direction, int distance) {
            final int nextPosition = direction.apply(distance);
            final int wrappedPosition = ((this.currentDialPosition + nextPosition) % DIAL_POSITIONS.length + DIAL_POSITIONS.length) % DIAL_POSITIONS.length;
            final int before = Math.floorDiv(this.currentDialPosition, DIAL_POSITIONS.length);
            final int after  = Math.floorDiv(this.currentDialPosition + nextPosition, DIAL_POSITIONS.length);

            int occurrences = Math.abs(after - before);
            if (wrappedPosition == 0 && occurrences > 0) {
                occurrences -= 1;
            }

            this.rollingOccurrenceAtZero += occurrences;
            return wrappedPosition;
        }

        private enum Direction {

            LEFT("L", (value) -> -value),
            RIGHT("R", (value) -> +value);

            private static final Map<String, Direction> SYMBOL_MAPPED = new HashMap<>() {{
                for (final Direction direction : Direction.values()) {
                    this.put(direction.getSymbol(), direction);
                }
            }};

            private final String symbol;
            private final UnaryOperator<Integer> modifier;

            Direction(String symbol, UnaryOperator<Integer> modifier) {
                this.symbol = symbol;
                this.modifier = modifier;
            }

            public String getSymbol() {
                return this.symbol;
            }

            public int apply(int distance) {
                return this.modifier.apply(distance);
            }
        }
    }
}