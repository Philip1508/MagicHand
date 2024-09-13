package magichand.modid.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class MouseHoldingPacketResponse {

    public static void receive(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {

        PacketByteBuf packet = PacketByteBufs.create();



        packet.writeBoolean(MinecraftClient.getInstance().mouse.wasLeftButtonClicked());

        ClientPlayNetworking.send(PacketRegistrator.STILL_ACTIVE, packet);


        }

    }
