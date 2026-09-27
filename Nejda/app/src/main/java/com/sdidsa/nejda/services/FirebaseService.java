package com.sdidsa.nejda.services;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.controll.UserApi;
import com.sdidsa.nejda.model.Alert;
import com.sdidsa.nejda.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (!remoteMessage.getData().isEmpty()) {
            String type = remoteMessage.getData().get("type");
            switch (Objects.requireNonNull(type)) {
                case "message" -> {
                    handleMessage(remoteMessage.getData().get("message"));
                }
                case "alert" -> {
                    handleAlert(remoteMessage.getData().get("alert"));
                }
                case "stop_alert" -> {
                    handleStopAlert(remoteMessage.getData().get("alert"));
                }
                default -> {
                }
            }
        }
    }

    private void handleStopAlert(String alertString) {
        try {
            System.out.println(alertString);
            Alert alert = new Alert(new JSONObject(alertString));
            String title = alert.getTitle() + " ended";
            String body = "the ongoing alert was terminated, happy learning";
            showNotification(title, body);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Intent inte = broadcast(Action.ALERT_CHANGED);
        getApplicationContext().sendBroadcast(inte);
    }

    private void handleAlert(String alertString) {
        try {
            System.out.println(alertString);
            Alert alert = new Alert(new JSONObject(alertString));
            String title = alert.getTitle();
            String body = alert.getBody();
            showNotification(title, body);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Intent inte = broadcast(Action.ALERT_CHANGED);
        getApplicationContext().sendBroadcast(inte);
    }

    private void handleMessage(String msgString) {
        try {
            Message message = new Message(new JSONObject(msgString));
            if (message.getThread().getRestricted()) {
                String title = message.getThread().getTitle();
                String body = message.getText();
                showNotification(title, body);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Intent inte = broadcast(Action.MESSAGE_RECEIVED);
        inte.putExtra("message", msgString);
        getApplicationContext().sendBroadcast(inte);
    }

    public static Intent broadcast(Action action) {
        Intent intent = new Intent(Action.INTENT_FILTER);
        intent.putExtra("broadcast_action", action.name());
        return intent;
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                "main")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.notify(1001, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if (TempStore.getUser() != null)
            UserApi.saveFirebaseToken(TempStore.getUser());
    }
}
