package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Lobby implements AdventDay {

    private static final String INPUT_FILE = "lobby";

    @PartLoader
    public void solve() {
        long totalJoltage = 0;

        for (final String line : this.readLines(INPUT_FILE)) {
            final BatteryArray array = new BatteryArray(line);
            final BatterySegment bestSegment = array.getBestSegment(2);

            totalJoltage += bestSegment.getTotalJoltage();
        }

        System.out.println("[1] Total Joltage is " + totalJoltage);
    }

    @PartLoader(part = 2)
    public void solveSecond() {
        long totalJoltage = 0;

        for (final String line : this.readLines(INPUT_FILE)) {
            final BatteryArray array = new BatteryArray(line);
            final BatterySegment bestSegment = array.getBestSegment(12);

            totalJoltage += bestSegment.getTotalJoltage();
        }

        System.out.println("[2] Total Joltage is " + totalJoltage);
    }

    @Override
    public int day() {
        return 3;
    }

    private class BatterySegment {
        private final List<Battery> batteries;

        public BatterySegment(List<Battery> batteries) {
            this.batteries = batteries;
        }

        public boolean isValid() {
            return IntStream.range(1, this.batteries.size()).allMatch(i -> this.batteries.get(i - 1).position() <= this.batteries.get(i).position());
        }

        public long getTotalJoltage() {
            return Long.parseLong(this.batteries.stream().map((battery) -> Integer.toString(battery.joltage())).collect(Collectors.joining()));
        }

        @Override
        public String toString() {
            return "Segment[%s]".formatted(this.batteries);
        }
    }

    private class BatteryArray {
        private final List<Battery> batteries;

        public BatteryArray(String input) {
            this.batteries = new LinkedList<>();

            this.populateBatteries(input);
        }

        public BatterySegment getBestSegment(int batteriesPerSegment) {
            final List<Battery> segmentBuilder = new ArrayList<>();

            int startIndex = 0;
            for (int batteryIndex = 0; batteryIndex < batteriesPerSegment; batteryIndex++) {
                Battery highestIndex = null;

                for (int i = startIndex; i < this.batteries.size(); i++) {
                    final Battery battery = this.batteries.get(i);
                    if (highestIndex == null) {
                        highestIndex = battery;
                        continue;
                    }

                    if (highestIndex.joltage() >= battery.joltage()) {
                        continue;
                    }

                    if (this.batteries.size() - i < (batteriesPerSegment - batteryIndex)) {
                        continue;
                    }

                    highestIndex = battery;
                }

                startIndex = highestIndex.position() + 1;
                segmentBuilder.add(highestIndex);
            }

            return new BatterySegment(segmentBuilder);
        }

        private void populateBatteries(String input) {
            int index = 0;
            for (char character : input.toCharArray()) {
                final int value = Integer.parseInt(Character.toString(character));
                this.batteries.add(new Battery(index, value));
                index++;
            }
        }
    }

    private record Battery(int position, int joltage) {

        @Override
        public String toString() {
            return "Battery[pos=%s,joltage=%s]".formatted(this.position, this.joltage);
        }
    }
}