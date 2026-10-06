package io.github.mahardo.threelives.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.mahardo.threelives.LivesManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

// A mixin injects our code into an existing Minecraft class when the game starts.
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
	// Gives us access to the field "player" that already exists in the target class.
	@Shadow
	public ServerPlayer player;

	// In handleClientCommand (the player clicked "Respawn"), vanilla runs:
	//     if (server.isHardcore()) { player.setGameMode(SPECTATOR); }
	// We wrap the isHardcore() call: the original result is only kept when the player
	// has no lives left. Otherwise we return false, so no spectator mode and a normal respawn.
	// By this point vanilla has already replaced "player" with the new respawned player,
	// which carries the death counter (copyOnDeath).
	@ModifyExpressionValue(
			method = "handleClientCommand",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isHardcore()Z")
	)
	private boolean threelives$keepSpectatorOnlyWhenOutOfLives(boolean isHardcore) {
		return isHardcore && LivesManager.isOutOfLives(this.player);
	}
}
