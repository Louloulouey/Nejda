package com.sdidsa.nejda.view.app_content.session.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.graphics.Insets;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.components.Page;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;
import com.sdidsa.nejda.view.app_content.session.home.Home;

public class ContactsPage extends Page {

    private final VBox root;

    private final HBox top;

    private final Label descriptionLabel;

    private final VBox contactsContainer;

    private final Label helpInfoLabel;

    private final ColoredVBox helpInfoContainer;


    public ContactsPage(Context owner) {
        super(owner);

        root = new VBox(owner);
        root.setPadding(15);

        top = new HBox(owner);
        top.setPadding(10);
        top.setAlignment(Alignment.CENTER_LEFT);
        ColoredIcon back = new ColoredIcon(owner, Style.TEXT_NORM,
                R.drawable.arrow_left, 48);
        back.setImagePadding(11);
        back.setOnClick(this::onBack);
        ColoredLabel header = new ColoredLabel(owner, Style.TEXT_NORM, "Call for Help");
        header.setFont(new Font(24, FontWeight.MEDIUM));
        MarginUtils.setMarginLeft(header, owner, 20);
        top.addViews(back, header);

        descriptionLabel = new ColoredLabel(owner, Style.TEXT_NORM, "Emergency Contacts");
        descriptionLabel.setFont(new Font(22, FontWeight.MEDIUM));
        descriptionLabel.setPadding(15);

        // Emergency contacts section
        contactsContainer = new VBox(owner);
        contactsContainer.setPadding(10);
        contactsContainer.setSpacing(15);

        // Add emergency contacts
        addEmergencyContact(owner, contactsContainer, "Emergency Services", "911",
                R.drawable.danger);
        addEmergencyContact(owner, contactsContainer, "Police Department", "1548",
                R.drawable.police);
        addEmergencyContact(owner, contactsContainer, "Fire Department", "14",
                R.drawable.fire);
        addEmergencyContact(owner, contactsContainer, "Campus Medics", "0650708496",
                R.drawable.hospital);

        // Description section
        helpInfoLabel = new ColoredLabel(owner, Style.TEXT_NORM, "Contact Information");
        helpInfoLabel.setFont(new Font(22, FontWeight.MEDIUM));
        helpInfoLabel.setPadding(15);

        // Help information container
        helpInfoContainer = new ColoredVBox(owner, Style.EMPTY);
        helpInfoContainer.setClipToOutline(true);
        helpInfoContainer.setPadding(10);

        ColoredLabel helpInfoText = new ColoredLabel(owner, Style.TEXT_NORM,
            "In case of emergency, tap on one of the contacts above to immediately call for help." +
            "\n\nThese emergency numbers will connect you directly to the respective services." +
            "\n\nPlease only use these numbers in genuine emergency situations.");
        helpInfoText.setFont(new Font(16));
        helpInfoText.setCornerRadius(CornerUtils.DEFAULT);
        helpInfoText.setBack(Style.BACK_TER);
        helpInfoText.setPadding(15);
        
        helpInfoContainer.addViews(helpInfoText);

        root.addViews(top, descriptionLabel, contactsContainer, helpInfoLabel, helpInfoContainer);
        
        SpacerUtils.spacer(root, -1, -1);
        
        addView(root);
    }

    private void addEmergencyContact(Context owner, VBox container, String name, String number, int icon) {
        ColoredVBox contactItem = new ColoredVBox(owner, Style.BACK_TER);
        contactItem.setClipToOutline(true);
        contactItem.setPadding(15);
        contactItem.setCornerRadius(CornerUtils.DEFAULT);
        
        HBox contactHeader = new HBox(owner);
        contactHeader.setAlignment(Alignment.CENTER_LEFT);
        
        ColoredIcon contactIcon = new ColoredIcon(owner, Style.TEXT_NORM, icon, 48);
        contactIcon.setImagePadding(11);
        
        VBox contactInfo = new VBox(owner);
        Label contactName = new ColoredLabel(owner, Style.TEXT_NORM, name);
        contactName.setFont(new Font(18, FontWeight.MEDIUM));
        SpacerUtils.spacer(contactInfo, -2, -2);
        
        Label contactNumber = new ColoredLabel(owner, Style.TEXT_SEC, number);
        contactNumber.setFont(new Font(16));
        
        contactInfo.addViews(contactName, contactNumber);
        
        ColoredIcon callButton = new ColoredIcon(owner, Style.ACCENT, R.drawable.call, 48);
        callButton.setPadding(12);
        contactItem.setOnClickListener((v) -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            owner.startActivity(intent);
        });
        
        MarginUtils.setMarginLeft(contactInfo, owner, 15);
        
        contactHeader.addViews(contactIcon, contactInfo, callButton);
        contactItem.addViews(contactHeader);
        
        container.addViews(contactItem);
    }

    @Override
    public boolean onBack() {
        ContextUtils.loadPage(owner, Home.class);
        return true;
    }

    @Override
    public Animation setup(int direction) {
        return new ParallelAnimation(400)
                .addAnimation(Animation.fadeInDown(owner, top, descriptionLabel, contactsContainer))
                .addAnimation(Animation.fadeInUp(owner, helpInfoContainer, helpInfoLabel))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }

    @Override
    public Animation destroy(int direction) {
        return new ParallelAnimation(400)
                .addAnimation(Animation.fadeOutUp(owner, top, descriptionLabel, contactsContainer))
                .addAnimation(Animation.fadeOutDown(owner, helpInfoContainer, helpInfoLabel))
                .setInterpolator(Interpolator.ANTICIPATE_OVERSHOOT);
    }

    @Override
    public void applyInsets(Insets insets) {
        root.setPadding(15, 15 + insets.top, 15, 15 + insets.bottom);
    }
} 