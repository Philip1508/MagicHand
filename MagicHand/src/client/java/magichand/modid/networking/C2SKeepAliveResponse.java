package magichand.modid.networking;

import magichand.modid.MagicHand;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class C2SKeepAliveResponse {

    public static void receive(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        PacketByteBuf packet = PacketByteBufs.create();

        int response = 0;



        if (MinecraftClient.getInstance().mouse.wasLeftButtonClicked())
        {
            MagicHand.LOGGER.info("Left Mouse Button Response Affirmative!");
            response += 1;
        }

        if (MinecraftClient.getInstance().mouse.wasRightButtonClicked())
        {
            response += 2;
        }

        packet.writeInt(response);

        ClientPlayNetworking.send(PacketRegistrator.S2C_KEEPALIVE, packet);

    }


}
