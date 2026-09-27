package com.sdidsa.nejda.abs.components.layout.overlay.media.medialist;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sdidsa.nejda.abs.data.property.ObservableArrayList;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.data.media.Media;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MediaList extends RecyclerView {
    public static final int SPAN_COUNT = 3;
    private final MediaAdapter adapter;

    private Consumer<List<Media>> onData;

    private final ObservableArrayList<Media> selected;

    public MediaList(Context owner) {
        this(owner, null, null);
    }

    public MediaList(Context owner, ObservableArrayList<Media> selected, Property<Boolean> single) {
        super(owner);
        this.selected = selected;

        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(true);

        GridLayoutManager lm = new GridLayoutManager(owner, SPAN_COUNT);
        lm.setSmoothScrollbarEnabled(true);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(lm);

        int size = ContextUtils.getScreenWidth(owner) - SizeUtils.dipToPx(50, owner);
        adapter = new MediaAdapter(owner, size, selected, single);
        setAdapter(adapter);

        GradientDrawable clip = new GradientDrawable();
        clip.setCornerRadius(SizeUtils.dipToPx(12, owner));
        setBackground(clip);
        setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        setClipToOutline(true);

        setClipToPadding(false);
    }

    public void setOnData(Consumer<List<Media>> onData) {
        this.onData = onData;
    }

    public void setData(List<Media> data, boolean single) {
        adapter.setData(data);
        scrollToPosition(0);
        List<Media> fs = data.stream().filter(selected::contains).collect(Collectors.toList());
        selected.setAll(fs);

        if(onData != null) {
            onData.accept(data);
        }
    }
}