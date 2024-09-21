package magichand.modid.networking;

import magichand.modid.MagicHand;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class ClientPacketRegistrator {

    //public  static  Identifier STILL_ACTIVE = new Identifier(MagicHand.MOD_ID, "stillActivePing");



    public static void initializeAndRegister()
    {
        ClientPlayNetworking.registerGlobalReceiver(PacketRegistrator.STILL_ACTIVE, MouseHoldingPacketResponse::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketRegistrator.RUNTIMEDATA_S2C, RuntimeDataInformationPacket::receive);


    }
}
