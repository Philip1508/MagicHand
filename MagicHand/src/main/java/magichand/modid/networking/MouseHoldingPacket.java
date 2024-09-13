package magichand.modid.networking;

import magichand.modid.MagicHand;
import magichand.modid.items.ItemRegistrator;
import magichand.modid.items.SprayDebugHand;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MouseHoldingPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
    {
        boolean stillShooting = buf.readBoolean();
        ItemStack hand = player.getMainHandStack();

        if (stillShooting && hand.isOf(ItemRegistrator.DEBUG_SPRAY_HAND))
        {
            MagicHand.LOGGER.info("Player Still Firing Spell");
            NbtCompound nbt = hand.getOrCreateSubNbt(SprayDebugHand.ACTIVE_CAST);

            nbt.putBoolean(SprayDebugHand.CURRENTLY_CASTING, stillShooting);
            nbt.putInt(SprayDebugHand.CASTED_HAND, 2);
            nbt.putInt(SprayDebugHand.REMAINING,  3);
        }



    }

    }
