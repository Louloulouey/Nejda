package org.luke.controll;

import org.luke.controll.abs.Api;
import org.luke.model.Alert;
import org.luke.model.AlertRequest;
import org.luke.model.AlertRequestAttachment;

import java.util.List;
import java.util.function.Consumer;

public class AlertApi extends Api {
    private static final String GET_ACTIVE = "/alert/getActive";
    private static final String GET_RECENT = "/alert/getRecent";
    private static final String REQUEST_ALERT = "/alert/requestAlert";
    private static final String GET_REQUESTS = "/alert/getAlertRequests";
    private static final String SAVE_REQUEST_ATTACHMENT = "/alert/saveRequestAttachment";
    private static final String CREATE_ALERT = "/alert/add";
    private static final String TREAT_REQUEST = "/alert/treat";
    private static final String END_ALERT = "/alert/end";
    private static final String DELETE_ALERT = "/alert/delete";
    private static final String GET_ALL = "/alert/getAll";

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

    public static void getAll(Consumer<List<Alert>> onResponse) {
        sendRequest(GET_ALL, result -> {
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

    public static void treat(AlertRequest request, Consumer<AlertRequest> onResponse) {
        sendRequest(TREAT_REQUEST, result -> {
            if (result.has("id")) {
                onResponse.accept(new AlertRequest(result));
            }
        }, request);
    }

    public static void end(Alert alert, Consumer<Alert> onResponse) {
        sendRequest(END_ALERT, result -> {
            if (result.has("id")) {
                onResponse.accept(new Alert(result));
            }
        }, alert);
    }

    public static void delete(Alert alert, Consumer<Alert> onResponse) {
        sendRequest(DELETE_ALERT, result -> {
            onResponse.accept(new Alert(result));
        }, alert);
    }

    public static void createAlert(Alert alert, Consumer<Alert> onResponse) {
        sendRequest(CREATE_ALERT, result -> {
            if(result.has("id")) {
                onResponse.accept(new Alert(result));
            }
        }, alert);
    }

    public static void getRequests(Consumer<List<AlertRequest>> onResponse) {
        sendRequest(GET_REQUESTS, result -> {
            onResponse.accept(parseList(result, AlertRequest.class));
        });
    }

    public static void saveRequestAttachment(AlertRequestAttachment requestImage,
                                             Consumer<AlertRequestAttachment> onResponse) {
        sendRequest(SAVE_REQUEST_ATTACHMENT, result -> {
            if (result.has("id")) {
                onResponse.accept(new AlertRequestAttachment(result));
            }
        }, requestImage);
    }
}
