package com.sdidsa.nejda.view.app_content.session.attachments;

import android.content.Context;
import android.widget.LinearLayout;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.controls.audio.AudioProxy;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

import java.io.File;

public class AudioAttachment extends BasicAttachment {
    private final File audioFile;

    private Runnable onRemove;

    public AudioAttachment(Context owner, File media) {
        super(owner);
        this.audioFile = media;
        setFill(Style.BACK_SEC);
        int size = SizeUtils.dipToPx(80, owner);
        setLayoutParams(new LinearLayout.LayoutParams(size, size));
        setCornerRadius(10);

        ColoredIcon mic = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.play, 48);
        mic.setPadding(10);

        setOnClickListener((e) -> {
            AudioPlayer player = AudioPlayer.getInstance(owner);
            player.setOnRemove(onRemove);
            player.show(audioFile);
        });

        addAligned(mic, Alignment.CENTER);
    }

    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    @Override
    public File toFile(String name) {
        return AudioProxy.saveTemp(audioFile, name);
    }
}
