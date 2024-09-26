package magichand.modid.mixin;


import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.NbtConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerRuntimeDataMixin {



    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {

        if (nbt.contains(NbtConstants.MAGIC_NBT)) {
            PlayerEntity player = (PlayerEntity) ((Object) this);
            MagickaMachine.deserializePlayer(player, nbt.getCompound(NbtConstants.MAGIC_NBT));
        }
    }


    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci)
    {
        PlayerEntity player = (PlayerEntity) ((Object) this);

        MagickaMachine.serializeDisconnectingPlayer(player, nbt);

    }


    @Inject(at = @At("TAIL"), method =  "tick")
    public void tick(CallbackInfo ci)
    {
        PlayerEntity player = (PlayerEntity) ((Object) this);
        MagickaMachine.tick(player);

    }



}
