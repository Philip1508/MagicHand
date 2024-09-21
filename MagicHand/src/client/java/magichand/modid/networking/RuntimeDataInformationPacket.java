package magichand.modid.networking;


import magichand.modid.playerextension.MagickaMachine;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

// Hier müssen die Spieler Laufzeitdaten empfangen werden.
public class RuntimeDataInformationPacket {


    public static void receive(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {



        NbtCompound nbt = packetByteBuf.readUnlimitedNbt();

        if (nbt != null)
        {
            MagickaMachine.updateClientPlayer(MinecraftClient.getInstance().player, nbt);

        }



    }

}
