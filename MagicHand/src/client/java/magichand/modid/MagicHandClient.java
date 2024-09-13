package magichand.modid;

import magichand.modid.entity.EntityRegistrator;
import magichand.modid.networking.ClientPacketRegistrator;
import magichand.modid.render.entity.ProjectileRender;
import magichand.modid.render.entity.SprayMagicProjectileRender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class MagicHandClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		EntityRendererRegistry.register(EntityRegistrator.PROJECTILE, ProjectileRender::new);
		EntityRendererRegistry.register(EntityRegistrator.SPRAY_PROJECTILE, SprayMagicProjectileRender::new);
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ClientPacketRegistrator.initializeAndRegister();
	}
}