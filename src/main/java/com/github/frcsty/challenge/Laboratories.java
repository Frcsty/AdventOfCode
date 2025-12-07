package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Laboratories implements AdventDay {

    private static final String INPUT_FILE = "laboratories";

    @PartLoader
    public void solveOne() {
        final Diagram diagram = new Diagram(this.readLines(INPUT_FILE));
        System.out.println("[1] Total Beam Splits: " + diagram.getBeamSplits());
    }

    @PartLoader(part = 2)
    public void solveTwo() {
        final QuantumDiagram diagram = new QuantumDiagram(this.readLines(INPUT_FILE));
        System.out.println("[2] Total Timelines: " + diagram.getTimelines());
    }

    @Override
    public int day() {
        return 7;
    }

    private static abstract class DiagramTemplate {
        protected final List<TachyonBeam> beams = new ArrayList<>();
        protected final ObjectType[][] diagram;

        public DiagramTemplate(List<String> input) {
            this.diagram = new ObjectType[input.size()][input.get(0).length()];

            for (int row = 0; row < input.size(); row++) {
                final String line = input.get(row);

                int column = 0;
                for (final char character : line.toCharArray()) {
                    final ObjectType type = ObjectType.SYMBOL_MAPPED.get(character);
                    if (type == null) {
                        continue;
                    }

                    if (type == ObjectType.START) {
                        this.beams.add(new TachyonBeam(row, column));
                    }

                    this.diagram[row][column] = type;
                    column++;
                }
            }
        }

        protected TachyonBeam splitBeam(TachyonBeam beam, int direction) {
            return new TachyonBeam(beam.row, beam.column + direction);
        }
    }

    private static class QuantumDiagram extends DiagramTemplate {
        private final int[][] visitedLocations;
        private final Map<Integer, String[][]> timelinePrintouts = new HashMap<>();
        private final Set<Timeline> visitedTimelines = new HashSet<>();

        private int timelines = 0;

        public QuantumDiagram(List<String> input) {
            super(input);

            this.visitedLocations = new int[input.size()][input.getFirst().length()];
            for (final int[] locations : this.visitedLocations) {
                Arrays.fill(locations, 0);
            }

            int depth = 0;
            boolean currentPath = true;
            while (currentPath && depth < 1_000) {
                currentPath = this.createQuantumTachyonBeam();
                depth++;
            }

            System.out.println("All Timelines: ");
            for (int index = 0; index < this.visitedLocations.length; index++) {
                for (final String[][] printout : this.timelinePrintouts.values()) {
                    System.out.print(Arrays.toString(printout[index]) + " ");
                }

                System.out.println();
            }

            for (int index = 0; index < this.visitedLocations.length; index++) {
                final StringBuilder builder = new StringBuilder();
                
                for (final int visitedCount : this.visitedLocations[index]) {
                    final int addition = 5 - (Integer.toString(visitedCount).length());
                    builder.append("[");
                    
                    if (visitedCount > 0) {
                        builder.append("\u001B[31m").append(visitedCount).append("\u001B[0m");
                    } else {
                        builder.append(visitedCount);
                    }
                    
                    builder.append(" ".repeat(addition)).append("]");
                }
                
                System.out.println(builder);
            }
        }

        private boolean createQuantumTachyonBeam() {
            final List<TachyonBeam> beams = new ArrayList<>();
            for (final TachyonBeam beam : this.beams) {
                beams.add(new TachyonBeam(beam.row, beam.column));
            }

            final ObjectType[][] currentDiagram = new ObjectType[this.diagram.length][this.diagram[0].length];
            final String[][] printout = new String[currentDiagram.length][currentDiagram[0].length];
            for (final String[] line : printout) {
                Arrays.fill(line, ".");
            }

            int depth = 0;
            final List<Position> visited = new ArrayList<>();
            while (depth < currentDiagram.length) {
                for (final TachyonBeam beam : new ArrayList<>(beams)) {
                    final int newPosition = beam.row + 1;
                    if (newPosition >= currentDiagram.length) {
                        continue; // we reached the bottom
                    }

                    final ObjectType tileType = this.diagram[newPosition][beam.column];
                    if (tileType == ObjectType.EMPTY) {
                        visited.add(new Position(beam.row, beam.column));
                        beam.setRow(newPosition);
                        currentDiagram[beam.row][beam.column] = ObjectType.BEAM;
                        printout[beam.row][beam.column] = "\u001B[31m|\u001B[0m";
                        continue;
                    }

                    if (tileType != ObjectType.SPLIT) {
                        continue;
                    }

                    // Since we encountered a splitter, the current beam is removed
                    visited.add(new Position(beam.row, beam.column));
                    beams.remove(beam);
                    beam.setRow(newPosition);

                    final TachyonBeam leftBeam = this.splitBeam(beam, -1);
                    final TachyonBeam rightBeam = this.splitBeam(beam, 1);
                    final int leftVisited = this.visitedLocations[leftBeam.row][leftBeam.column];
                    final int rightVisited = this.visitedLocations[rightBeam.row][rightBeam.column];

                    final TachyonBeam prioritised = leftVisited < rightVisited ? leftBeam : rightBeam;
                    final ObjectType newType = currentDiagram[prioritised.row][prioritised.column];
                    //if (newType != null) {
                    //    continue;
                    //}

                    this.proceedBeamSplit(currentDiagram, printout, beams, prioritised);
                }

                depth++;
            }

            final Timeline timeline = new Timeline(visited);
            if (!this.visitedTimelines.contains(timeline)) {
                this.visitedTimelines.add(timeline);
                this.timelinePrintouts.put(this.timelines, printout);
                this.timelines++;
            }

            return true;
        }

        private void proceedBeamSplit(ObjectType[][] currentDiagram, String[][] printout, List<TachyonBeam> beams, TachyonBeam beam) {
            currentDiagram[beam.row][beam.column] = ObjectType.BEAM;
            beams.add(beam);
            printout[beam.row][beam.column] = "\u001B[31m|\u001B[0m";
            this.visitedLocations[beam.row][beam.column]++;
        }

        public int getTimelines() {
            return this.timelines;
        }

        private record Timeline(List<Position> positions) {

            @Override
            public int hashCode() {
                return Objects.hash(this.positions);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof Timeline(List<Position> positions1))) {
                    return false;
                }
                return Objects.equals(this.positions, positions1);
            }
        }

        private record Position(int row, int column) {}
    }

    private static class Diagram extends DiagramTemplate {

        private int splittersHit = 0;

        public Diagram(List<String> input) {
            super(input);

            this.computeTachyonBeam();
        }

        private void computeTachyonBeam() {
            final ObjectType[][] currentDiagram = Arrays.copyOf(this.diagram, this.diagram.length);

            int depth = 0;
            while (depth < this.diagram.length) {
                for (final TachyonBeam beam : new ArrayList<>(this.beams)) {
                    final int newPosition = beam.row + 1;
                    if (newPosition >= this.diagram.length) {
                        continue; // we reached the bottom
                    }

                    final ObjectType tileType = this.diagram[newPosition][beam.column];
                    if (tileType == ObjectType.EMPTY) {
                        beam.setRow(newPosition);
                        currentDiagram[beam.row][beam.column] = ObjectType.BEAM;
                        continue;
                    }

                    if (tileType != ObjectType.SPLIT) {
                        continue;
                    }

                    this.splittersHit++;

                    // Since we encountered a splitter, the current beam is removed
                    this.beams.remove(beam);
                    beam.setRow(newPosition);

                    final TachyonBeam leftBeam = this.splitBeam(beam, -1);
                    if (this.diagram[leftBeam.row][leftBeam.column] == ObjectType.EMPTY) {
                        currentDiagram[leftBeam.row][leftBeam.column] = ObjectType.BEAM;
                        this.beams.add(leftBeam);
                    }

                    final TachyonBeam rightBeam = this.splitBeam(beam, 1);
                    if (this.diagram[rightBeam.row][rightBeam.column] == ObjectType.EMPTY) {
                        currentDiagram[rightBeam.row][rightBeam.column] = ObjectType.BEAM;
                        this.beams.add(rightBeam);
                    }
                }

                depth++;
            }
        }

        private long getBeamSplits() {
            return this.splittersHit;
        }
    }

    private static class TachyonBeam {
        private int row;
        private int column;

        public TachyonBeam(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        @Override
        public String toString() {
            return "Beam[row=%s,column=%s]".formatted(this.row, this.column);
        }
    }

    private enum ObjectType {

        START('S'),
        EMPTY('.'),
        SPLIT('^'),
        BEAM('|');

        private static final Map<Character, ObjectType> SYMBOL_MAPPED = new HashMap<>() {{
            for (final ObjectType objectType : ObjectType.values()) {
                this.put(objectType.symbol, objectType);
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