package io.github.mahardo.threelives.client.mixin;

import io.github.mahardo.threelives.client.ClientLivesState;
import io.github.mahardo.threelives.config.ThreeLivesConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {
	// The constructor's "hardcore" parameter switches the screen to the "Spectate" variant.
	// While lives remain we pass false instead, which gives the normal "Respawn" screen.
	// Without the mod on the server (max is 0) nothing changes.
	@ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
	private static boolean threelives$respawnWhileLivesRemain(boolean hardcore) {
		return hardcore && !ClientLivesState.hasLivesLeft();
	}

	// Draws "You have X lives left" below the score line (score is at y=100).
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void threelives$drawLivesLeft(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		Minecraft minecraft = Minecraft.getInstance();
		if (!ThreeLivesConfig.showDeathScreenLine || minecraft.level == null || !minecraft.level.getLevelData().isHardcore() || !ClientLivesState.hasLivesLeft()) {
			return;
		}
		int remaining = ClientLivesState.remaining();

		Component text = remaining == 1
				? Component.translatableWithFallback("three-lives.death_screen.lives_left.one", "You have 1 life left")
				: Component.translatableWithFallback("three-lives.death_screen.lives_left", "You have %s lives left", remaining);
		graphics.centeredText(minecraft.font, text, graphics.guiWidth() / 2, 114, 0xFF55FFFF);
	}
}
