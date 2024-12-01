package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class HistorianHysteria implements AdventDay {

    private static final String INPUT_FILE = "historian_hysteria";

    private final Map<ListPosition, LinkedList<Integer>> locationData = new HashMap<>();

    @PartLoader
    public void solve() {
        this.loadLocationData();

        int distance = 0;
        while (!this.locationData.get(ListPosition.FIRST).isEmpty()) {
            final LinkedList<Integer> first = this.locationData.get(ListPosition.FIRST);
            final LinkedList<Integer> second = this.locationData.get(ListPosition.SECOND);

            first.sort(Integer::compare);
            second.sort(Integer::compare);

            final int firstLowest = first.removeFirst();
            final int secondLowest = second.removeFirst();

            distance += Math.abs(firstLowest - secondLowest);

            this.locationData.put(ListPosition.FIRST, first);
            this.locationData.put(ListPosition.SECOND, second);
        }

        System.out.println("Distance between lists is '" + distance + "'");
    }

    @PartLoader(part = 2)
    public void solveSecond() {
        this.locationData.clear();
        this.loadLocationData(); // Load fresh data

        int similarityScore = 0;
        for (int index = 0; index < this.locationData.get(ListPosition.FIRST).size(); index++) {
            final int wanted = this.locationData.get(ListPosition.FIRST).get(index);
            final int count = (int) this.locationData.get(ListPosition.SECOND).stream()
                .filter((entry) -> entry == wanted)
                .count();

            similarityScore += wanted * count;
        }

        System.out.println("Similarity score between lists is '" + similarityScore + "'");
    }

    private void loadLocationData() {
        for (String line : this.readLines(INPUT_FILE)) {
            line = line.replace("  ", ""); // Get rid of the two excess spaces first
            final String[] parts = line.split(" ");

            final int first = Integer.parseInt(parts[ListPosition.FIRST.ordinal()]);
            final int second = Integer.parseInt(parts[ListPosition.SECOND.ordinal()]);

            this.addEntry(ListPosition.FIRST, first);
            this.addEntry(ListPosition.SECOND, second);
        }
    }

    private void addEntry(ListPosition position, int entry) {
        this.locationData.compute(position, ($, values) -> {
            if (values == null) {
                values = new LinkedList<>();
            }

            values.add(entry);
            return values;
        });
    }

    @Override
    public int day() {
        return 1;
    }

    private enum ListPosition {
        FIRST,
        SECOND
    }
}