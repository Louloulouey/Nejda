package com.sdidsa.nejda.abs.components.layout.overlay.media.bucketlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdidsa.nejda.abs.data.media.Bucket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BucketAdapter extends RecyclerView.Adapter<BucketHolder> {
    private final Context owner;
    private List<Bucket> data;

    private Consumer<Bucket> onAction;

    public BucketAdapter(Context owner) {
        this.owner = owner;
        this.data = new ArrayList<>();
    }

    public void setOnAction(Consumer<Bucket> onAction) {
        this.onAction = onAction;
    }

    @NonNull
    @Override
    public BucketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BucketHolder holder = new BucketHolder(new BucketEntry(owner));
        holder.setOnAction(code -> {
            if (onAction != null) {
                onAction.accept(code);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BucketHolder holder, int position) {
        Bucket code = data.get(position);
        holder.load(code);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Bucket> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}