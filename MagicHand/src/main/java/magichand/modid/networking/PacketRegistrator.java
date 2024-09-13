package magichand.modid.networking;

import magichand.modid.MagicHand;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class PacketRegistrator {


    public  static  Identifier STILL_ACTIVE = new Identifier(MagicHand.MOD_ID, "still-active-ping");

    public static void initializeAndRegister()
    {
        ServerPlayNetworking.registerGlobalReceiver(STILL_ACTIVE, MouseHoldingPacket::receive);


    }
}
