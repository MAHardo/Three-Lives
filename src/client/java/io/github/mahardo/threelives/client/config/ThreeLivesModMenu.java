package io.github.mahardo.threelives.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;

// Entrypoint "modmenu": tells Mod Menu which screen to open for our "Configure" button.
public class ThreeLivesModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		// The screen is built with Cloth Config, which players may not have installed.
		// Without it we return Mod Menu's default, which simply shows no "Configure" button.
		if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
			return ModMenuApi.super.getModConfigScreenFactory();
		}
		return ThreeLivesConfigScreen::create;
	}
}
