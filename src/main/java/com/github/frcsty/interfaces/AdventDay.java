package com.github.frcsty.interfaces;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

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