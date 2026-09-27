package com.sdidsa.nejda.abs.components.layout.scroll;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder<U,V extends RecyclerItemView<U>> extends RecyclerView.ViewHolder {
    private final V itemView;

    public RecyclerViewHolder(@NonNull V itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void load(U item) {
        itemView.load(item);
    }

}
