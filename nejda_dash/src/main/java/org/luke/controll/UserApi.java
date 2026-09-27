package org.luke.controll;
import org.luke.controll.abs.Api;
import org.luke.controll.abs.BodyParam;
import org.luke.model.User;

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
