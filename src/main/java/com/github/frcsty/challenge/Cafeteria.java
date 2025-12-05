package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cafeteria implements AdventDay {

    private static final String INPUT_FILE = "cafeteria";

    @PartLoader
    public void solveOne() {
        final AvailableIngredients ingredients = new AvailableIngredients(this.readLines(INPUT_FILE));
        System.out.println("[1] Fresh Available Ingredients: " + ingredients.getAllFreshAvailableIngredients());
    }

    @PartLoader(part = 2)
    public void solveTwo() {
        final FreshIngredients freshIngredients = new FreshIngredients(this.readLines(INPUT_FILE));
        System.out.println("[2] Fresh Ingredient Id Count: " + freshIngredients.getAllFreshIngredientIds());
    }

    @Override
    public int day() {
        return 5;
    }

    private static class FreshIngredients {
        private final List<IngredientRange> freshIngredients = new ArrayList<>();

        public FreshIngredients(List<String> input) {
            for (final String line : input) {
                if (line.isBlank() || !line.contains("-")) {
                    break;
                }

                final String[] parts = line.split("-");
                final long start = Long.parseLong(parts[0]);
                final long end = Long.parseLong(parts[1]);

                this.freshIngredients.add(new IngredientRange(start, end));
            }
        }

        public boolean isFresh(long id) {
            for (final IngredientRange range : this.freshIngredients) {
                if (!range.isFresh(id)) {
                    continue;
                }

                return true;
            }

            return false;
        }

        public long getAllFreshIngredientIds() {
            this.freshIngredients.sort(Comparator.comparingLong(IngredientRange::start));
            final List<IngredientRange> merged = new ArrayList<>();

            for (final IngredientRange range : this.freshIngredients) {
                if (merged.isEmpty() || merged.get(merged.size() - 1).end() < range.start()) {
                    merged.add(range);
                    continue;
                }

                final IngredientRange existingRange = merged.get(merged.size() - 1);
                merged.remove(existingRange);

                final IngredientRange mergedRange = new IngredientRange(existingRange.start, Math.max(range.end(), existingRange.end()));
                merged.add(mergedRange);
            }

            long totalIdCount = 0;
            for (final IngredientRange range : merged) {
                final long distance = (range.end() + 1) - range.start();
                totalIdCount += distance;
            }

            return totalIdCount;
        }

        private record IngredientRange(long start, long end) {

            public boolean isFresh(long id) {
                return this.start <= id && id <= this.end;
            }
        }
    }

    private static class AvailableIngredients {
        private final FreshIngredients freshIngredients;
        private final List<Long> availableIngredientIds = new ArrayList<>();

        public AvailableIngredients(List<String> input) {
            this.freshIngredients = new FreshIngredients(input);

            for (final String line : input) {
                if (line.isBlank() || line.contains("-")) {
                    continue;
                }

                this.availableIngredientIds.add(Long.parseLong(line));
            }
        }

        public long getAllFreshAvailableIngredients() {
            long fresh = 0;
            for (final long id : this.availableIngredientIds) {
                if (!this.freshIngredients.isFresh(id)) {
                    continue;
                }

                fresh++;
            }

            return fresh;
        }
    }
}