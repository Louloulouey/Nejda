package org.luke.nejda.app.home.left;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import org.luke.gui.NodeUtils;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.image.ColorIcon;
import org.luke.gui.controls.image.ColoredIcon;
import org.luke.gui.controls.label.MultiText;
import org.luke.gui.controls.label.TextTransform;
import org.luke.gui.style.ColorItem;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.nejda.app.home.Home;

import java.util.ArrayList;

public class Section extends VBox implements Styleable {

	private ColoredIcon icon;
	private MultiText title;
	private final VBox items;
	
	private final ArrayList<SectionItem> secItems;

	public Section(Home home, String titleKey, String iconName, boolean first) {

		items = new VBox(2);
		NodeUtils.nestedFocus(items);

		if (titleKey != null) {
			title = new MultiText(home.getWindow(), titleKey, new Font(16, FontWeight.BOLD));
			title.setTransform(TextTransform.UPPERCASE);
			HBox titCont = new HBox(title);
			titCont.setAlignment(Pos.CENTER_LEFT);
			titCont.setPadding(new Insets(10, 0, 15, 10));
			titCont.setMaxWidth(208);
			titCont.setMinWidth(0);
			titCont.setSpacing(10);

			if(iconName != null) {
				icon = new ColoredIcon(home.getWindow(), iconName, 28, 20, Style::getAccent);
				titCont.getChildren().addFirst(icon);
			}

			getChildren().add(titCont);
		}

		getChildren().add(items);
		secItems = new ArrayList<>();

		applyStyle(home.getWindow().getStyl());
	}

	public void addPreTitle(ColorItem node) {
		title.addNode(0, node);
	}

	public Section(Home home) {
		this(home, null, null);
	}

	public void addItem(SectionItem item) {
		items.getChildren().add(item);
		secItems.add(item);
	}
	
	public boolean fire(String match) {
		for(SectionItem item : secItems) {
			String k = item.getKey().toLowerCase().trim();
			String m = match.toLowerCase().trim();
			
			if(m.contains(k) || k.contains(m)) {
				item.fire();
				return true;
			}
		}
		
		return false;
	}

	public Section(Home home, String titleKey, String iconName) {
		this(home, titleKey, iconName, false);
	}

	@Override
	public void applyStyle(Style style) {
		if (title != null)
			title.setFill(style.getChannelsDefault());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

}
