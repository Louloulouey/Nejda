package org.luke.nejda.settings;

import org.luke.gui.window.Window;
import org.luke.nejda.settings.abs.Settings;
import org.luke.nejda.settings.abs.left.Section;
import org.luke.nejda.settings.abs.left.SectionItem;
import org.luke.nejda.settings.content.display.DisplaySettings;


public class AppSettings extends Settings {

	public AppSettings(Window win) {
		super(win);
		
		Section appSettings = new Section(this, "nejda_settings", true);
		appSettings.addItem(new SectionItem(this, "display", DisplaySettings.class));

		sideBar.addSection(appSettings);
		sideBar.separate(win);

		fire("display");
	}
	
	public boolean fire(String match) {
		return sideBar.fire(match);
	}

}
