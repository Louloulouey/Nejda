package com.sdidsa.nejda.abs.components.layout.overlay.media.bucketlist;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.data.media.Bucket;

import java.util.function.Consumer;

public class BucketHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final BucketEntry entry;
    private Bucket data;

    private Consumer<Bucket> onAction;

    public BucketHolder(BucketEntry entry) {
        super(entry);
        entry.setOnClickListener(this);
        this.entry = entry;
    }

    public void load(Bucket data) {
        this.data = data;
        entry.load(data);
    }

    @Override
    public void onClick(View view) {
        if(onAction != null && data != null) {
            try {
                onAction.accept(data);
            } catch (Exception x) {
                ErrorHandler.handle(x , "handle bucket selection");
            }
        }
    }

    public void setOnAction(Consumer<Bucket> onAction) {
        this.onAction = onAction;
    }
}
