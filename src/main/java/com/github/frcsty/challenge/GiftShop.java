package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.HashSet;
import java.util.Set;

public class GiftShop implements AdventDay {

    private static final String INPUT_FILE = "gift_shop";

    @PartLoader
    public void solve() {
        long totalCount = 0;
        for (final String line : this.readLines(INPUT_FILE)) {
            for (final String input : line.split(",")) {
                final Range range = new Range(input, true);

                totalCount += range.getTotalInvalidCount();
            }
        }

        System.out.println("[1] Total count: " + totalCount);
    }

    @PartLoader(part = 2)
    public void solveSecond() {
        long totalCount = 0;
        for (final String line : this.readLines(INPUT_FILE)) {
            for (final String input : line.split(",")) {
                final Range range = new Range(input, false);
                totalCount += range.getTotalInvalidCount();
            }
        }

        System.out.println("[2] Total count: " + totalCount);
    }

    @Override
    public int day() {
        return 2;
    }

    private static class Range {
        private long start;
        private long end;
        private final Set<Long> invalidIds;

        public Range(String input, boolean oldPattern) {
            final String[] split = input.split("-");

            this.start = Long.parseLong(split[0]);
            this.end = Long.parseLong(split[1]);
            this.invalidIds = new HashSet<>();

            if (oldPattern) {
                this.checkIds();
            } else {
                this.checkUpdatedIds();
            }
        }

        private void checkIds() {
            for (long i = this.start; i <= this.end; i++) {
                if (this.isValid(i)) {
                    continue;
                }

                this.invalidIds.add(i);
            }
        }

        private void checkUpdatedIds() {
            for (long i = this.start; i <= this.end; i++) {
                if (this.isValidPattern(i)) {
                    continue;
                }

                this.invalidIds.add(i);
            }
        }

        private boolean isValid(long id) {
            final String stringed = Long.toString(id);
            final String partOne = stringed.substring(0, stringed.length() / 2);
            final String partTwo = stringed.substring(stringed.length() / 2);

            return !partOne.equalsIgnoreCase(partTwo);
        }

        private boolean isValidPattern(long id) {
            final String stringed = Long.toString(id);
            final String pattern = findPattern(stringed);
            if (pattern.isEmpty() || pattern.length() == stringed.length()) {
                return true;
            }

            final String result = stringed.replaceAll(pattern, "");
            return !result.isEmpty();
        }

        private static String findPattern(String s) {
            int n = s.length();
            int[] lps = new int[n];

            // Compute KMP prefix table
            for (int i = 1, j = 0; i < n; ) {
                if (s.charAt(i) == s.charAt(j)) {
                    lps[i++] = ++j;
                } else if (j > 0) {
                    j = lps[j - 1];
                } else {
                    lps[i++] = 0;
                }
            }

            int len = n - lps[n - 1];
            return (len < n && n % len == 0) ? s.substring(0, len) : s;
        }

        public long getTotalInvalidCount() {
            long totalInvalidCount = 0;
            for (final long value : this.invalidIds) {
                totalInvalidCount += value;
            }

            return totalInvalidCount;
        }

        public Set<Long> getInvalidIds() {
            return this.invalidIds;
        }
    }
}