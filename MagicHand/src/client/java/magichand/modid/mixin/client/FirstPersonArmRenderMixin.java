package magichand.modid.mixin.client;

import magichand.modid.items.ItemRegistrator;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class FirstPersonArmRenderMixin {

	@Shadow
	private void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm)
	{
		// DO NOT WRITE CODE HERE. THIS IS A SHADOW WITH A DUMMY BODY.
	}




	@Inject(at = @At("HEAD"), method = "renderFirstPersonItem", cancellable = true)
	public void renderFirstPersonItem(AbstractClientPlayerEntity player,
									  float tickDelta,
									  float pitch,
									  Hand hand,
									  float swingProgress,
									  ItemStack item,
									  float equipProgress,
									  MatrixStack matrices,
									  VertexConsumerProvider vertexConsumers,
									  int light
	, CallbackInfo ci) {


		if (item.isOf(ItemRegistrator.DEBUG_SPRAY_HAND))
		{

			boolean isMainHand = hand == Hand.MAIN_HAND;
			Arm arm = isMainHand ? Arm.RIGHT : Arm.LEFT;

			matrices.push();
			renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, 0, arm);
			matrices.pop();

			ci.cancel();
		}

	}
}