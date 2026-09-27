package org.luke.nejda.app.home.right.emergency.requests.open;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import org.luke.controll.AlertApi;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.controls.space.ExpandingVSpace;
import org.luke.gui.controls.space.FixedHSpace;
import org.luke.gui.factory.Backgrounds;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;
import org.luke.model.Alert;
import org.luke.model.AlertRequest;
import org.luke.model.Severity;
import org.luke.nejda.app.home.right.emergency.CreateAlert;
import org.luke.nejda.ui.Button;

import java.util.ArrayList;

public class OpenAlertRequest extends VBox implements Styleable {
    private final ArrayList<Label> headers = new ArrayList<>();

    private final Label name;
    private final Label matricule;

    private final ColorIcon icon;

    private final Button deny;
    private final Button accept;

    public OpenAlertRequest(Window window, AlertRequest request, Runnable close) {
        setPadding(new Insets(15));
        setSpacing(10);

        VBox.setVgrow(this, Priority.ALWAYS);

        icon = new ColorIcon(request.getReportedBy().getPfp(), 38, false);
        icon.noColor();
        icon.round(-1);

        HBox user = new HBox();
        user.setSpacing(15);

        VBox texts = new VBox();
        texts.setSpacing(3);
        texts.setAlignment(Pos.CENTER_LEFT);

        name = new Label(window, request.getReportedBy().getFirstName() + " " +
                request.getReportedBy().getLastName());
        name.setFont(new Font(14, FontWeight.BOLD));
        matricule = new Label(window, request.getReportedBy().getUserId());
        matricule.setFont(new Font(12, FontWeight.MEDIUM));

        texts.getChildren().addAll(name, matricule);

        user.getChildren().addAll(icon, texts);

        HBox line1 = new HBox();
        line1.setSpacing(10);

        TextDetail type = new TextDetail(window, "Type", request.getType());
        DateDetail date = new DateDetail(window, "Create At", request.getCreatedAt());

        HBox line2 = new HBox();
        line2.setSpacing(10);

        TextDetail severity = new TextDetail(window, "Severity", Severity.bySeverity(request.getSeverity())
                .getText());
        TextDetail location = new TextDetail(window, "Location", request.getDetails());

        line1.getChildren().addAll(type, severity);
        line2.getChildren().addAll(location, date);

        HBox attachments = new HBox();
        attachments.setSpacing(10);

        request.getAttachments().forEach(att -> {
            System.out.println(att.getUrl());
            if(att.getUrl().endsWith(".jpg")) {
                attachments.getChildren().add(new ImageAttachment(window, att));
            } else {
                attachments.getChildren().add(new VoiceAttachment(window, att));
            }
        });

        HBox buttons = new HBox();
        buttons.setSpacing(10);

        deny = new Button(window, "Deny");
        deny.add(
                new ColoredIcon(window, "delete", 18, 16, Style::getTextDanger),
                new FixedHSpace(10)
        );
        deny.setAction(()-> {
            deny.startLoading();
            AlertApi.treat(request, re -> {
                deny.stopLoading();
                if(close != null) close.run();
            });
        });
        accept = new Button(window, "Accept");
        accept.add(
                new ColoredIcon(window, "done", 24, 18, Style::getTextPositive),
                new FixedHSpace(10)
        );
        accept.setAction(() -> {
            Alert alert = new CreateAlert(window)
                    .init(request)
                    .showForResult();
            if(alert == null) return;
            accept.startLoading();
            AlertApi.createAlert(alert, resp -> {
                AlertApi.treat(request, re -> {
                    accept.stopLoading();
                    if(close != null) close.run();
                });
            });
        });
        HBox.setHgrow(deny, Priority.ALWAYS);
        HBox.setHgrow(accept, Priority.ALWAYS);

        deny.prefWidthProperty().bind(widthProperty().subtract(40).divide(2));
        accept.prefWidthProperty().bind(widthProperty().subtract(40).divide(2));

        buttons.getChildren().addAll(deny, accept);

        //ADD TO ROOT
        addHeader(window, "Request details",
                new ColoredIcon(window, "info", 24, 16, Style::getHeaderPrimary));
        getChildren().addAll(line1, line2);

        addHeader(window, "Attachments",
                new ColoredIcon(window, "attach", 24, 16, Style::getHeaderPrimary));

        getChildren().add(attachments);

        getChildren().add(new ExpandingVSpace());

        addHeader(window, "Requested by",
                new ColoredIcon(window, "user", 24, 16, Style::getHeaderPrimary));
        getChildren().add(user);

        getChildren().add(buttons);

        applyStyle(window.getStyl());
    }

    public void addHeader(Window win, String s, Node... nodes) {
        Label lab = new Label(win, s, new Font(Font.DEFAULT_FAMILY_MEDIUM, 14));
        headers.add(lab);

        HBox hbox = new HBox(20, nodes);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().add(0, new ExpandingHSpace());
        hbox.getChildren().add(0, lab);
        hbox.setOpacity(0.7);

        getChildren().addAll(hbox);
    }

    @Override
    public void applyStyle(Style style) {
        name.setFill(style.getTextNormal());
        matricule.setFill(style.getTextNormal());
        icon.setBackground(Backgrounds.make(style.getBackgroundTertiary(), 8));
        headers.forEach(h -> h.setFill(style.getHeaderPrimary()));

        deny.setFill(style.getBackgroundTertiary());
        accept.setFill(style.getBackgroundTertiary());

        deny.setTextFill(style.getTextDanger());
        accept.setTextFill(style.getTextPositive());

        setBackground(Backgrounds.make(style.getBackgroundSecondary(), 10));
    }

    @Override
    public void applyStyle(ObjectProperty<Style> style) {
        Styleable.bindStyle(this, style);
    }
}
