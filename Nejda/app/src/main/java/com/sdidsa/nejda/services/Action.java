package com.sdidsa.nejda.services;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

public enum Action {
    MESSAGE_RECEIVED(),
    ALERT_CHANGED();

    public static final String INTENT_FILTER = "com.sdidsa.bondcheck.ACTION_REQUEST";

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public static void registerListener(Context context, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(INTENT_FILTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        }else {
            context.registerReceiver(receiver, filter);
        }
    }
}
