package com.sdidsa.nejda.model;

import com.sdidsa.nejda.abs.utils.ErrorHandler;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

public class BeanParser {
    private static final HashMap<Class<?>, Function<String, ?>> parsers = new HashMap<>();

    static {
        addParser(String.class, s -> s);
        addParser(Float.class, Float::parseFloat);
        addParser(Integer.class, Integer::parseInt);
        addParser(Double.class, Double::parseDouble);
        addParser(Long.class, Long::parseLong);
        addParser(Boolean.class, Boolean::parseBoolean);
        addParser(LocalDate.class, LocalDate::parse);
        addParser(LocalDateTime.class, LocalDateTime::parse);
        addParser(LocalTime.class, LocalTime::parse);
    }

    private static <T> void addParser(Class<T> type, Function<String, T> parser) {
        parsers.put(type, parser);
    }

    public static boolean isSupported(Class<?> type) {
        return parsers.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T parse(Class<T> type, String src) {
        if(isSupported(type)) {
            return (T) Objects.requireNonNull(parsers.get(type)).apply(src);
        } else {
            ErrorHandler.handle(new ParseException(type.getSimpleName() + " is not supported", -1),
                    "parse " + src);
        }
        return null;
    }
}
