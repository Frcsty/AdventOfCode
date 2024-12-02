package com.github.frcsty.interfaces;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface AdventDay {

    String FILE_PATH = "G:\\Projects\\IntelliJ\\Random\\AdventOfCode\\src\\main\\resources";

    int day();

    default List<String> readLines(String fileName) {
        try {
            final BufferedReader reader = new BufferedReader(new FileReader("%s/%s".formatted(FILE_PATH, fileName)));
            return reader.lines().toList();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to retrieve file input");
        }
    }

    default <T> List<T> readLines(String fileName, Function<String, T> mappingFunction) {
        final List<String> lines = this.readLines(fileName);
        final List<T> modified = new ArrayList<>();

        for (final String line : lines) {
            modified.add(mappingFunction.apply(line));
        }

        return modified;
    }

    default char[][] readAsCharArray(String fileName) {
        final List<String> lines = this.readLines(fileName);
        final char[][] result = new char[lines.size()][lines.get(0).length()];

        for (int index = 0; index < lines.size(); index++) {
            final char[] chars = lines.get(index).toCharArray();

            System.arraycopy(chars, 0, result[index], 0, chars.length);
        }

        return result;
    }
}