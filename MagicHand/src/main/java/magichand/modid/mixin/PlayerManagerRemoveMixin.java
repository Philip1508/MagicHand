package magichand.modid.mixin;

import magichand.modid.playerextension.MagickaMachine;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerRemoveMixin {
	@Inject(at = @At("TAIL"), method = "remove")
	private void init(ServerPlayerEntity player , CallbackInfo info) {
		// This code is injected into the start of MinecraftServer.loadWorld()V

		if (MagickaMachine.isPlayerMagicuser(player))
		{
			MagickaMachine.removePlayer(player);
		}

	}
}