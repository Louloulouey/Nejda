package org.luke.nejda.app;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.luke.gui.controls.popup.Direction;
import org.luke.gui.window.Window;
import org.luke.gui.window.content.app_bar.AppBarButton;
import org.luke.nejda.app.home.Home;
import org.luke.nejda.settings.SettingsPan;
import org.luke.nejda.lang.LanguageMenu;
import org.luke.nejda.local.LocalStore;

import javafx.application.Application;
import javafx.stage.Stage;
import org.luke.nejda.about.Credits;

public class Nejda extends Application {
	public static Nejda instance;
	public static Window winstance;

	public Nejda() {
		instance = this;
	}

	private SettingsPan sets;
	private Home home;

	@Override
	public void start(Stage ps) throws Exception {

		System.setProperty("prism.lcdtext", "false");

		Window window = new Window(this, LocalStore.getStyle(), LocalStore.getLanguage());
		winstance = window;
		
		window.getStyl().addListener((_, _, nv) -> {
			LocalStore.setStyle(nv);
		});

		home = new Home(window);

		Credits about = new Credits(home);

		sets = new SettingsPan(home);

		AppBarButton openSettings = new AppBarButton(window, "settings");
		AppBarButton lang = new AppBarButton(window, "language");

		LanguageMenu langMenu = new LanguageMenu(window);

		window.addBarButton(1, openSettings);
		window.addBarButton(1, lang);
		window.setOnInfo(about::showAndWait);

		window.setWindowIcon("nejda-icon");
		window.setTaskIcon("nejda-task-icon");

		window.setTitle("Nejda");

		openSettings.setAction(sets::show);

		lang.setAction(() -> {
			langMenu.showPop(lang, Direction.DOWN, 0, 15);
		});

		window.setOnShown(_ -> window.loadPage(home));
		window.show();
	}

	public void openSettings(String match) {
		if (sets.fire(match)) {
			sets.showAndWait();
		}
	}
	
	public Home getHome() {
		return home;
	}

	public static File get7z() {
        return new File(URLDecoder.decode(Objects.requireNonNull(Nejda.class.getResource("/7z.exe")).getFile(),
				StandardCharsets.UTF_8)).getParentFile();
    }

	public static File getResourceHacker() {
		return new File(URLDecoder.decode(Objects.requireNonNull(Nejda.class.getResource("/ResourceHacker.exe")).getFile(),
				StandardCharsets.UTF_8)).getParentFile();
	}
}
