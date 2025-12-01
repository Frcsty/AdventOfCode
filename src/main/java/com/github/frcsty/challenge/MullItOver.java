package com.github.frcsty.challenge;

import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.interfaces.AdventDay;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MullItOver implements AdventDay {

    private static final String INPUT_FILE = "mull_it_over";
    private static final Pattern REGEX_PATTERN = Pattern.compile("mul\\(([0-9]{0,3}),([0-9]{0,3})\\)");
    private static final Pattern DISABLE_PATTERN = Pattern.compile("don't\\(\\)");
    private static final Pattern ENABLE_PATTERN = Pattern.compile("do\\(\\)");

    @PartLoader
    public void solve() {
        int programValue = 0;

        final String program = this.readLine(INPUT_FILE);
        final Matcher matcher = REGEX_PATTERN.matcher(program);
        while (matcher.find()) {
            final int firstValue = Integer.parseInt(matcher.group(1));
            final int secondValue = Integer.parseInt(matcher.group(2));

            programValue += firstValue * secondValue;
        }

        System.out.printf("All the results multiplied give the number '%s'%n", programValue);
    }

    @PartLoader(part = 2)
    public void solveSecond() {
        int conditionalProgramValue = 0;

        String program = this.readLine(INPUT_FILE);
        ProgramStatus status = ProgramStatus.ENABLED;

        Matcher matcher = REGEX_PATTERN.matcher(program);
        while (matcher.find()) {

        }

        System.out.printf("The result of the conditional program is '%s'%n", conditionalProgramValue);
    }

    @Override
    public int day() {
        return 3;
    }

    private enum ProgramStatus {
        ENABLED,
        DISABLED
    }
}