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


        // If the player isn't a magic user (not in Relation) then do nothing (Safety).
        if (!PLAYER_TO_MAGICKDATA.containsKey(player))  {return;}

            PlayerRuntimeData playerData = PLAYER_TO_MAGICKDATA.get(player);

            switch (playerData.getState())
            {
                case MANA_ACTIVE_CAST ->
                {
                    // Both server and client want know when it is time to regenerate again
                    playerData.setRegenerationCooldown(playerData.getRegenerationCooldown() - 1);
                    if (playerData.getRegenerationCooldown() == 0)
                    {
                        playerData.setState(MagickaMachineState.MANA_PASSIVE_REGENERATION);
                    }

                    // Spell Execution Code?

                }


                case MANA_PASSIVE_REGENERATION ->
                {
                    // This Tick must also be called on the client to ensure smooth progress of the mana bar.
                    // Perhaps in the future we can make the server skip ticks if it is running behind, whilst smoothly
                    // regenerating on the client.
                    playerData.getManaManager().tick();

                    // Update maximum Mana and Mana Regeneration every 1.5 Seconds. "Expensive Update"
                    if (player.getWorld().getTime() % 30 == 0 && player instanceof ServerPlayerEntity)
                    {
                        PLAYER_TO_MAGICKDATA.get(player).getManaManager().recalculateRegeneration(player);
                        PLAYER_TO_MAGICKDATA.get(player).getManaManager().recalculateMaximumMana(player);
                        sendS2CPacket(player);
                    }
                }


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


            // Das darf hier nicht passieren.
            // PLAYER_TO_MAGICKDATA.remove(player);
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








    public static PlayerRuntimeData getPlayerRuntimeData(PlayerEntity player)
    {
        return PLAYER_TO_MAGICKDATA.get(player);
    }


    // ToDo; Remove magic numbers.
    public static void activatePlayerManaRegenerationCooldown(PlayerEntity player)
    {
        PLAYER_TO_MAGICKDATA.get(player).setState(MagickaMachineState.MANA_ACTIVE_CAST);

        PLAYER_TO_MAGICKDATA.get(player).setRegenerationCooldown(20*3);;

    }


}
