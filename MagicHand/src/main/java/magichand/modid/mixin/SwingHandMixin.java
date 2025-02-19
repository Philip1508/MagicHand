package magichand.modid.mixin;


import magichand.modid.MagicHand;
import magichand.modid.entity.EntityRegistrator;
import magichand.modid.items.ItemRegistrator;
import magichand.modid.items.SprayDebugHand;
import magichand.modid.items.spellcatalysts.AbstractSpellCatalyst;
import magichand.modid.playerextension.MagickaMachine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
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

    @Shadow public abstract boolean isAlive();

    @Shadow public abstract boolean isBaby();

    @Shadow public abstract void swingHand(Hand hand);

    @Inject(at = @At("HEAD"), method = "swingHand(Lnet/minecraft/util/Hand;Z)V", cancellable = true)
    public void swingHand(Hand hand, boolean fromServerPlayer, CallbackInfo info)
    {
        // The Linter is confused, which is to be expected since this is code injection.
        LivingEntity entity = ((LivingEntity) (Object) this );
        ItemStack item = entity.getMainHandStack();


        if (item.getItem() instanceof AbstractSpellCatalyst && item.getHolder() instanceof ServerPlayerEntity sPlayer)
        {
            MagickaMachine.getPlayerRuntimeData(sPlayer).getCastMachine().initiateCast(Hand.MAIN_HAND);
            MagicHand.LOGGER.info("Main Hand Chime Firing.");
            info.cancel();


        }




    }

}
