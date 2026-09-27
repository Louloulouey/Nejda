package com.example.rescueapp.service;

import com.google.firebase.messaging.*;

import java.util.List;

public class NotificationManager {

    private static long mostRecentAlert = -1;
    public static void sendAlert(String alert, List<String> tokens) {
        long now = System.currentTimeMillis();
        if(now - mostRecentAlert < 5000) {
            return;
        }
        mostRecentAlert = now;
        MulticastMessage msg = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putData("type", "alert")
                .putData("alert", alert)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(msg);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
        }catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void stopAlert(String alert, List<String> tokens) {
        MulticastMessage msg = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putData("type", "stop_alert")
                .putData("alert", alert)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(msg);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
        }catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void sendMessage(String message, List<String> tokens) {
        MulticastMessage msg = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putData("type", "message")
                .putData("message", message)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(msg);
            System.out.println(response.getSuccessCount() + " messages were sent successfully");
        }catch (Exception x) {
            x.printStackTrace();
        }
    }
}
