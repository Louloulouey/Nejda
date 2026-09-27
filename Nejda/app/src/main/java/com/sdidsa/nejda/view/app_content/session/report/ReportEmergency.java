package com.sdidsa.nejda.view.app_content.session.report;

import android.Manifest;
import android.content.Context;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.audio.AudioRecorder;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.image.ColorIcon;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.input.InputField;
import com.sdidsa.nejda.abs.components.controls.input.MinimalInputField;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.overlay.RadioOverlay;
import com.sdidsa.nejda.abs.data.media.Media;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.abs.utils.view.AlignUtils;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PermissionUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.controll.AlertApi;
import com.sdidsa.nejda.model.AlertRequest;
import com.sdidsa.nejda.model.AlertRequestImage;
import com.sdidsa.nejda.view.app_content.session.attachments.Attachments;
import com.sdidsa.nejda.view.app_content.session.attachments.CaptureImage;
import com.sdidsa.nejda.view.app_content.session.attachments.UploadedAttachment;
import com.sdidsa.nejda.view.app_content.session.home.Home;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class ReportEmergency extends Page {

    private final InputField typeInput;
    private final MinimalInputField add;

    private final VBox root;

    private Severity severity;

    private final ColoredLabel severityVal;
    private final ColoredButton severityButton;

    private final Attachments attachments;

    private final ColoredIcon mic;

    private final RadioOverlay sevov;

    private final RadioOverlay typov;

    public ReportEmergency(Context owner) {
        super(owner);

        root = new VBox(owner);
        root.setSpacing(15);
        root.setPadding(15);

        HBox top = new HBox(owner);
        top.setAlignment(Alignment.CENTER_LEFT);
        ColoredIcon back = new ColoredIcon(owner, Style.TEXT_NORM,
                R.drawable.arrow_left, 48);
        back.setImagePadding(11);
        back.setOnClick(this::onBack);
        ColoredLabel header = new ColoredLabel(owner, Style.TEXT_NORM, "Report Emergency");
        header.setFont(new Font(24, FontWeight.MEDIUM));
        MarginUtils.setMarginLeft(header, owner, 20);
        top.addViews(back, header);

        severityButton = new ColoredButton(owner, Style.BACK_TER,
                Style.TEXT_NORM, "Severity");
        severityButton.setFont(new Font(20));

        severityVal = new ColoredLabel(owner, Style.TEXT_NORM, "");
        severityVal.setFont(new Font(20));

        ColorIcon severityIcon = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.info, 24);
        MarginUtils.setMarginRight(severityIcon, owner, 15);
        severityButton.addPreLabel(severityIcon);
        severityButton.addPostLabel(SpacerUtils.spacer(owner, Orientation.HORIZONTAL));
        severityButton.addPostLabel(severityVal);

        sevov = new RadioOverlay(owner, "Emergency Severity");

        for (Severity sev : Severity.values()) {
            sevov.addButton(sev.getText(), sev.getColor());
        }

        sevov.removeSave();

        sevov.setOnSave(value -> {
            Severity sev = Severity.valueOf(value.toUpperCase());
            this.severity = sev;
            severityVal.setKey(value);
            severityVal.setFill(sev.getColor());
            severityButton.setTextFill(sev.getColor());
            sevov.hide();
        });

        severityButton.setOnClick(sevov::show);

        typeInput = new InputField(owner, "Emergency type");
        typeInput.setBackColor(Style.BACK_TER);
        //PaddingUtils.setPadding(type.getInput(), 7, 15, 7, 15, owner);
        typeInput.setFont(new Font(20));
        typeInput.setEditable(false);

        typov = new RadioOverlay(owner, "Emergency Type");

        for (Type type : Type.values()) {
            typov.addButton(type.getText());
        }

        typov.removeSave();

        typov.setOnSave(value -> {
            Type type = Type.byText(value);
            assert type != null;
            typeInput.setValue(type.getText());
            typov.hide();
        });

        Runnable onTypeClicked = typov::show;

        typeInput.setOnClickListener(v -> {
            onTypeClicked.run();
        });

        typeInput.getPrompts().setOnClickListener(v -> {
            onTypeClicked.run();
        });


        Label addLabel = new ColoredLabel(owner, Style.TEXT_NORM, "Location and Addition details");
        addLabel.setFont(new Font(22, FontWeight.MEDIUM));
        MarginUtils.setMarginLeft(addLabel, owner, 10);

        VBox preAdd = new ColoredVBox(owner, Style.BACK_TER);
        preAdd.setClipToOutline(true);
        preAdd.setPadding(10);
        preAdd.setCornerRadius(CornerUtils.DEFAULT);

        add = new MinimalInputField(owner, "enter addition information");
        add.setBackFill(Style.EMPTY);
        add.setMultiline(5);
        add.getPreInput().setAlignment(Alignment.TOP_LEFT);
        AlignUtils.alignInFrame(add.getPreInput(), Alignment.TOP_LEFT);

        add.setFont(new Font(20));
        SpacerUtils.spacer(add, -1, -1);
        SpacerUtils.spacer(preAdd, -1, -1);
        SpacerUtils.spacer(root, -1, -1);

        attachments = new Attachments(owner, Style.BACK_SEC);

        HBox attach = new HBox(owner);
        attach.setClipToOutline(true);
        attach.setAlignment(Alignment.BOTTOM_RIGHT);

        CaptureImage ci = new CaptureImage(owner);
        ci.setOnCapture(bmp -> {
            ci.hide();
            attachments.add(bmp);
        });

        ColoredIcon gallery = new ColoredIcon(owner, Style.TEXT_SEC, R.drawable.photo_video, 48);
        ColoredIcon camera = new ColoredIcon(owner, Style.TEXT_SEC, R.drawable.camera, 48);
        mic = new ColoredIcon(owner, Style.TEXT_SEC, R.drawable.mic_fill, 48);

        SendReport send = new SendReport(owner);
        send.setFocusable(true);

        gallery.setPadding(11);
        camera.setPadding(11);
        mic.setPadding(11);

        gallery.setOnClick(() -> {
            ContextUtils.pickImages(owner, media -> {
                attachments.add(media.toArray(new Media[0]));
            });
        });

        camera.setOnClick(() -> {
            PermissionUtils.requirePermissions(owner, ci::show, Manifest.permission.CAMERA);
        });

        mic.setOnClick(this::startRecording);

        attach.addViews(gallery, camera, mic, send);

        preAdd.addViews(add, attachments, attach);

        send.setOnClick(() -> {
            if (this.severity == null || send.isLoading()) return;

            send.startLoading();

            Platform.runBack(() -> {
                AlertRequest request = new AlertRequest(new JSONObject());
                request.setSeverity(this.severity.getSeverityValue());
                request.setType(typeInput.getValue());
                request.setDetails(add.getValue());
                request.setReportedBy(TempStore.getUser());
                AlertApi.requestAlert(request, response -> {
                    Platform.runBack(() -> {
                        List<UploadedAttachment> urls = attachments.uploadAll(null, "request", response.getId().intValue());
                        ArrayList<Thread> saveImages = new ArrayList<>();
                        for (UploadedAttachment url : urls) {
                            Thread t = new Thread(() -> {
                                AlertRequestImage image = new AlertRequestImage(new JSONObject());
                                image.setAlertRequest(response);
                                image.setUrl(url.url());
                                Semaphore sem = new Semaphore(0);
                                AlertApi.saveRequestAttachment(image, res -> {
                                    sem.release();
                                });
                                sem.acquireUninterruptibly();
                            });
                            saveImages.add(t);
                        }

                        saveImages.forEach(Thread::start);
                        saveImages.forEach(this::join);

                        Platform.runLater(() -> {
                            ContextUtils.toast(owner, "Your report was submitted");
                            ContextUtils.loadPage(owner, Home.class, send::stopLoading);
                            Page.clearCache(ReportEmergency.class);
                        });
                    });
                });
            });
        });

        root.addViews(top, severityButton, typeInput, addLabel, preAdd);

        addView(root);
    }

    public void startRecording() {
        AudioRecorder recorder = new AudioRecorder();
        recorder.startRecording(owner, () -> {
            mic.setFill(Style.TEXT_POS);
            mic.setOnClick(() -> {
                recorder.stopRecording(attachments::addAudio);
                resetMic();
            });
        });
    }

    public void resetMic() {
        mic.setFill(Style.TEXT_SEC);
        mic.setOnClick(this::startRecording);
    }

    private void join(Thread t) {
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onBack() {
        ContextUtils.loadPage(owner, Home.class);
        return true;
    }

    @Override
    public Animation setup(int direction) {
        root.setAlpha(0);
        return Animation.fadeInUp(owner, root)
                .setDuration(400)
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();
    }

    @Override
    public Animation destroy(int direction) {
        return Animation.fadeOutDown(owner, root)
                .setDuration(400)
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT)
                .start();
    }

    @Override
    public void applyInsets(Insets insets) {
        setPadding(insets.left, insets.top, insets.right, insets.bottom);
    }

    public static void load(Context owner, int severity, String type, String location) {
        Platform.runBack(() -> {
            ReportEmergency instance = Page.getInstance(owner, ReportEmergency.class);
            Platform.runLater(() -> {
                instance.sevov.select(Objects.requireNonNull(Severity.bySeverity(severity)).getText());
                instance.typeInput.setValue(type);
                instance.add.setValue(location);
            });
        });
    }
}
