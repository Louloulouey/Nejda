package com.sdidsa.nejda.view.app_content.session.nav;

import android.content.Context;
import android.widget.FrameLayout;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.controls.scratches.ColoredSeparator;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.scratches.Separator;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.components.layout.fragment.FragmentPane;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.view.app_content.session.home.ai.AiAssistant;
import com.sdidsa.nejda.view.app_content.session.home.HomePage;
import com.sdidsa.nejda.view.app_content.session.home.chats.Chats;
import com.sdidsa.nejda.view.app_content.session.home.main.Main;
import com.sdidsa.nejda.view.app_content.session.home.maps.Maps;

public class NavBar extends ColoredStackPane {
    private final HBox root;

    private NavIcon selected;
    private final FragmentPane content;

    public NavBar(Context owner, FragmentPane content) {
        super(owner, Style.BACK_SEC);
        this.content = content;

        root = new HBox(owner);

        Separator sep = new ColoredSeparator(owner, Orientation.HORIZONTAL, 0, Style.TEXT_SEC);
        sep.setAlpha(0.2f);

        setLayoutParams(new FrameLayout.LayoutParams(-1, -2));

        addNavIcon(R.drawable.home_fill, R.drawable.home_outline, "Home", Main.class);
        addNavIcon(R.drawable.map_fill, R.drawable.map_outline, "Map", Maps.class);
        addNavIcon(R.drawable.chat_fill, R.drawable.chat_outline, "Chats", Chats.class);
        addNavIcon(R.drawable.bot_fill, R.drawable.bot_outline, "Assistant", AiAssistant.class);

        addCentered(root);
        addAligned(sep, Alignment.TOP_CENTER);
    }

    public void selectMaps() {
        select(1);
    }

    public void select(int index) {
        NavIcon icon = (NavIcon) root.getChildAt(index);
        icon.select();
    }

    public void unselect() {
        if(selected == null) return;
        selected.unselect();
        selected = null;
    }

    public NavIcon getSelected() {
        return selected;
    }

    public void setSelected(NavIcon selected) {
        this.selected = selected;
    }

    public void addNavIcon(int fill, int out, String text) {
        addNavIcon(fill, out, text, (Runnable) null);
    }

    public void addNavIcon(int fill, int out, String text, Class<? extends HomePage> nextPage) {
        NavIcon icon = new NavIcon(owner, fill, out, text, this);
        icon.setOnSelect(() -> {
            if(selected == null) {
                content.nextInto(nextPage);
                return;
            }

            int oldIndex = root.indexOfChild(selected);
            int myIndex = root.indexOfChild(icon);

            if(oldIndex < myIndex) {
                content.nextInto(nextPage);
            } else {
                content.previousInto(nextPage);
            }
        });
        root.addView(icon);
    }

    public int selectedIndex() {
        return root.indexOfChild(selected);
    }

    public void addNavIcon(int fill, int out, String text, Runnable onSelect) {
        NavIcon icon = new NavIcon(owner, fill, out, text, this);
        icon.setOnSelect(onSelect);
        root.addView(icon);
    }
}
