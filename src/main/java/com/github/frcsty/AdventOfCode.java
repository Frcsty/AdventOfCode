package com.github.frcsty;

import com.github.frcsty.entity.MethodHolder;
import com.github.frcsty.interfaces.AdventDay;
import com.github.frcsty.util.ReflectionUtil;
import java.util.Calendar;

public class AdventOfCode {

    private static final Calendar CALENDAR = Calendar.getInstance();
    private static final int CURRENT_DAY = CALENDAR.get(Calendar.DAY_OF_MONTH);

    public static void main(String[] args) {
        final AdventDay currentDay = ReflectionUtil.getDayOf(CURRENT_DAY);
        if (currentDay == null) {
            throw new RuntimeException("Could not retrieve day class for '" + CURRENT_DAY + "'");
        }

        AdventOfCode.solveDay(currentDay);
    }

    private static void solveDay(AdventDay day) {
        for (final MethodHolder method : ReflectionUtil.getPartMethods(day)) {
            try {
                method.getMethod().invoke(day);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}