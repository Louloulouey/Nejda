package com.sdidsa.nejda.view.app_content.auth.login;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateXAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.animation.view.scale.ScaleXYAnimation;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.button.Button;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.image.Image;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.overlay.MultipleOptionOverlay;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

import java.util.ArrayList;

public class Welcome extends Page {
    private final ArrayList<Image> images = new ArrayList<>() ;
    private final ArrayList<Image> big_images = new ArrayList<>() ;
    private final ArrayList<String> headers = new ArrayList<>();
    private final ArrayList<String> bodies = new ArrayList<>();

    private int loaded = 0;

    private final VBox root;

    private final StackPane img_container;
    private final Button next;
    private final Button skip;

    private final StackPane header_container;
    private final StackPane body_container;

    private final VBox bottom;

    private final StackPane big_container;

    public Welcome(Context owner) {
        super(owner);

        images.add(loadImage(R.drawable.illustration_03));
        images.add(loadImage(R.drawable.illustration_01));
        images.add(loadImage(R.drawable.illustration_04));
        images.add(loadImage(R.drawable.illustration_02));

        big_images.add(loadBigImage(R.drawable.illustration_03));
        big_images.add(loadBigImage(R.drawable.illustration_01));
        big_images.add(loadBigImage(R.drawable.illustration_04));
        big_images.add(loadBigImage(R.drawable.illustration_02));

        headers.add("Your Safety, Our Priority");
        headers.add("Stay Secure on Campus");
        headers.add("Real time alerts");
        headers.add("Welcome to Nejda");

        bodies.add("Welcome to Campus Safe, " +
                "your personal security assistant. Quickly alert campus police, " +
                "share your location, and receive instant alerts during emergencies. " +
                "Stay safe, informed, and connected on campus.");

        bodies.add("Instantly report incidents, access emergency resources, " +
                "and connect with safety personnel at the University.");

        bodies.add("help is never far away — giving you peace of mind, wherever you are");

        bodies.add("");

        img_container = new StackPane(owner);
        img_container.addView(images.get(0));
        img_container.setPadding(20);
        img_container.setClipToPadding(false);

        root = new VBox(owner);
        root.setAlignment(Alignment.TOP_CENTER);
        root.setSpacing(30);
        root.setClipChildren(false);

        header_container = new StackPane(owner);
        header_container.addCentered(loadHeader(headers.get(0)));

        body_container = new StackPane(owner);
        body_container.setClipToOutline(false);
        body_container.setClipToPadding(false);
        body_container.addAligned(loadBody(bodies.get(0)), Alignment.TOP_CENTER);
        body_container.setPadding(15);

        bottom = new VBox(owner);
        bottom.setPadding(20);

        next = new ColoredButton(owner, Style.ACCENT, Style.ONACCENT, "Next");
        next.setPadding(12);
        next.setFont(new Font(20, FontWeight.MEDIUM));
        next.setOnClick(this::nextImage);
        MarginUtils.setMarginBottom(next, owner, 10);

        skip = new ColoredButton(owner, Style.EMPTY, Style.TEXT_SEC, "Skip");
        skip.setPadding(12);
        skip.setElevation(0);
        skip.setFont(new Font(20, FontWeight.MEDIUM));
        skip.setOnClick(() -> {
            loaded = images.size() - 2;
            transition(1, true);
        });

        bottom.addViews(next, skip);

        root.addViews(img_container, header_container, body_container, bottom);

        SpacerUtils.spacer(body_container, Orientation.VERTICAL);

        big_container = new StackPane(owner);

        big_container.addView(big_images.get(0));

        addViews(big_container, root);

    }

    private Label loadHeader(String text) {
        Label header = new ColoredLabel(owner, Style.TEXT_NORM, text);
        header.setFont(new Font(30, FontWeight.SEMIBOLD));
        return header;
    }

    private Label loadBody(String text) {
        Label body = new ColoredLabel(owner, Style.TEXT_NORM, text);
        body.setFont(new Font(18));
        body.centerText();
        return body;
    }

    private Image loadImage(@DrawableRes int res) {
        Image img = new Image(owner, res);
        img.setSize(-1, 260);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        img.setCornerRadius(20);

        return img;
    }

    private Image loadBigImage(@DrawableRes int res) {
        Image big = new Image(owner, res);
        big.setAlpha(0.05f);
        big.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ImageView imageview = big.getView();
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageview.setColorFilter(filter);

        return big;
    }

    private void nextImage() {
        transition(1, false);
    }

