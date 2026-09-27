package org.luke.model;

import javafx.util.Pair;
import org.json.JSONArray;
import org.luke.controll.abs.Api;
import org.luke.gui.exception.ErrorHandler;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

public class BeanParser {
    private static final HashMap<Class<?>, Function<String, ?>> parsers = new HashMap<>();
    private static final HashMap<Pair<String, String>, Function<String, ?>> byNameParsers = new HashMap<>();

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

        addParser("message", "attachments", s -> {
            return Api.parseList(new JSONArray(s), MessageAttachment.class);
        });

        addParser("thread", "messages", s -> {
            return Api.parseList(new JSONArray(s), Message.class);
        });

        addParser("alertrequest", "attachments", s -> {
            return Api.parseList(new JSONArray(s), AlertRequestAttachment.class);
        });
    }

    private static <T> void addParser(Class<T> type, Function<String, T> parser) {
        parsers.put(type, parser);
    }

    private static <T> void addParser(String beanName, String attributeName, Function<String, T> parser) {
        byNameParsers.put(new Pair<>(beanName, attributeName), parser);
    }

    public static boolean isSupported(Class<?> type) {
        return parsers.containsKey(type);
    }

    public static boolean isSupportedByName(String beanName, String attrName) {
        return byNameParsers.containsKey(new Pair<>(beanName, attrName));
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

    @SuppressWarnings("unchecked")
    public static <T> T parseByName(String beanName, String attrName, String src) {
        if(isSupportedByName(beanName, attrName)) {
            return (T) Objects.requireNonNull(byNameParsers.get(new Pair<>(beanName, attrName))).apply(src);
        } else {
            ErrorHandler.handle(new ParseException(beanName + "." + attrName + " is not supported", -1),
                    "parse " + src);
        }
        return null;
    }
}
