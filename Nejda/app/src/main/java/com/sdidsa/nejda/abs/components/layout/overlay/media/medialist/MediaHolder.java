package com.sdidsa.nejda.abs.components.layout.overlay.media.medialist;

import androidx.recyclerview.widget.RecyclerView;

import com.sdidsa.nejda.abs.data.property.ObservableArrayList;
import com.sdidsa.nejda.abs.data.media.Media;
import com.sdidsa.nejda.abs.data.property.Property;

import java.util.List;

public class MediaHolder extends RecyclerView.ViewHolder {
    private final MediaEntry entry;
    private Media data;
    private final ObservableArrayList<Media> selected;
    private final Property<Boolean> single;

    public MediaHolder(MediaEntry entry, ObservableArrayList<Media> selected, Property<Boolean> single) {
        super(entry);
        this.single = single;
        this.selected = selected;
        entry.setOnClick(() -> {
            if(selected.contains(data)) {
                selected.remove(data);
            } else {
                System.out.println(single);
                if(single.get()) {
                    selected.setAll(List.of(data));
                } else {
                    selected.add(data);
                }
            }
        });
        selected.addListener((src, c) -> {
            if(c.getAdded().contains(data)) {
                System.out.println("added");
                entry.select();
            }
            if(c.getRemoved().contains(data)) {
                System.out.println("removed");
                entry.deselect();
            }
            if(single.get()) {
                entry.setIndex(-1);
            }else if(src.contains(data)) {
                entry.setIndex(src.indexOf(data));
            }
        });
        this.entry = entry;
    }

    public void load(Media data) {
        if (selected.contains(data) && !selected.contains(this.data)) {
            entry.select();
        } else if (!selected.contains(data) && selected.contains(this.data)) {
            entry.deselect();
        }
        if(single.get()) {
            entry.setIndex(-1);
        }else if(selected.contains(data)) {
            entry.setIndex(selected.indexOf(data));
        }
        this.data = data;
        entry.load(data);
    }
}