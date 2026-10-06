package io.github.mahardo.threelives.attachment;

import com.mojang.serialization.Codec;

import io.github.mahardo.threelives.ThreeLives;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public final class ModAttachments {
	// How often a player has died in this hardcore world.
	// persistent: saved with the player data, so it survives relogging and server restarts.
	// copyOnDeath: on respawn Minecraft creates a new ServerPlayer object; this copies the value over.
	public static final AttachmentType<Integer> DEATHS = AttachmentRegistry.<Integer>create(
			ThreeLives.id("deaths"),
			builder -> builder.persistent(Codec.INT).copyOnDeath()
	);

	// Set when a player used up their last life. Stays set until an admin gives lives back (/threelives set|reset),
	// so raising the gamerule later doesn't bring a spectator back to life.
	public static final AttachmentType<Boolean> ELIMINATED = AttachmentRegistry.<Boolean>create(
			ThreeLives.id("eliminated"),
			builder -> builder.persistent(Codec.BOOL).copyOnDeath()
	);

	// Marks that the current death was already counted. Not persistent and not copied on death,
	// so the respawned player starts without it. Protects against die() being called twice for one death.
	public static final AttachmentType<Boolean> DEATH_COUNTED = AttachmentRegistry.create(
			ThreeLives.id("death_counted")
	);

	private ModAttachments() {
	}

	// Called from the mod initializer so this class gets loaded and DEATHS gets registered.
	public static void init() {
	}
}
