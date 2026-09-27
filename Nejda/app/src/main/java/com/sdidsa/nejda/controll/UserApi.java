package com.sdidsa.nejda.controll;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.sdidsa.nejda.abs.utils.Store;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.controll.abs.Api;
import com.sdidsa.nejda.controll.abs.BodyParam;
import com.sdidsa.nejda.model.User;

import java.util.function.Consumer;

public class UserApi extends Api {
    private static final String LOGIN = "/user/login";
    private static final String LOGOUT = "/user/logout";
    private static final String GET_USER = "/user/getUser";
    private static final String SET_PFP = "/user/setPfp";
    private static final String SAVE_FIREBASE_TOKEN = "/user/saveFirebaseToken";

    public static void login(String userId, String password, Consumer<User> onResponse) {
        sendRequest(LOGIN, result -> {
                    if(result.has("userId")) {
                        User user = new User(result);
                        onResponse.accept(user);
                    } else {
                        onResponse.accept(null);
                    }
                },
                new BodyParam("userId", userId),
                new BodyParam("password", password));
    }

    public static void logout(User user, Consumer<User> onSuccess) {
        sendRequest(LOGOUT, result -> {
            onSuccess.accept(new User(result));
        }, user);
    }

    public static void saveFirebaseToken(User user) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed",
                                task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Store.setFirebaseToken(token, null);

                    saveFirebaseToken(user, token);
                });
    }

    public static void saveFirebaseToken(User user, String token) {
        user.setFirebaseToken(token);
        sendRequest(SAVE_FIREBASE_TOKEN, result -> {
        }, user);
    }

    public static void setPfp(String pfp, Consumer<User> onUser) {
        User user = TempStore.getUser();
        user.setPfp(pfp);
        sendRequest(SET_PFP, result -> {
            onUser.accept(new User(result));
        }, user);
    }

    public static void getUser(String userId, Consumer<User> onResponse) {
        sendRequest(GET_USER, result -> {
                    if(result.has("userId")) {
                        onResponse.accept(new User(result));
                    } else {
                        onResponse.accept(null);
                    }
                },
                new BodyParam("userId", userId));
    }
}
