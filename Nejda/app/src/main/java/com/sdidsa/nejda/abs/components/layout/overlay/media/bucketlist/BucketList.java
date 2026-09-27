package com.sdidsa.nejda.abs.components.layout.overlay.media.bucketlist;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sdidsa.nejda.abs.data.media.Bucket;

import java.util.List;
import java.util.function.Consumer;

public class BucketList extends RecyclerView {
    private final BucketAdapter adapter;

    private Consumer<Bucket> onAction;
    public BucketList(Context owner) {
        super(owner);

        setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(true);

        LinearLayoutManager lm = new LinearLayoutManager(owner);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(lm);

        adapter = new BucketAdapter(owner);
        adapter.setOnAction(bucket -> {
            if(onAction != null) {
                onAction.accept(bucket);
            }
        });
        setAdapter(adapter);

        setClipToPadding(false);
    }

    public void setOnAction(Consumer<Bucket> onAction) {
        this.onAction = onAction;
    }

    public void setData(List<Bucket> data) {
        adapter.setData(data);
    }
}