package io.github.mahardo.threelives.client.config;

import io.github.mahardo.threelives.config.ThreeLivesConfig;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// The settings screen, built with Cloth Config. Only loaded when Cloth Config is installed (see ThreeLivesModMenu).
final class ThreeLivesConfigScreen {
	private ThreeLivesConfigScreen() {
	}

	static Screen create(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable("three-lives.config.title"))
				// Runs when the player clicks "Save & Quit".
				.setSavingRunnable(ThreeLivesConfig::save);
		ConfigEntryBuilder entries = builder.entryBuilder();

		ConfigCategory world = builder.getOrCreateCategory(Component.translatable("three-lives.config.category.world"));
		world.addEntry(entries.startIntField(Component.translatable("three-lives.config.default_lives"), ThreeLivesConfig.defaultLives)
				.setDefaultValue(ThreeLivesConfig.DEFAULT_LIVES)
				.setMin(1)
				.setTooltip(Component.translatable("three-lives.config.default_lives.tooltip"))
				.setSaveConsumer(value -> ThreeLivesConfig.defaultLives = value)
				// The gamerule gets its start value when the game starts, so a change needs a restart.
				.requireRestart()
				.build());

		ConfigCategory display = builder.getOrCreateCategory(Component.translatable("three-lives.config.category.display"));
		display.addEntry(entries.startBooleanToggle(Component.translatable("three-lives.config.show_hearts"), ThreeLivesConfig.showHearts)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("three-lives.config.show_hearts.tooltip"))
				.setSaveConsumer(value -> ThreeLivesConfig.showHearts = value)
				.build());
		display.addEntry(entries.startBooleanToggle(Component.translatable("three-lives.config.show_death_screen_line"), ThreeLivesConfig.showDeathScreenLine)
				.setDefaultValue(true)
				.setTooltip(Component.translatable("three-lives.config.show_death_screen_line.tooltip"))
				.setSaveConsumer(value -> ThreeLivesConfig.showDeathScreenLine = value)
				.build());

		return builder.build();
	}
}
