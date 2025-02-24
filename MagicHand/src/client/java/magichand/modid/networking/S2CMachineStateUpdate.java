package magichand.modid.networking;

import magichand.modid.MagicHand;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.MagickaMachineState;
import magichand.modid.playerextension.maskedconstants.ClientPlayerRepresentationConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.Objects;

public class S2CMachineStateUpdate {

    // ToDo; We need to declare a "header" in the received nbt. That header will determine, wether the message contains
    // a full update / partial update.
    public static void receive(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {


        NbtCompound nbt = packetByteBuf.readNbt();

        String state = nbt.getString(ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_KEY);

        if (MagickaMachine.clientRepresentation == null) {return;}

        MagickaMachine.clientRepresentation.refresh();

        if (Objects.equals(state, ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_PASSIVE))
        {
            MagicHand.LOGGER.info("Setting Client State to Passive");
            MagickaMachine.clientRepresentation.state = MagickaMachineState.MANA_PASSIVE_REGENERATION;
        }

        if (Objects.equals(state, ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_ACTIVE))
        {
            MagicHand.LOGGER.info("Setting Client State to Passive");
            MagickaMachine.clientRepresentation.state = MagickaMachineState.MANA_ACTIVE_CAST;
        }





    }

}
