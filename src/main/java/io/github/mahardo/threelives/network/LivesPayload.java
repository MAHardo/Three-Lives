package io.github.mahardo.threelives.network;

import io.github.mahardo.threelives.ThreeLives;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// Server -> client: how many lives the player has left and the maximum (the gamerule).
// A record is a short class that just holds values; Java generates the constructor and remaining() / max() for us.
public record LivesPayload(int remaining, int max) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<LivesPayload> TYPE = new CustomPacketPayload.Type<>(ThreeLives.id("lives"));

	// Describes how the two numbers are written to / read from the network packet (in this order).
	public static final StreamCodec<RegistryFriendlyByteBuf, LivesPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, LivesPayload::remaining,
			ByteBufCodecs.VAR_INT, LivesPayload::max,
			LivesPayload::new
	);

	@Override
	public CustomPacketPayload.Type<LivesPayload> type() {
		return TYPE;
	}
}
