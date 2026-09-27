package org.luke.nejda.app.home;

import java.awt.Dimension;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import org.luke.gui.style.Style;
import org.luke.gui.style.Styleable;
import org.luke.gui.window.Page;
import org.luke.gui.window.Window;

import javafx.beans.property.ObjectProperty;
import org.luke.nejda.app.home.left.Left;
import org.luke.nejda.app.home.right.HomeContent;

public class Home extends Page {

	private HBox root;
	private Left left;
	private Right right;

	public Home(Window window) {
		super(window, new Dimension(500 * 2 + 15 * 4 + 30, 600));

		root = new HBox();
		root.setPadding(new Insets(10));

		right = new Right(window);
		left = new Left(this);

		root.getChildren().add(left);
		root.getChildren().add(right);
		getChildren().add(root);

		applyStyle(window.getStyl());
	}

	public void load(HomeContent content) {
		right.load(content);
		content.setup();
	}

	public ReadOnlyDoubleProperty contentWidth() {
		return right.widthProperty();
	}

	@Override
	public void applyStyle(Style style) {
		//root.setBackground(Backgrounds.make(style.getAccent()));
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

}
