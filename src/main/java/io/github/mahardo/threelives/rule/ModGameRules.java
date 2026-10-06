package io.github.mahardo.threelives.rule;

import io.github.mahardo.threelives.config.ThreeLivesConfig;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public final class ModGameRules {
	// Number of lives each player has in a hardcore world. The start value for new worlds comes from the config file;
	// reading ThreeLivesConfig.defaultLives loads that file first if needed.
	// minValue(1) makes the /gamerule command reject 0, negative numbers and non-integers,
	// and also validates the value when the world is loaded.
	public static final GameRule<Integer> LIVES = GameRuleBuilder.forInteger(ThreeLivesConfig.defaultLives)
			.minValue(1)
			.category(GameRuleCategory.PLAYER)
			.buildAndRegister(Identifier.fromNamespaceAndPath("threelives", "lives"));

	private ModGameRules() {
	}

	// Called from the mod initializer so this class gets loaded and LIVES gets registered.
	public static void init() {
	}
}
