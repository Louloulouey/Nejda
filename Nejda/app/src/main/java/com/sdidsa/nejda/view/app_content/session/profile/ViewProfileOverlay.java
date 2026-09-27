package com.sdidsa.nejda.view.app_content.session.profile;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.UiCache;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.image.ImageProxy;
import com.sdidsa.nejda.abs.components.controls.image.NetImage;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.DateFormat;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.overlay.PartialSlideOverlay;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.TempStore;
import com.sdidsa.nejda.abs.utils.view.AlignUtils;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.LocaleUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.controll.UserApi;
import com.sdidsa.nejda.model.User;
import com.sdidsa.nejda.view.Nejda;
import com.sdidsa.nejda.view.app_content.session.home.chats.display.ImageOverlay;

import java.util.ArrayList;

public class ViewProfileOverlay extends PartialSlideOverlay {
    private static final ArrayList<ViewProfileOverlay> cache = new ArrayList<>();

    public static ViewProfileOverlay getInstance(Context owner) {
        cache.removeIf(inst -> inst.getOwner() != owner);

        ViewProfileOverlay found = null;
        for (ViewProfileOverlay inst : cache) {
            if (!inst.isAttachedToWindow()) {
                found = inst;
                break;
            }
        }

        if (found == null) {
            found = new ViewProfileOverlay(owner);
            cache.add(found);
        }

        return found;
    }

    public static void clearCache() {
        cache.clear();
    }

    static {
        UiCache.register(ViewProfileOverlay::clearCache);
    }

    private final Label initials;
    private final NetImage pfp;
    private final Label username;
    private final UserInfo matricule;
    private final UserInfo birthdate;

    private final ColoredIcon editPfp;

    private String uid;

    private ViewProfileOverlay(Context owner) {
        super(owner, 0.7);

        StackPane root = new StackPane(owner);
        root.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));

        ColoredStackPane topBack = new ColoredStackPane(owner, Style.BACK_TER);
        topBack.setCornerRadiusTop(20);
        topBack.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, SizeUtils.dipToPx(180, owner)));

        ColoredStackPane avatarBack = new ColoredStackPane(owner, Style.BACK_PRI);
        int size = SizeUtils.dipToPx(180, owner);
        avatarBack.setCornerRadius(128);
        avatarBack.setLayoutParams(new FrameLayout.LayoutParams(size, size));
        avatarBack.setY(SizeUtils.dipToPx(40, owner));

        root.addView(topBack);
        root.addView(avatarBack);
        AlignUtils.alignInFrame(avatarBack, Alignment.TOP_CENTER);

        StackPane avatar = new ColoredStackPane(owner, Style.BACK_TER);
        avatar.setCornerRadius(160);
        int avsize = SizeUtils.dipToPx(160, owner);
        avatar.setLayoutParams(new LayoutParams(avsize, avsize));
        avatar.setY(SizeUtils.dipToPx(50, owner));

        pfp = new NetImage(owner);
        pfp.setSize(160);
        pfp.setCornerRadius(160);
        pfp.setAlpha(0);

        initials = new ColoredLabel(owner, Style.ACCENT, "");
        initials.setFont(new Font(48, FontWeight.SEMIBOLD));
        initials.setAlpha(0);

        avatar.addCentered(pfp);
        avatar.addCentered(initials);

        editPfp = new ColoredIcon(owner, Style.TEXT_SEC, R.drawable.camera, 48);
        editPfp.setPadding(10);

        editPfp.setOnClick(this::editPfp);

        HBox top = new HBox(owner);
        PaddingUtils.setPaddingTop(top, 110, owner);

        root.addView(avatar);
        AlignUtils.alignInFrame(avatar, Alignment.TOP_CENTER);

        VBox ui = new VBox(owner);
        ui.setPadding(15);
        ui.setSpacing(15);
        ui.setAlignment(Alignment.TOP_CENTER);

        username = new ColoredLabel(owner, Style.TEXT_NORM, "");
        username.setFont(new Font(24));
        username.centerText();

        matricule = new UserInfo(owner, "Matricule");
        birthdate = new UserInfo(owner, "Date of birth", true);

        MarginUtils.setMarginTop(username, owner, 40);
        MarginUtils.setMarginTop(matricule, owner, 20);

        top.addView(editPfp);
        ui.addView(top);
        ui.addView(username);
        ui.addView(matricule);
        ui.addView(birthdate);

        root.addView(ui);

        list.addView(root);
    }

    private void editPfp() {
        ContextUtils.pickImage(owner, media -> {
            initials.setAlpha(0);
            pfp.setAlpha(1);
            pfp.startLoading();
            ImageProxy.getThumbnail(owner, media.uri(),
                    SizeUtils.dipToPx(160, owner),
                    bmp -> Nejda.uploadAttachment(ImageProxy.saveTemp(bmp, "pfp_" + uid),
                            url -> UserApi.setPfp(url,
                                    res -> pfp.setImageThumbUrl(url, 160))),
                    () -> ContextUtils.toast(owner, "something went wrong"));
        });
    }

    public void show(String uid) {
        UserApi.getUser(uid, this::show);
    }

    public void show(User user) {
        this.loadUserInfo(user);
        super.show();
    }

    @Override
    public void show() {
        ErrorHandler.handle(new IllegalAccessError(
                        "can't show without setting user_id, " +
                                "use show(String) or show(String, BondState) instead"),
                "showing ViewProfileOverlay");
    }

    private void loadUserInfo(User user) {
        if(user.getUserId().equalsIgnoreCase(TempStore.getUser().getUserId())) {
            editPfp.setVisibility(VISIBLE);
        } else {
            editPfp.setVisibility(INVISIBLE);
        }
        uid = user.getUserId();
        if (user.getPfp() != null && !user.getPfp().equalsIgnoreCase("null")) {
            pfp.setImageThumbUrl(user.getPfp(), 160);

            pfp.setOnClick(() -> {
                ImageOverlay.getInstance(owner).show(user);
            });


            initials.setAlpha(0);
            pfp.setAlpha(1);
        } else {
            initials.setText(user.getFirstName().substring(0, 1));
            initials.setAlpha(1);
            pfp.setAlpha(0);
        }
        username.setKey(user.getFirstName() + " " + user.getLastName());
        matricule.setValue(user.getUserId());
        birthdate.setValue(
                DateFormat.FULL_SHORT.format(LocaleUtils.getLocale(owner).get(),
                        user.getBirthDate()));
    }

    private static class UserInfo extends HBox {
        private final ColoredLabel value;

        private final boolean keyed;

        public UserInfo(Context owner, String titleString) {
            this(owner, titleString, false);
        }

        public UserInfo(Context owner, String titleString, boolean keyed) {
            super(owner);
            this.keyed = keyed;
            setCornerRadius(15);
            setPadding(5);
            setAlignment(Alignment.TOP_LEFT);

            setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            ColoredLabel title = new ColoredLabel(owner, Style.TEXT_SEC, titleString);
            title.setLines(1);
            title.setFont(new Font(18, FontWeight.MEDIUM));

            value = new ColoredLabel(owner, Style.TEXT_NORM, "");
            value.setFont(new Font(16));

            VBox labels = new VBox(owner);
            labels.addViews(title, value);
            labels.setSpacing(5);
            SpacerUtils.spacer(labels);
            addViews(labels);
        }

        public void setValue(String value) {
            Platform.runLater(() -> {
                if (keyed) {
                    this.value.setKey(value);
                } else {
                    this.value.setText(value);
                }
            });
        }
    }
}
