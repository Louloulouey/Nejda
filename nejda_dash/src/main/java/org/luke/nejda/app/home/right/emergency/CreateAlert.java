package org.luke.nejda.app.home.right.emergency;

import javafx.scene.text.FontWeight;
import org.json.JSONObject;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.alert.AbstractOverlay;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.style.Style;
import org.luke.gui.window.Window;
import org.luke.model.Alert;
import org.luke.model.AlertRequest;
import org.luke.model.Severity;
import org.luke.nejda.ui.ComboVal;
import org.luke.nejda.ui.TextVal;

public class CreateAlert extends AbstractOverlay {

    private final TextVal title;
    private final TextVal body;
    private final TextVal location;
    private final ComboVal severity;

    private Alert alert;

    public CreateAlert(Window window) {
        super(window.getLoadedPage());

        Label head = new Label(window, "Create Alert");
        head.setFont(new Font(20, FontWeight.BOLD));

        title = new TextVal(window, "Alert title");
        body = new TextVal(window, "Alert description");
        location = new TextVal(window, "Alert location");
        severity = new ComboVal(window, "Alert severity");

        for (Severity sev : Severity.values()) {
            severity.getField().addItem(sev.toCombo(severity.getField()));
        }

        top.getChildren().add(head);
        center.getChildren().addAll(title, body, location, severity);

        done.setAction(() -> {
            alert = new Alert(new JSONObject());
            alert.setTitle(title.getValue());
            alert.setBody(body.getValue());
            alert.setLocation(location.getValue());
            alert.setSeverity(Integer.parseInt(severity.getField().getValue()));
            alert.setActive(true);
            hide();
        });

        applyStyle(window.getStyl());
    }

    public CreateAlert init(AlertRequest request) {
        title.setValue(request.getType() + " in " + request.getDetails());
        body.setPrompt("Alert description...");
        location.setValue(request.getDetails());
        severity.getField().setValue(request.getSeverity() + "");
        return this;
    }

    public Alert showForResult() {
        super.showAndWait();
        return alert;
    }
}
