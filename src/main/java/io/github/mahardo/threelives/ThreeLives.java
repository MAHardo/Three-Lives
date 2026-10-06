package io.github.mahardo.threelives;

import io.github.mahardo.threelives.attachment.ModAttachments;
import io.github.mahardo.threelives.command.ThreeLivesCommand;
import io.github.mahardo.threelives.network.LivesPayload;
import io.github.mahardo.threelives.rule.ModGameRules;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.resources.Identifier;

public class ThreeLives implements ModInitializer {
	public static final String MOD_ID = "three-lives";

	@Override
	public void onInitialize() {
		ModGameRules.init();
		ModAttachments.init();

		// Registers the packet on both sides (client and server must know it). "clientboundPlay" = server -> client.
		PayloadTypeRegistry.clientboundPlay().register(LivesPayload.TYPE, LivesPayload.CODEC);

		// Send the current values when a player joins, after a respawn, and when the gamerule changes.
		ServerPlayConnectionEvents.JOIN.register((listener, sender, server) -> LivesManager.sync(listener.getPlayer()));
		// After a respawn the client gets the lives of the old player object (see LivesManager.syncFrom).
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> LivesManager.syncFrom(newPlayer, oldPlayer));
		GameRuleEvents.changeCallback(ModGameRules.LIVES).register((value, server) -> LivesManager.syncAll(server));

		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) ->
				ThreeLivesCommand.register(dispatcher));
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
