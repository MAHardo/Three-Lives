package io.github.mahardo.threelives.mixin;

import io.github.mahardo.threelives.LivesManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
	// ServerPlayer.die sends the packet that opens the death screen right at its start.
	// We count the death before that (HEAD = very first line), so the client already knows
	// the new number of lives when it builds the death screen. (Fabric's AFTER_DEATH event runs too late.)
	@Inject(method = "die", at = @At("HEAD"))
	private void threelives$countDeath(DamageSource source, CallbackInfo ci) {
		LivesManager.onDeath((ServerPlayer) (Object) this);
	}
}
