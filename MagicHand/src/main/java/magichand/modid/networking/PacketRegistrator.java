package magichand.modid.networking;

import magichand.modid.MagicHand;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class PacketRegistrator {


    public  static  Identifier STILL_ACTIVE = new Identifier(MagicHand.MOD_ID, "still-active-ping");
    public  static  Identifier S2C_KEEPALIVE = new Identifier(MagicHand.MOD_ID, "s2c-keepalive");
    public static Identifier RUNTIMEDATA_S2C = new Identifier(MagicHand.MOD_ID, "runtimedata-s2c");


    public static void initializeAndRegister()
    {
        ServerPlayNetworking.registerGlobalReceiver(STILL_ACTIVE, MouseHoldingPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(S2C_KEEPALIVE, S2CKeepAlive::receive);


    }
}
