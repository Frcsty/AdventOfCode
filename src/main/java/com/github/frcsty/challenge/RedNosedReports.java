package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

public class RedNosedReports implements AdventDay {

    private static final String INPUT_FILE = "red_nosed_reports";

    @PartLoader
    public void solve() {
        int safeReports = 0;

        for (final Report report : this.readLines(INPUT_FILE, Report::create)) {
            if (!report.isSafe()) {
                continue;
            }

            safeReports++;
        }

        System.out.println("There are '" + safeReports + "' safe reports");
    }

    @PartLoader(part = 2)
    public void solveSecond() {
        int safeReports = 0;
        int safeDampenedReports = 0;

        for (final Report report : this.readLines(INPUT_FILE, Report::create)) {
            if (report.isSafe()) {
                safeReports++;
                continue;
            }

            if (!report.isSafeDampened()) {
                continue;
            }

            safeDampenedReports++;
        }

        final int totalSafeReports = safeReports + safeDampenedReports;
        System.out.printf("There are '%s' total safe reports (safe=%s,dampened=%s)%n", totalSafeReports, safeReports, safeDampenedReports);
    }

    @Override
    public int day() {
        return 2;
    }

    public static class Report {
        private final List<Integer> levels;

        public Report(List<Integer> levels) {
            this.levels = levels;
        }

        public static Report create(String line) {
            return new Report(new ArrayList<>(Arrays.stream(line.split(" "))
                .mapToInt(Integer::parseInt)
                .boxed().toList()));
        }

        public int isSafe(List<Integer> levels) {
            LevelOrder order = null;
            int previous = -1;

            for (int index = 0; index < levels.size(); index++) {
                final int level = levels.get(index);

                final LevelOrder currentOrder = LevelOrder.getForValues(previous, level);
                if (currentOrder == LevelOrder.EQUAL) {
                    System.out.printf("%s is unsafe due to no order (no value difference)%n", this);
                    return index;
                }

                if (previous != -1 && Math.abs(level - previous) > 3) {
                    System.out.printf("%s is unsafe due to value difference (valueDiff=%s)%n", this, Math.abs(level - previous));
                    return index;
                }

                if (order != null && currentOrder != order && previous != -1) {
                    System.out.printf("%s is unsafe due to ordering (previous=%s,current=%s)%n", this, order, currentOrder);
                    return index;
                }

                order = previous == -1 ? null : currentOrder;
                previous = level;
            }

            //System.out.printf("%s is safe%n", this);
            return -1;
        }

        public boolean isSafe() {
            return this.isSafe(this.levels) == -1;
        }

        public boolean isSafeDampened() {
            final int unsafeLevelIndex = this.isSafe(this.levels);
            if (unsafeLevelIndex == -1) {
                return true;
            }

            this.levels.remove(unsafeLevelIndex);
            return this.isSafe();
        }

        @Override
        public String toString() {
            return "Report[levels=%s]".formatted(this.levels);
        }
    }

    private enum LevelOrder {
        INCREASING((first, second) -> first < second),
        DECREASING((first, second) -> first > second),
        EQUAL((first, second) -> first == second);

        private static final LevelOrder[] VALUES = LevelOrder.values();

        private final BiPredicate<Integer, Integer> orderFunction;

        LevelOrder(BiPredicate<Integer, Integer> orderFunction) {
            this.orderFunction = orderFunction;
        }

        public static LevelOrder getForValues(int first, int second) {
            for (final LevelOrder order : VALUES) {
                if (!order.orderFunction.test(first, second)) {
                    continue;
                }

                return order;
            }

            return null;
        }
    }
}