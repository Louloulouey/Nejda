package com.sdidsa.nejda.abs.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/** @noinspection unused */
public class ErrorHandler {
    private static final String DEFAULT_TAG = "ErrorHandler";

    public static void handle(Throwable throwable, String action, String tag) {
        Log.e(tag, trace(throwable, action));
    }

    public static String trace(Throwable throwable, String action) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return throwable.getClass().getSimpleName() + " happened in thread [" +
                Thread.currentThread().getName() + "] while " + action + "\n" + sw;
    }

    public static void handle(Throwable throwable, String action) {
        handle(throwable, action, DEFAULT_TAG);
    }

    public static void log(String message) {
        log(message, DEFAULT_TAG);
    }

    public static void log(String message, String tag) {
        handle(new RuntimeException(""), message, tag);
    }

    public static String trace(String action) {
        return trace(new RuntimeException(""), action);
    }
}
