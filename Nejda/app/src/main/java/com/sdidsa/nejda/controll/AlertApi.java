package com.sdidsa.nejda.controll;

import com.sdidsa.nejda.controll.abs.Api;
import com.sdidsa.nejda.model.Alert;
import com.sdidsa.nejda.model.AlertRequest;
import com.sdidsa.nejda.model.AlertRequestImage;

import java.util.List;
import java.util.function.Consumer;

public class AlertApi extends Api {
    private static final String GET_ACTIVE = "/alert/getActive";
    private static final String GET_RECENT = "/alert/getRecent";
    private static final String REQUEST_ALERT = "/alert/requestAlert";
    private static final String SAVE_REQUEST_ATTACHMENT = "/alert/saveRequestAttachment";

    public static void getActive(Consumer<Alert> onResponse) {
        sendRequest(GET_ACTIVE, result -> {
            if (result.has("id")) {
                onResponse.accept(new Alert(result));
            } else {
                onResponse.accept(null);
            }
        });
    }

    public static void getRecent(Consumer<List<Alert>> onResponse) {
        sendRequest(GET_RECENT, result -> {
            onResponse.accept(parseList(result, Alert.class));
        });
    }

    public static void requestAlert(AlertRequest request, Consumer<AlertRequest> onResponse) {
        sendRequest(REQUEST_ALERT, result -> {
            if (result.has("id")) {
                onResponse.accept(new AlertRequest(result));
            }
        }, request);
    }

    public static void saveRequestAttachment(AlertRequestImage requestImage,
                                        Consumer<AlertRequestImage> onResponse) {
        sendRequest(SAVE_REQUEST_ATTACHMENT, result -> {
            if (result.has("id")) {
                onResponse.accept(new AlertRequestImage(result));
            }
        }, requestImage);
    }
}
