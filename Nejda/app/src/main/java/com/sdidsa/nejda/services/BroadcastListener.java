package com.sdidsa.nejda.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class BroadcastListener extends BroadcastReceiver {
    private final HashMap<Action, ArrayList<Consumer<Intent>>> listeners;

    public BroadcastListener(Context context) {
        listeners = new HashMap<>();
        Action.registerListener(context, this);
    }

    public void onIntent(Action event, Consumer<Intent> listener) {
        ArrayList<Consumer<Intent>> handlers =
                listeners.computeIfAbsent(event, k -> new ArrayList<>());
        handlers.add(listener);
    }

    public void on(Action event, Runnable action) {
        onIntent(event, intent -> action.run());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("broadcast_action")) {
            Action action = Action.valueOf(
                    Objects.requireNonNull(intent.getExtras())
                            .getString("broadcast_action"));

            List<Consumer<Intent>> handlers = listeners.get(action);
            if(handlers != null) {
                handlers.forEach(handler -> handler.accept(intent));
            }
        }
    }
}
