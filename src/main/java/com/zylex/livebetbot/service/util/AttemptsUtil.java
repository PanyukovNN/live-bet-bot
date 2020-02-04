package com.zylex.livebetbot.service.util;

import java.util.function.Consumer;

public class AttemptsUtil {

    public static <T> boolean attempt(Consumer<T> consumer, T arg, int attemptsCount) {
        int attempts = attemptsCount;
        while (attempts-- > 0) {
            try {
                consumer.accept(arg);
                return true;
            } catch (Exception ignore) {
            }
        }
        return false;
    }
}
