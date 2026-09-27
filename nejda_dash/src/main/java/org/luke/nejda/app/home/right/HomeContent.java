package org.luke.nejda.app.home.right;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.luke.gui.controls.Font;
import org.luke.gui.controls.label.keyed.Label;
import org.luke.gui.controls.space.ExpandingHSpace;
import org.luke.gui.controls.space.FixedVSpace;
import org.luke.gui.controls.space.Separator;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Window;
import org.luke.nejda.app.home.Home;

import java.util.ArrayList;

public class HomeContent extends VBox implements Styleable {
	protected Font header = new Font(Font.DEFAULT_FAMILY_MEDIUM, 20);

	private final ArrayList<Label> headers = new ArrayList<>();

	private final Home home;

	public HomeContent(Home home) {
		this.home = home;
		setMinWidth(0);
		maxWidthProperty().bind(home.contentWidth());
	}

	public Home getHome() {
		return home;
	}

	public void addHeader(Window win, String s, Node...nodes) {
		Label lab = new Label(win, s, new Font(Font.DEFAULT_FAMILY_MEDIUM, 16));
		headers.add(lab);
		
		HBox hbox = new HBox(20, nodes);
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().add(0, new ExpandingHSpace());
		hbox.getChildren().add(0, lab);
		hbox.setPadding(new Insets(20, 20, 0, 20));
		
		getChildren().addAll(hbox, new FixedVSpace(20));
	}

	public void separate(Window win, double margin) {
		Separator sep = new Separator(win, Orientation.HORIZONTAL);
		setMargin(sep, new Insets(margin, 0, margin, 0));
		getChildren().add(sep);
	}

	public void setup() {

	}

	@Override
	public void applyStyle(Style style) {
		headers.forEach(h -> h.setFill(style.getHeaderPrimary()));
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}
