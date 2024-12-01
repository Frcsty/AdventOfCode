package com.github.frcsty.util;

import com.github.frcsty.AdventOfCode;
import com.github.frcsty.annotation.PartLoader;
import com.github.frcsty.entity.MethodHolder;
import com.github.frcsty.interfaces.AdventDay;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.reflections.Reflections;
import static org.reflections.scanners.Scanners.SubTypes;

public class ReflectionUtil {

    private static final Reflections REFLECTIONS = new Reflections(AdventOfCode.class.getPackage().getName());

    /**
     * Returns the day matching the provided {@link Integer} day,
     * or null if none found
     *
     * @param requiredDay Day to retrieve
     * @return Matching day or null
     */
    public static AdventDay getDayOf(int requiredDay) {
        AdventDay foundDay = null;

        for (final Class<?> clazz : REFLECTIONS.get(SubTypes.of(AdventDay.class).asClass())) {
            if (!AdventDay.class.isAssignableFrom(clazz)) {
                continue;
            }

            try {
                final AdventDay adventDay = (AdventDay) clazz.getConstructor().newInstance();
                if (adventDay.day() != requiredDay) {
                    continue;
                }

                foundDay = adventDay;
                break;
            } catch (Exception ignored) {}
        }

        return foundDay;
    }

    /**
     * Returns a sorted list of the annotated methods which solve the days challenge
     *
     * @param day Day to find methods for
     * @return Applicable method or empty collection
     */
    public static List<MethodHolder> getPartMethods(AdventDay day) {
        final List<MethodHolder> foundMethods = new ArrayList<>();

        for (final Method method : day.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PartLoader.class)) {
                continue;
            }

            final PartLoader partLoader = method.getAnnotation(PartLoader.class);
            foundMethods.add(new MethodHolder(partLoader.part(), method));
        }

        foundMethods.sort(Comparator.comparingInt(MethodHolder::getPart));
        return foundMethods;
    }
}