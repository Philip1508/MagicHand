package magichand.modid.networking;

import magichand.modid.MagicHand;
import magichand.modid.items.ItemRegistrator;
import magichand.modid.items.SprayDebugHand;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.PlayerRuntimeData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class S2CKeepAlive {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender)
    {
        int stillShooting = buf.readInt();


        boolean mainHandStillActive = (stillShooting & 1) == 1;


        boolean offHandStillActive = (stillShooting & 2) == 2;

        PlayerRuntimeData data = MagickaMachine.getPlayerRuntimeData(player);
        if (data != null)
        {
            if (!mainHandStillActive)
            {
                //MagicHand.LOGGER.info("Disabling Main Hand Cast due to Network Response!");
                data.getCastMachine().disableHand(Hand.MAIN_HAND);
            }
            if (!offHandStillActive)
            {
                //MagicHand.LOGGER.info("Disabling Off Hand Cast due to Network Response!");
                data.getCastMachine().disableHand(Hand.OFF_HAND);
            }

        }






    }

}
