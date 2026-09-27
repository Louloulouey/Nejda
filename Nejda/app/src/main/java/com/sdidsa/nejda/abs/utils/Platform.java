package com.sdidsa.nejda.abs.utils;

import android.os.Handler;
import android.os.Looper;

import com.sdidsa.nejda.abs.animation.base.Animation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.function.Supplier;

public class Platform {
    private static final Function<String, ThreadFactory> threadMaker =
            (name) ->
                    (runnable) -> {
                        Thread t = new Thread(runnable, name);
                        t.setDaemon(true);
                        return t;
    };

    private static final Function<String, ExecutorService> poolMaker =
            (name) -> Executors.newCachedThreadPool(threadMaker.apply(name));

    private static final ExecutorService back = poolMaker.apply("back_thread");
    private static final ExecutorService wait = poolMaker.apply("wait_thread");

    private static Handler handler;
    public static void runLater(Runnable r) {
        if(handler == null) handler = new Handler(Looper.getMainLooper());
        handler.post(r);
    }

    public static void runAfter(Runnable r, long after) {
        back.execute(() -> {
            sleepReal(after);
            runLater(r);
        });
    }

    public static void runAfterScaled(Runnable r, long after) {
        back.execute(() -> {
            sleep(after);
            runLater(r);
        });
    }

    public static void waitWhile(Supplier<Boolean> condition) {
        waitWhile(condition, -1);
    }

    public static void waitWhileNot(Supplier<Boolean> condition) {
        waitWhileNot(condition, -1);
    }

    public static boolean waitWhile(Supplier<Boolean> condition, long timeout) {
        long start = System.currentTimeMillis();
        while(condition.get() && (timeout < 0 || System.currentTimeMillis() - start < timeout)) {
            sleepReal(10);
        }
        return !condition.get();
    }

    public static boolean waitWhileNot(Supplier<Boolean> condition, long timeout) {
        long start = System.currentTimeMillis();
        while(!condition.get() && (timeout < 0 || System.currentTimeMillis() - start < timeout)) {
            sleepReal(10);
        }
        return condition.get();
    }

    public static void waitWhile(Supplier<Boolean> condition, Runnable post) {
        waitWhile(condition, post, -1);
    }

    public static void waitWhileNot(Supplier<Boolean> condition, Runnable post) {
        waitWhileNot(condition, post, -1);
    }

    public static void waitWhile(Supplier<Boolean> condition, Runnable post, long timeout) {
        wait.execute(() -> {
            if(waitWhile(condition, timeout))
                runLater(post);
        });
    }

    public static void waitWhileNot(Supplier<Boolean> condition, Runnable post, long timeout) {
        wait.execute(() -> {
            if(waitWhileNot(condition, timeout))
                runLater(post);
        });
    }

    public static void sleep(long duration) {
        try {
            long dur = Math.max((long) ((float) duration * Animation.timeScale), 0);
            Thread.sleep(dur);
        } catch (InterruptedException x) {
            Thread.currentThread().interrupt();
        }
    }

    public static void sleepReal(long duration) {
        try {
            long dur = Math.max(duration, 0);
            Thread.sleep(dur);
        } catch (InterruptedException x) {
            Thread.currentThread().interrupt();
        }
    }

    public static void runBack(Runnable action, Runnable post) {
        back.execute(() -> {
            safeRunnable(action).run();
            if(post != null) post.run();
        });
    }

    public static void runBack(Runnable action) {
        runBack(action, null);
    }

    private static Runnable safeRunnable(Runnable action) {
        return () -> {
            try {
                action.run();
            } catch (Exception x) {
                ErrorHandler.handle(x, "(doing something) in thread " + Thread.currentThread().getName());
            }
        };
    }
}
