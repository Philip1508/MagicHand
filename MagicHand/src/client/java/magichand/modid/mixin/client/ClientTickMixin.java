package magichand.modid.mixin.client;

import magichand.modid.ClientRepresentation.ClientRepresentationInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientTickMixin {
	@Inject(at = @At("TAIL"), method = "tick")
	private void init(CallbackInfo info) {
		// This code is injected into the start of MinecraftClient.run()V
		ClientPlayerEntity player = (ClientPlayerEntity) ((Object) this);

		ClientRepresentationInterface.clientTickExtension(player);

	}
}