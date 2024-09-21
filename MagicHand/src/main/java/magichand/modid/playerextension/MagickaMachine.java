package magichand.modid.playerextension;

import magichand.modid.MagicHand;
import magichand.modid.networking.PacketRegistrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public abstract class MagickaMachine {

    // Note that this data structure is NOT synced across clients! Be wary of the consequences that ensue!
    private static final Map<PlayerEntity, PlayerRuntimeData> PLAYER_TO_MAGICKDATA = new HashMap<>();






    public static void tick(PlayerEntity player)
    {

        // Dieser Fall muss durch Networking scheinbar definiert werden...?
        // Das ist hier so nicht möglich. Scheinbar muss bei der Serialisierung eine Nachricht an den Client
        // gesendet werden, welche den Client entsprechend registrieren müsste?
        // Wir brauchen hierbei scheinbar eine vollständige Tick-Injection für den Client, wobei diese Datenstruktur
        // im Client entsprechend parallel vorhanden seien muss.
        // Dies ist handlich, da Spieler in der "Partie" ggf. geteilt werden könnten.
        //if (player instanceof ClientPlayerEntity clientPlayer)
        //{
        //    MagicHand.LOGGER.info("Client Tick");
        //}


        if (PLAYER_TO_MAGICKDATA.containsKey(player) && player instanceof ServerPlayerEntity serverPlayer)
        {
            
            MagicHand.LOGGER.info("Server Tick");
        }




    }


    /**
     * Note: This is the Raw Player Nbt. It is possible that there is no serialized Data in here!
     * */
    public static void serializeDisconnectingPlayer(PlayerEntity player, NbtCompound playerNbt)
    {
        if (PLAYER_TO_MAGICKDATA.containsKey(player))
        {
            PlayerRuntimeData playerData = PLAYER_TO_MAGICKDATA.get(player);
            playerNbt.put(NbtConstants.MAGIC_NBT, playerData.serialize());

            PLAYER_TO_MAGICKDATA.remove(player);
        }
    }




    /**
     * Note: The NbtCompbound in this case is the Sub Nbt containin the Data; It can be read from directly!
     * */
    public static void deserializePlayer(PlayerEntity player, NbtCompound magickData)
    {
        PLAYER_TO_MAGICKDATA.put(player, new PlayerRuntimeData(player,magickData));



    }

    public static void registerPlayer(PlayerEntity player)
    {
        if (!PLAYER_TO_MAGICKDATA.containsKey(player))
        {
            PLAYER_TO_MAGICKDATA.put(player, new PlayerRuntimeData(player));
            sendS2CPacket(player);
        }

    }




    public static void updateClientPlayer(PlayerEntity player, NbtCompound nbt)
    {
        if (!(player instanceof ServerPlayerEntity))
        {
            PLAYER_TO_MAGICKDATA.put(player, new PlayerRuntimeData(player, nbt));
        }

    }




    public static void sendS2CPacket(PlayerEntity player)
    {
        if (player instanceof ServerPlayerEntity serverPlayer)
        {
            PacketByteBuf packet = PacketByteBufs.create();

            packet.writeNbt(PLAYER_TO_MAGICKDATA.get(player).serialize());

            ServerPlayNetworking.send(serverPlayer, PacketRegistrator.RUNTIMEDATA_S2C, packet);

        }
    }




}
