package io.github.mahardo.threelives;

import io.github.mahardo.threelives.attachment.ModAttachments;
import io.github.mahardo.threelives.network.LivesPayload;
import io.github.mahardo.threelives.rule.ModGameRules;

import java.util.Set;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelData;

// The single place that knows how lives are calculated. Server-side only.
public final class LivesManager {
	private LivesManager() {
	}

	public static int getDeaths(ServerPlayer player) {
		return player.getAttachedOrElse(ModAttachments.DEATHS, 0);
	}

	public static int getMaxLives(MinecraftServer server) {
		return server.getGameRules().get(ModGameRules.LIVES);
	}

	// Out of lives: either they used the last one (ELIMINATED, which survives a raised gamerule),
	// or the gamerule was lowered to or below their death count.
	public static boolean isOutOfLives(ServerPlayer player) {
		return player.getAttachedOrElse(ModAttachments.ELIMINATED, false)
				|| getMaxLives(player.level().getServer()) - getDeaths(player) <= 0;
	}

	public static int getRemainingLives(ServerPlayer player) {
		return isOutOfLives(player) ? 0 : getMaxLives(player.level().getServer()) - getDeaths(player);
	}

	// Sets the remaining lives (1..max) by adjusting the death counter: deaths = max - remaining.
	public static void setRemainingLives(ServerPlayer player, int remaining) {
		boolean wasOutOfLives = isOutOfLives(player);
		player.setAttached(ModAttachments.DEATHS, getMaxLives(player.level().getServer()) - remaining);
		player.removeAttached(ModAttachments.ELIMINATED);
		afterAdminChange(player, wasOutOfLives);
	}

	public static void resetDeaths(ServerPlayer player) {
		boolean wasOutOfLives = isOutOfLives(player);
		player.setAttached(ModAttachments.DEATHS, 0);
		player.removeAttached(ModAttachments.ELIMINATED);
		afterAdminChange(player, wasOutOfLives);
	}

	// Tells the player's client how many lives are left. Clients without the mod can't receive it, so they are skipped.
	public static void sync(ServerPlayer player) {
		syncFrom(player, player);
	}

	// Sends the lives of "source" to the client of "target". After a respawn the old player object is the reliable
	// source: it still has the data, no matter when Fabric copies it over to the new player.
	public static void syncFrom(ServerPlayer target, ServerPlayer source) {
		if (ServerPlayNetworking.canSend(target, LivesPayload.TYPE)) {
			ServerPlayNetworking.send(target, new LivesPayload(
					getRemainingLives(source), getMaxLives(target.level().getServer())));
		}
	}

	// For changes that affect everyone, like the gamerule.
	public static void syncAll(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			sync(player);
		}
	}

	// A spectator who ran out of lives is brought back when an admin gives lives back. Only in hardcore worlds, and
	// only if they were out of lives: someone who just watches as a spectator keeps their game mode.
	private static void afterAdminChange(ServerPlayer player, boolean wasOutOfLives) {
		sync(player);

		MinecraftServer server = player.level().getServer();
		if (wasOutOfLives && server.isHardcore() && player.isSpectator()) {
			// The spectator may be flying inside stone or above the void. Survival right there could kill them at once,
			// so they start at the world spawn.
			LevelData.RespawnData spawn = server.overworld().getRespawnData();
			ServerLevel level = server.getLevel(spawn.dimension());
			if (level != null) {
				player.teleportTo(level, spawn.pos().getX() + 0.5, spawn.pos().getY(), spawn.pos().getZ() + 0.5,
						Set.of(), spawn.yaw(), spawn.pitch(), true);
			}
			player.setGameMode(GameType.SURVIVAL);
		}
	}

	public static void onDeath(ServerPlayer player) {
		if (!player.level().getServer().isHardcore() || player.hasAttached(ModAttachments.DEATH_COUNTED)) {
			return;
		}
		player.setAttached(ModAttachments.DEATH_COUNTED, true);

		// Someone who is already out of lives (a spectator hit by /kill) is not counted any further.
		if (!isOutOfLives(player)) {
			player.modifyAttached(ModAttachments.DEATHS, deaths -> deaths == null ? 1 : deaths + 1);
		}
		if (isOutOfLives(player)) {
			player.setAttached(ModAttachments.ELIMINATED, true);
		}

		sync(player);
	}
}
