package io.github.mahardo.threelives.client;

import io.github.mahardo.threelives.client.hud.LivesHud;
import io.github.mahardo.threelives.network.LivesPayload;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ThreeLivesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Runs on the client thread whenever the server sends a LivesPayload.
		ClientPlayNetworking.registerGlobalReceiver(LivesPayload.TYPE, (payload, context) -> {
			ClientLivesState.update(payload.remaining(), payload.max());
		});

		// Forget the values when leaving a world, so a server without the mod doesn't show the old hearts.
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientLivesState.update(0, 0));

		LivesHud.init();
	}
}
