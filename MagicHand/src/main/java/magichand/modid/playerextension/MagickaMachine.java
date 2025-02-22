package magichand.modid.playerextension;

import magichand.modid.MagicHand;
import magichand.modid.networking.PacketRegistrator;
import magichand.modid.networking.S2CUpdater;
import magichand.modid.playerextension.activecast.CastMachine;
import magichand.modid.playerextension.manaregeneration.ManaManager;
import magichand.modid.playerextension.maskedconstants.ClientPlayerRepresentationConstants;
import magichand.modid.playerextension.maskedconstants.PlayerDataSerializerNbtConstants;
import magichand.modid.util.Rational;
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

    public static PEClientRepresentation clientRepresentation;





    public static void tick(PlayerEntity player)
    {
        boolean isClient = player.getWorld().isClient();
        boolean isServer = !isClient;




        if (isClient) {clientTick(player);}

        if (isServer) {serverTick(player);}


    }


    /**
     * Note: This is the Raw Player Nbt. It is possible that there is no serialized Data in here!
     * */
    public static void serializePlayer(PlayerEntity player, NbtCompound playerNbt)
    {
        if (PLAYER_TO_MAGICKDATA.containsKey(player))
        {
            PlayerRuntimeData playerData = PLAYER_TO_MAGICKDATA.get(player);
            playerNbt.put(PlayerDataSerializerNbtConstants.MAGIC_NBT, playerData.serialize());
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
            S2C_FullTransmission(player);
        }

    }





    public static void S2C_FullTransmission(PlayerEntity player)
    {
        if (player instanceof ServerPlayerEntity serverPlayer)
        {
            PlayerRuntimeData playerRuntimeData = PLAYER_TO_MAGICKDATA.get(player);
            S2CUpdater.serverToClientFullSynch(playerRuntimeData);
        }
    }








    public static PlayerRuntimeData getPlayerRuntimeData(PlayerEntity player)
    {
        return PLAYER_TO_MAGICKDATA.get(player);
    }


    public static void clientTick(PlayerEntity cPlayer)
    {

        if (cPlayer instanceof ServerPlayerEntity || clientRepresentation == null) {return;}
        if(!(cPlayer.getId() == clientRepresentation.player.getId())) {return;}


        if (clientRepresentation.state == MagickaMachineState.MANA_ACTIVE_CAST)
        {
            MagicHand.LOGGER.info("Active State properly registred.");
        }


        if (clientRepresentation.state == MagickaMachineState.MANA_PASSIVE_REGENERATION)
        {
            clientRepresentation.manaFractional = clientRepresentation.manaFractional.add(clientRepresentation.manaRegeneration);
            if (clientRepresentation.manaFractional.greaterOne())
            {
                int regeneratedPoints = clientRepresentation.manaFractional.getWholeAndFlatten();
                if (clientRepresentation.mana.getNumerator() + regeneratedPoints > clientRepresentation.mana.getDenominator())
                {
                    clientRepresentation.mana.setNumerator(clientRepresentation.mana.getDenominator());
                }
                else
                {
                    clientRepresentation.mana.setNumerator(clientRepresentation.mana.getNumerator() + regeneratedPoints);
                }

            }
        }




        // Destroy the representation if it's not refreshed...
        // This must happen at LAST.
        if (clientRepresentation.notRefreshed())  {
            clientRepresentation = null;
        }
    }


    public static void serverTick(PlayerEntity player)
    {
        // If the player isn't a magic user (not in Relation) then do nothing (Safety).
        if (!PLAYER_TO_MAGICKDATA.containsKey(player))  {return;}

        PlayerRuntimeData playerData = PLAYER_TO_MAGICKDATA.get(player);

        if (playerData.loginRefresh())
        {
            S2C_FullTransmission(player);
        }


        switch (playerData.getState())
        {
            case MANA_ACTIVE_CAST ->
            {


                if (player instanceof ServerPlayerEntity)
                {
                    playerData.getCastMachine().tick();
                }


            }


            case MANA_PASSIVE_REGENERATION ->
            {
                // Perhaps in the future we can make the server skip ticks if it is running behind, whilst smoothly
                // regenerating on the client.
                playerData.getManaManager().tick();

                // Update maximum Mana and Mana Regeneration every 5 Seconds. "Expensive Update"
                if (player.getWorld().getTime() % (20*5) == 0 && player instanceof ServerPlayerEntity)
                {
                    recalculateManaManager(player);
                }
            }


        }
    }



    private static void recalculateManaManager(PlayerEntity player)
    {
        PLAYER_TO_MAGICKDATA.get(player).getManaManager().recalculateRegeneration(player);
        PLAYER_TO_MAGICKDATA.get(player).getManaManager().recalculateMaximumMana(player);
        S2C_FullTransmission(player);
    }

    public static void recalculateManaManagerSafe(PlayerEntity player)
    {
        if (PLAYER_TO_MAGICKDATA.containsKey(player))
        {
            recalculateManaManager(player);
        }


    }





}
