package org.luke.nejda.settings.content.display;

import org.luke.nejda.settings.abs.Settings;
import org.luke.nejda.settings.abs.SettingsContent;
import org.luke.nejda.settings.content.display.theme.ThemeSetting;

public class DisplaySettings extends SettingsContent {

	public DisplaySettings(Settings settings) {
		super(settings);

		addHeader(settings.getWindow(), "color_theme");

		getChildren().add(new ThemeSetting(settings));

		separate(settings.getWindow(), 20);

		applyStyle(settings.getWindow().getStyl());
	}
}
