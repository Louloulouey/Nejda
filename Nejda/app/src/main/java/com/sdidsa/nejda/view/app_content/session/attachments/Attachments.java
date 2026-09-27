package com.sdidsa.nejda.view.app_content.session.attachments;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewOutlineProvider;
import android.widget.HorizontalScrollView;

import com.sdidsa.nejda.abs.components.controls.image.ImageProxy;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.data.media.Media;
import com.sdidsa.nejda.abs.data.observable.ListChangeListener;
import com.sdidsa.nejda.abs.data.property.ObservableArrayList;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.view.Nejda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Attachments extends HorizontalScrollView {
    private final Context owner;
    private final HBox root;

    private final StyleToColor fill;

    private final ObservableArrayList<BasicAttachment> media;

    public Attachments(Context owner, StyleToColor fill) {
        super(owner);
        this.fill = fill;
        setHorizontalScrollBarEnabled(false);

        setClipToOutline(true);

        setOutlineProvider(ViewOutlineProvider.BOUNDS);

        this.owner = owner;
        this.media = new ObservableArrayList<>();

        root = new HBox(owner);
        PaddingUtils.setPadding(root, 0, 10, 10, 0, owner);
        addView(root);
    }

    public void add(Bitmap bmp) {
        setVisibility(VISIBLE);
        Platform.runBack(() -> {
            ImageAttachment img = new ImageAttachment(owner, bmp);
            img.setFill(fill);
            MarginUtils.setMarginLeft(img, owner, 10);
            img.setOnClickListener(v -> img.hide(() -> {
                media.remove(img);
                root.removeView(img);
                if(media.isEmpty()) setVisibility(GONE);
            }));
            media.add(img);
            Platform.runLater(() -> root.addView(img));
        });
    }

    public void addAudio(File bmp) {
        setVisibility(VISIBLE);

        Platform.runBack(() -> {
            AudioAttachment att = new AudioAttachment(owner, bmp);
            att.setFill(fill);
            MarginUtils.setMarginLeft(att, owner, 10);
            att.setOnRemove(() -> att.hide(() -> {
                media.remove(att);
                root.removeView(att);
                if(media.isEmpty()) setVisibility(GONE);
            }));
            media.add(att);
            Platform.runLater(() -> root.addView(att));
        });
    }

    public void add(Media media) {
        Platform.runBack(() -> {
            Bitmap bmp = ImageProxy.decode(owner, media.uri());
            Platform.runLater(() -> add(bmp));
        });
    }

    public void add(Media...medias) {
        for(Media media : medias) {
            add(media);
        }
    }

    public void addListener(ListChangeListener<BasicAttachment> onChange) {
        media.addListener(onChange);
    }

    public boolean isEmpty() {
        return media.isEmpty();
    }

    public List<UploadedAttachment> uploadAll(List<BasicAttachment> media, String prefix, long id) {
        ArrayList<UploadedAttachment> res = new ArrayList<>();

        media = media == null ? this.media : media;

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < media.size(); i++) {
            BasicAttachment att = media.get(i);
            File file = att.toFile(prefix + "_" + id
                    + "_" + i);
            Thread thread = new Thread(() -> {
                Semaphore sem = new Semaphore(0);
                Nejda.uploadAttachment(file, url -> {
                    String type = "unknown";
                    if(att instanceof AudioAttachment) {
                        type = "audio";
                    } else if(att instanceof ImageAttachment) {
                        type = "image";
                    }
                    res.add(new UploadedAttachment(url, type));
                    sem.release();
                });
                sem.acquireUninterruptibly();
            });
            threads.add(thread);
        }

        threads.forEach(Thread::start);
        threads.forEach(this::join);

        return res;
    }

    public ObservableArrayList<BasicAttachment> getMedia() {
        return media;
    }

    private void join(Thread t) {
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        media.clear();
        setVisibility(GONE);
        root.removeAllViews();
    }
}
