package com.sdidsa.nejda.view.app_content.auth.login;

import android.Manifest;
import android.content.Context;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.button.Button;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.input.InputField;
import com.sdidsa.nejda.abs.components.controls.input.PasswordField;
import com.sdidsa.nejda.abs.components.controls.scratches.ColoredSeparator;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.scratches.Separator;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.overlay.PartialSlideOverlay;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.Store;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PermissionUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.controll.UserApi;
import com.sdidsa.nejda.view.app_content.session.home.Home;

public class Login extends Page {
    private final HBox top;
    private final VBox inputs;

    private final Button login;

    private final StackPane or;
    private final VBox pre_qr;

    private QrScanOverlay qrOverlay;

    private final InputField matricule;
    private final PasswordField password;


    public Login(Context owner) {
        super(owner);

        VBox root = new VBox(owner);
        root.setSpacing(30);

        top = new HBox(owner);
        top.setAlignment(Alignment.CENTER_LEFT);
        ColoredIcon back = new ColoredIcon(owner, Style.TEXT_NORM,
                R.drawable.arrow_left, 48);
        back.setImagePadding(10);
        back.setOnClick(this::onBack);
        ColoredLabel header = new ColoredLabel(owner, Style.TEXT_NORM, "Login");
        header.setFont(new Font(30, FontWeight.SEMIBOLD));
        MarginUtils.setMarginLeft(header, owner, 20);
        top.addViews(back, header);

        inputs = new VBox(owner);
        inputs.setSpacing(15);

        matricule = new InputField(owner, "Matricule");
        password = new PasswordField(owner, "Password");

        inputs.addViews(matricule, password);

        login = new ColoredButton(owner, Style.ACCENT, Style.ONACCENT, "Connect");
        login.setFont(new Font(22, FontWeight.MEDIUM));

        or = new StackPane(owner);

        Separator sep = new ColoredSeparator(owner, Orientation.HORIZONTAL, 10,
                Style.TEXT_SEC);

        ColoredLabel orText = new ColoredLabel(owner, Style.TEXT_SEC, Style.BACK_PRI, "or");
        orText.setFont(new Font(22));
        orText.setPadding(10);

        or.addCentered(sep);
        or.addCentered(orText);

        pre_qr = new ColoredVBox(owner, Style.BACK_TER);
        pre_qr.setAlignment(Alignment.TOP_CENTER);
        pre_qr.setSpacing(20);
        pre_qr.setPadding(30);
        pre_qr.setCornerRadius(30);

        StackPane qr_container = new StackPane(owner);

        ColoredIcon qr_inner = new ColoredIcon(owner, Style.ACCENT, R.drawable.qr_inner, 128);
        ColoredIcon qr_outer = new ColoredIcon(owner, Style.TEXT_SEC, R.drawable.qr_outer, 128);

        qr_container.addCentered(qr_inner);
        qr_container.addCentered(qr_outer);

        Label qr_label = new ColoredLabel(owner, Style.TEXT_NORM, "Scan your badge");
        qr_label.setFont(new Font(22, FontWeight.MEDIUM));

        Label qr_help = new ColoredLabel(owner, Style.TEXT_SEC, "Where do i find it?");
        qr_help.setFont(new Font(20));

        pre_qr.addView(qr_container);
        pre_qr.addView(qr_label);
        pre_qr.addView(qr_help);

        root.addViews(top);
        root.addView(inputs);
        root.addView(login);

        root.addView(or);

        root.addView(pre_qr);

        addView(root);

        qr_container.setOnClickListener(e ->
                PermissionUtils.requirePermissions(owner, () -> {
                    if(qrOverlay == null) qrOverlay = new QrScanOverlay(owner);
                    qrOverlay.show();
        }, Manifest.permission.CAMERA));

        qr_help.setOnClickListener(e -> {
            PartialSlideOverlay pso = new PartialSlideOverlay(owner, 0.5f) {
            };

            pso.show();
        });

        login.setOnClick(this::loginWithPassword);
    }

    public void loginWithMatricule(String matricule) {
        login.startLoading();
        UserApi.getUser(matricule, user -> {
            login.stopLoading();
            if(user != null) {
                UserApi.saveFirebaseToken(user);
                Store.setUserId(user.getUserId(), (id) -> {
                    TempStore.putUser(user);
                    ContextUtils.loadPage(owner, Home.class);
                });
            }
        });
    }

    public void loginWithPassword() {
        login.startLoading();
        matricule.hideError();
        password.hideError();
        UserApi.login(matricule.getValue(), password.getValue(), user -> {
            login.stopLoading();
            if(user != null) {
                UserApi.saveFirebaseToken(user);
                Store.setUserId(user.getUserId(), (id) -> {
                    TempStore.putUser(user);
                    ContextUtils.loadPage(owner, Home.class);
                });
            }else {
                matricule.setError("matricule or password incorrect");
                password.setError("matricule or password incorrect");
            }
        });
    }

    @Override
    public boolean onBack() {
        ContextUtils.loadPage(owner, Welcome.class);
        return true;
    }

    @Override
    public Animation setup(int direction) {
        return new ParallelAnimation(400)
                .addAnimation(Animation.fadeInDown(owner, top))
                .addAnimation(Animation.fadeInUp(owner, login, or))
                .addAnimation(Animation.fadeInScaleUp(inputs))
                .addAnimation(Animation.fadeInScaleUp(pre_qr))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }

    @Override
    public Animation destroy(int direction) {
        return new ParallelAnimation(400)
                .addAnimation(Animation.fadeOutUp(owner, top))
                .addAnimation(Animation.fadeOutDown(owner, login, or))
                .addAnimation(Animation.fadeOutScaleDown(inputs))
                .addAnimation(Animation.fadeOutScaleDown(pre_qr))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }

    @Override
    public void applyInsets(Insets insets) {
        int add = SizeUtils.dipToPx(20, owner);
        setPadding(insets.left + add,
                insets.top + add,
                insets.right + add,
                add);
    }
}
