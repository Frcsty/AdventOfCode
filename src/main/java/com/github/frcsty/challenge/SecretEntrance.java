package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class SecretEntrance implements AdventDay {

    private static final int[] DIAL_POSITIONS = IntStream.range(0, 100).toArray();
    private static final int STARTING_DIAL_POSITION = 50;
    private static final String INPUT_FILE = "secret_entrance";

    @PartLoader
    public void solve() {
        final Counter counter = new Counter();

        System.out.println("[1]: " + counter.getCount());
    }

    @PartLoader(part = 2)
    public void solveSecond() {
        long totalZeroOccurrences = 0;
        int currentDialPosition = STARTING_DIAL_POSITION;

        for (final String action : SecretEntrance.this.readLines(INPUT_FILE)) {
            if (action.isEmpty()) {
                continue;
            }

            final Counter.Direction direction = Counter.Direction.SYMBOL_MAPPED.get(action.substring(0, 1));
            final int distance = Integer.parseInt(action.substring(1));

            for (int i = 0; i < distance; i++) {
                final int nextPosition = direction.apply(1);
                final int nextDialPosition = ((currentDialPosition + nextPosition) % DIAL_POSITIONS.length + DIAL_POSITIONS.length) % DIAL_POSITIONS.length;

                if (nextDialPosition == 0) {
                    totalZeroOccurrences++;
                }

                currentDialPosition = nextDialPosition;
            }
        }

        System.out.println("[2]: " + totalZeroOccurrences);
    }

    @Override
    public int day() {
        return 1;
    }

    private class Counter {
        private int currentDialPosition;
        private int occurrenceAtZero;

        public Counter() {
            this.currentDialPosition = STARTING_DIAL_POSITION;

            this.executeActions();
        }

        public int getCount() {
            return this.occurrenceAtZero;
        }

        private void executeActions() {
            for (final String action : SecretEntrance.this.readLines(INPUT_FILE)) {
                if (action.isEmpty()) {
                    continue;
                }

                final Direction direction = Direction.SYMBOL_MAPPED.get(action.substring(0, 1));
                final int distance = Integer.parseInt(action.substring(1));

                this.currentDialPosition = this.getNextDialPosition(direction, distance);
                if (this.currentDialPosition == 0) {
                    this.occurrenceAtZero++;
                }
            }
        }

        private int getNextDialPosition(Direction direction, int distance) {
            final int nextPosition = direction.apply(distance);
            return ((this.currentDialPosition + nextPosition) % DIAL_POSITIONS.length + DIAL_POSITIONS.length) % DIAL_POSITIONS.length;
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