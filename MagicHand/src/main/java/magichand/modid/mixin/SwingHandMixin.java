package magichand.modid.mixin;


import magichand.modid.MagicHand;
import magichand.modid.entity.EntityRegistrator;
import magichand.modid.items.ItemRegistrator;
import magichand.modid.items.SprayDebugHand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// This must not be instantiated.
// Due to the obscure documentation of returning in void type methods I'll make it a toggle, if Spraying....
@Mixin(LivingEntity.class)
public abstract class SwingHandMixin {


    @Shadow public abstract void remove(Entity.RemovalReason reason);

    @Inject(at = @At("HEAD"), method = "swingHand(Lnet/minecraft/util/Hand;Z)V", cancellable = true)
    public void swingHand(Hand hand, boolean fromServerPlayer, CallbackInfo info)
    {
        // The Linter is confused, which is to be expected since this is code injection.
        LivingEntity entity = ((LivingEntity) (Object) this );

        boolean test = fromServerPlayer;
        if (test)
        {
            MagicHand.LOGGER.info("LOL");
        }

        ItemStack item = entity.getMainHandStack();





        if (item.isOf(ItemRegistrator.DEBUG_SPRAY_HAND) && hand == Hand.MAIN_HAND)
        {
            System.out.println("Magic Hand Detected");
            System.out.println(hand);

            if (entity instanceof PlayerEntity player)
            {
                NbtCompound nbt = item.getOrCreateSubNbt(SprayDebugHand.ACTIVE_CAST);

                nbt.putBoolean(SprayDebugHand.CURRENTLY_CASTING, true);
                nbt.putInt(SprayDebugHand.CASTED_HAND, 2);
                nbt.putInt(SprayDebugHand.REMAINING,  3);
            }


            info.cancel();
            return;

        }

    }

}
