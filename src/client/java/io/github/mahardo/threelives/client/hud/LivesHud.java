package io.github.mahardo.threelives.client.hud;

import io.github.mahardo.threelives.ThreeLives;
import io.github.mahardo.threelives.client.ClientLivesState;
import io.github.mahardo.threelives.config.ThreeLivesConfig;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudStatusBarHeightRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

// Turquoise hearts for the remaining lives, drawn above the vanilla health and armor rows.
public final class LivesHud {
	private static final Identifier ID = ThreeLives.id("lives");
	// Points to assets/three-lives/textures/gui/sprites/heart.png
	private static final Identifier HEART = ThreeLives.id("heart");
	// A used-up life: the same heart, but dark.
	private static final Identifier HEART_EMPTY = ThreeLives.id("heart_empty");

	private static final int HEART_SIZE = 9;
	private static final int HEART_STRIDE = 8; // like vanilla, hearts overlap by one pixel
	private static final int ROW_HEIGHT = 10;
	private static final int MAX_HEARTS = 10;

	private LivesHud() {
	}

	public static void init() {
		// "After armor" means above it: the HUD stacks elements from the bottom up.
		HudElementRegistry.attachElementAfter(VanillaHudElements.ARMOR_BAR, ID, LivesHud::extractRenderState);
		// Tells Fabric how much space our row takes, so the vanilla rows below don't overlap and rows above move up.
		// It must return 0 whenever we don't draw anything.
		HudStatusBarHeightRegistry.addLeft(ID, player -> isVisible() ? ROW_HEIGHT : 0);
	}

	// Only if enabled in the config, only in hardcore worlds, only while the server has sent us values, and only while the vanilla health bar shows too
	// (not in creative or spectator).
	private static boolean isVisible() {
		Minecraft minecraft = Minecraft.getInstance();
		return ThreeLivesConfig.showHearts
				&& minecraft.level != null
				&& minecraft.level.getLevelData().isHardcore()
				&& ClientLivesState.isActive()
				&& minecraft.gameMode != null
				&& minecraft.gameMode.canHurtPlayer();
	}

	private static void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		if (!isVisible()) {
			return;
		}

		int remaining = ClientLivesState.remaining();
		int max = ClientLivesState.max();
		int left = graphics.guiWidth() / 2 - 91; // same left edge as the vanilla health bar
		int top = graphics.guiHeight() - HudStatusBarHeightRegistry.getHeight(ID);

		if (max <= MAX_HEARTS) {
			// One heart per life; used-up lives stay visible as dark hearts.
			for (int i = 0; i < max; i++) {
				Identifier sprite = i < remaining ? HEART : HEART_EMPTY;
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, left + i * HEART_STRIDE, top, HEART_SIZE, HEART_SIZE);
			}
		} else {
			// Too many for a row: a single heart plus "x<n>". The layout depends on the maximum, so it doesn't jump around.
			Identifier sprite = ClientLivesState.hasLivesLeft() ? HEART : HEART_EMPTY;
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, left, top, HEART_SIZE, HEART_SIZE);
			graphics.text(Minecraft.getInstance().font, "x" + remaining, left + HEART_SIZE + 3, top + 1, 0xFF55FFFF, true);
		}
	}
}
