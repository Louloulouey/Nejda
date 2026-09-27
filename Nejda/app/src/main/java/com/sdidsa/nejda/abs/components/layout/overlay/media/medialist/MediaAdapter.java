package com.sdidsa.nejda.abs.components.layout.overlay.media.medialist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdidsa.nejda.abs.data.property.ObservableArrayList;
import com.sdidsa.nejda.abs.data.media.Media;
import com.sdidsa.nejda.abs.data.property.Property;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaHolder> {
    private final Context owner;
    private List<Media> data;

    private final int size;

    private final ObservableArrayList<Media> selected;
    private final Property<Boolean> single;

    public MediaAdapter(Context owner, int size, ObservableArrayList<Media> selected, Property<Boolean> single) {
        this.owner = owner;
        this.selected = selected;
        this.single = single;
        this.data = new ArrayList<>();
        this.size = size;
    }

    @NonNull
    @Override
    public MediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MediaHolder(new MediaEntry(owner, size / MediaList.SPAN_COUNT), selected, single);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaHolder holder, int position) {
        Media media = data.get(position);
        holder.load(media);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Media> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}