    private void previousImage() {
        transition(-1, false);
    }
    private boolean onGoing = false;
    private synchronized void transition(int direction, boolean isSkip) {
        if(onGoing) return;
        onGoing = true;
        Image currentImage = (Image) img_container.getChildAt(0);
        Image currentBigImage = (Image) big_container.getChildAt(0);
        Label currentHeader = (Label) header_container.getChildAt(0);
        Label currentBody = (Label) body_container.getChildAt(0);
        loaded+= direction;
        Image nextImage = images.get(loaded);
        Image nextBigImage = big_images.get(loaded);
        Label nextHeader = loadHeader(headers.get(loaded));
        Label nextBody = loadBody(bodies.get(loaded));

        int sw = ContextUtils.getScreenWidth(owner);
        nextImage.setTranslationX(sw * direction);
        nextBigImage.setTranslationX(-sw * direction);
        nextHeader.setAlpha(0);
        nextBody.setAlpha(0);
        img_container.addView(nextImage);
        big_container.addView(nextBigImage);
        header_container.addCentered(nextHeader);
        body_container.addAligned(nextBody, Alignment.TOP_CENTER);

        new ParallelAnimation(400)
                .addAnimation(new TranslateXAnimation(currentImage, -sw * direction))
                .addAnimation(new AlphaAnimation(currentImage, 0))
                .addAnimation(new ScaleXYAnimation(currentImage, 1, 0.7f))
                .addAnimation(new TranslateXAnimation(nextImage, 0))
                .addAnimation(new AlphaAnimation(nextImage, 1))
                .addAnimation(new ScaleXYAnimation(nextImage, 0.7f, 1))
                .addAnimation(new TranslateXAnimation(currentBigImage, sw * direction))
                .addAnimation(new TranslateXAnimation(nextBigImage, 0))
                .addAnimation(Animation.fadeInUp(owner, 0, direction, nextHeader, nextBody))
                .addAnimation(Animation.fadeOutUp(owner, direction, currentHeader, currentBody))
                .setOnFinished(() -> {
                    img_container.removeView(currentImage);
                    big_container.removeView(currentBigImage);
                    header_container.removeView(currentHeader);
                    body_container.removeView(currentBody);
                    onGoing = false;
                })
                .setInterpolator(isSkip ? Interpolator.ANTICIPATE_OVERSHOOT : Interpolator.EASE_OUT)
                .start();

        if(loaded == images.size() - 1 && direction == 1) {
            next.setKey("Get Started");
            next.setOnClick(() -> ContextUtils.loadPage(owner, Login.class));

            int height = next.getHeight();

            new ParallelAnimation(400)
                    .addAnimation(new TranslateYAnimation(skip, height))
                    .addAnimation(new TranslateYAnimation(next, height))
                    .addAnimation(new AlphaAnimation(skip, 0))
                    .setOnFinished(() -> {
                        bottom.removeView(skip);
                        next.setTranslationY(0);
                    })
                    .setInterpolator(isSkip ? Interpolator.ANTICIPATE_OVERSHOOT : Interpolator.EASE_OUT)
                    .start();
        } else if(loaded == images.size() - 2 && direction == -1){
            next.setKey("Next");
            next.setOnClick(this::nextImage);

            int height = next.getHeight();
            next.setTranslationY(height);
            skip.setTranslationY(height);
            bottom.addView(skip);
            new ParallelAnimation(400)
                    .addAnimation(new TranslateYAnimation(skip,0))
                    .addAnimation(new TranslateYAnimation(next, 0))
                    .addAnimation(new AlphaAnimation(skip, 1))
                    .setInterpolator(isSkip ? Interpolator.ANTICIPATE_OVERSHOOT : Interpolator.EASE_OUT)
                    .start();
        }
    }

    @Override
    public boolean onBack() {
        if(loaded > 0) {
            previousImage();
        } else {

            MultipleOptionOverlay leave = new MultipleOptionOverlay(owner,
                    "You're leaving the app",
                    s -> s.equals("stay"));

            leave.addButton("leave",() -> {
                ContextUtils.finishAndRemoveTask(owner);
            });
            leave.addButton("stay", leave::hide);
            leave.show();
        }

        return true;
    }

    @Override
    public Animation destroy(int direction) {
        return new ParallelAnimation(400)
                .addAnimation(Animation.fadeOutUp(owner, img_container))
                .addAnimation(Animation.fadeOutDown(owner, next))
                .addAnimation(Animation.fadeOutScaleUp(header_container))
                .addAnimation(Animation.fadeOutScaleUp(body_container))
                .addAnimation(Animation.fadeOutScaleUp(big_container))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }

    @Override
    public Animation setup(int direction) {
        return new ParallelAnimation(400)
                .addAnimation(Animation.fadeInDown(owner, img_container))
                .addAnimation(Animation.fadeInUp(owner, next, skip))
                .addAnimation(Animation.fadeInScaleDown(header_container))
                .addAnimation(Animation.fadeInScaleDown(body_container))
                .addAnimation(Animation.fadeInScaleDown(big_container))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }

    @Override
    public void applyInsets(Insets insets) {
        if(insets == null) return;
        root.setPadding(0, insets.top, 0, insets.bottom);
    }
}
