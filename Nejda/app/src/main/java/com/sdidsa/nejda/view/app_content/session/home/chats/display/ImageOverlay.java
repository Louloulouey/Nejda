package com.sdidsa.nejda.view.app_content.session.home.chats.display;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.UiCache;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.scale.ScaleXYAnimation;
import com.sdidsa.nejda.abs.components.controls.image.ImageProxy;
import com.sdidsa.nejda.abs.components.controls.image.NetImage;
import com.sdidsa.nejda.abs.components.controls.image.ZoomageView;
import com.sdidsa.nejda.abs.components.controls.text.DateFormat;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.overlay.Overlay;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.model.Message;
import com.sdidsa.nejda.model.MessageAttachment;
import com.sdidsa.nejda.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class ImageOverlay extends Overlay {
    private static final ArrayList<ImageOverlay> cache = new ArrayList<>();

    public static ImageOverlay getInstance(Context owner) {
        cache.removeIf(inst -> inst.getOwner() != owner);

        ImageOverlay found = null;
        for (ImageOverlay inst : cache) {
            if (!inst.isAttachedToWindow()) {
                found = inst;
                break;
            }
        }

        if (found == null) {
            found = new ImageOverlay(owner);
            cache.add(found);
        }

        return found;
    }

    public static void clearCache() {
        cache.clear();
    }

    static {
        UiCache.register(ImageOverlay::clearCache);
    }

    private final ItemOverlayHeader controls;
    private final ZoomageView view;

    private final HBox others;

    private ImageOverlay(Context owner) {
        super(owner);
        VBox cont = new VBox(owner);
        cont.setSpacing(15);
        cont.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        cont.setPadding(20);
        cont.setClipChildren(true);

        cont.setScaleY(.75f);
        cont.setScaleX(.75f);
        cont.setAlpha(0);

        view = new ZoomageView(owner);
        view.setRestrictBounds(true);
        view.setOnScale((ov, nv) -> {
            if (ov > 1 && nv == 1) {
                showControls();
            } else if (ov == 1 && nv > 1) {
                hideControls();
            }
        });

        view.setOnSingleTap(() -> {
            if (controlsShown) {
                hideControls();
            } else {
                showControls();
            }
        });

        controls = new ItemOverlayHeader(owner);
        PaddingUtils.setPadding(controls, 15, 15, 10, 15, owner);
        controls.setFill(s -> App.adjustAlpha(s.getBackgroundTertiary(), .85f));
        controls.setOnClose(this::hide);
        controls.hideInfo();

        StackPane preCont = new ColoredStackPane(owner, Style.BACK_TER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.weight = 1;
        preCont.setLayoutParams(params);
        preCont.addView(view);
        preCont.addView(controls);

        preCont.setCornerRadius(15);
        preCont.setOutlineProvider(new OutlineProvider());
        preCont.setClipToOutline(true);

        others = new HBox(owner);
        HorizontalScrollView sv = new HorizontalScrollView(owner);
        sv.setHorizontalScrollBarEnabled(false);
        sv.addView(others);

        cont.addView(preCont);
        cont.addView(sv);

        addToShow(new ScaleXYAnimation(cont, 1));
        addToShow(new AlphaAnimation(cont, 1));

        addToHide(new ScaleXYAnimation(cont, .75f));
        addToHide(new AlphaAnimation(cont, 0));

        addView(cont);
    }

    private String loadedUrl;
    private final ArrayList<NetImage> otherImages = new ArrayList<>();

    private void load(Message post, String url) {
        loadedUrl = url;
        view.setImageResource(R.drawable.empty);
        ImageProxy.getImage(owner, url, bmp -> {
            view.setImageBitmap(bmp);
            Platform.waitWhile(() -> !view.isReady(), view::reset);
            showControls();
        });

        others.removeAllViews();
        otherImages.clear();
        String[] urls = post.getAttachments().stream()
                .filter(a -> a.getType().equalsIgnoreCase("image"))
                .map(MessageAttachment::getUrl)
                .toArray(String[]::new);
        if (urls.length > 1) {
            for (int i = 0; i < urls.length; i++) {
                String u = urls[i];
                NetImage o = new NetImage(owner, Style.BACK_TER);
                o.setImageThumbUrl(u, 72);
                o.setCornerRadius(15);
                o.setOnClick(() -> {
                    if (u.equalsIgnoreCase(loadedUrl)) return;
                    loadedUrl = u;
                    view.setImageResource(R.drawable.empty);
                    ImageProxy.getImage(owner, u, bmp -> {
                        view.setImageBitmap(bmp);
                        Platform.waitWhile(() -> !view.isReady(), view::reset);
                        showControls();
                    });
                    for (NetImage img : otherImages) {
                        img.setViewAlpha(img.getUrl().equalsIgnoreCase(u) ? 1 : 0.4f);
                    }
                    controls.setOnSave(() -> saveImage(u,
                            "message_" + indexOf(urls, u),
                            post.getCreatedAt()));
                });
                if (i > 0) {
                    MarginUtils.setMarginLeft(o, owner, 15);
                }
                others.addView(o);
                o.setViewAlpha(u.equalsIgnoreCase(url) ? 1 : 0.4f);
                otherImages.add(o);
            }
        }
        controls.setOnSave(() -> saveImage(url,
                "message_" + post.getId() + "_" + indexOf(urls, url),
                post.getCreatedAt()));

        controls.setTitleText(post.getSender().getFirstName());
    }

    private void load(User user) {
        String url = user.getPfp();
        loadedUrl = url;
        view.setImageResource(R.drawable.empty);
        ImageProxy.getImage(owner, url, bmp -> {
            view.setImageBitmap(bmp);
            Platform.waitWhile(() -> !view.isReady(), view::reset);
            showControls();
        });

        others.removeAllViews();
        otherImages.clear();

        controls.setOnSave(() -> saveImage(url,
                "pfp_" + user.getUserId(),
                LocalDateTime.now()));

        controls.setTitleText(user.getFirstName());
    }

    private int indexOf(String[] urls, String url) {
        for (int i = 0; i < urls.length; i++) {
            if (urls[i].equalsIgnoreCase(url)) {
                return i;
            }
        }
        return -1;
    }

    public void show(Message post, String url) {
        controls.showInfo(false);
        load(post, url);
        super.show();
    }

    public void show(User post) {
        controls.showInfo(false);
        load(post);
        super.show();
    }

    @Override
    public void show() {
        ErrorHandler.handle(new IllegalAccessError(
                        "can't show without loading a screen object, " +
                                "use show(ScreenshotResponse) instead"),
                "showing ScreenshotOverlay");
    }

    private boolean controlsShown = false;
    private Animation showControls;

    public void showControls() {
        if (controlsShown) return;
        if (showControls == null) {
            showControls = Animation.fadeInDown(owner, controls)
                    .setInterpolator(Interpolator.EASE_OUT);
        }
        controls.setAlpha(0);
        if (hideControls != null) hideControls.stop();
        controlsShown = true;
        showControls.start();
    }

    private Animation hideControls;

    public void hideControls() {
        if (!controlsShown) return;
        if (hideControls == null) {
            hideControls = Animation.fadeOutUp(owner, controls)
                    .setInterpolator(Interpolator.EASE_OUT);
        }
        if (showControls != null) showControls.stop();
        controlsShown = false;
        hideControls.start();
    }


    private void saveImage(String url, String context, LocalDateTime date) {
        saveImage(url, context, DateFormat.convertToDate(date));
    }

    private void saveImage(String url, String context, Date date) {
        ImageProxy.getImage(owner, url, bmp ->
                Platform.runBack(() -> {
                    int res = ImageProxy.saveImageToGallery(owner, bmp,
                            context + "_" + date.getTime());
                    switch (res) {
                        case ImageProxy.FILE_SAVED -> ContextUtils.toast(owner, "Image saved");
                        case ImageProxy.FILE_EXISTS -> ContextUtils.toast(owner,
                                "This image has already been saved");
                        case ImageProxy.FILE_ERROR -> ContextUtils.toast(owner,
                                "Something went wrong, retry later");
                    }
                }));
    }

    @Override
    public void applySystemInsets(Insets insets) {
        setPadding(insets.left, insets.top, insets.right, insets.bottom);
    }

    private class OutlineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(new Rect(0, 0,
                    view.getWidth(), view.getHeight()), SizeUtils.dipToPx(15, owner));
        }

    }
}
