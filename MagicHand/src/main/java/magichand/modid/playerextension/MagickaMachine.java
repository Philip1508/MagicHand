package magichand.modid.playerextension;

import magichand.modid.MagicHand;
import magichand.modid.items.spellcatalysts.AbstractSpellCatalyst;
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
import net.minecraft.network.encryption.ClientPlayerSession;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This Class is the Interface for the Code Injection.
 * It decouples the Code Injection from the Code that must be injected for clean workflow, so that the injection only
 * "directly" injects function calls of this Machine.
 * It statically couples each player with his RunTimeData, which itself is not static anymore.
 * ToDo; Find a clean method to remove disconnected Players from PLAYER_TO_MAGICKDATA to avoid leaking memory.
 */
public abstract class MagickaMachine {
    // Server Side Map containing the Extended Runtime Data for each Player (Key).
    private static final Map<UUID, PlayerRuntimeData> PLAYER_TO_MAGICKDATA = new HashMap<>();

    /**
     * This Function is the extended Tick Function of a PlayerEntity.
     * It detects wether we are on the Server or Clientside and calculates the next step of the ExtendedData.
     * @param player - Given from PlayerEntity.tick()
     */
    public static void tick(ServerPlayerEntity player)
    {
        boolean isClient = player.getWorld().isClient();
        boolean isServer = !isClient;



        if (isServer) {serverTick(player);}


    }


    /**
     * This is the Extension of the Serializing Function from the PlayerEntityMixin.
     * It serializes the PlayerExtension to NbtCompbound and saves it in the Players mainNbt.
     * @param player
     * @param playerNbt
     */
    public static void serializePlayer(PlayerEntity player, NbtCompound playerNbt)
    {

        if (PLAYER_TO_MAGICKDATA.containsKey(player.getUuid()))
        {
            PlayerRuntimeData playerData = PLAYER_TO_MAGICKDATA.get(player.getUuid());

            playerNbt.put(PlayerDataSerializerNbtConstants.MAGIC_NBT, playerData.serialize());
        }

    }


    /**
     * This Method deserializes PlayerRuntimeData from a given Nbt.
     * @param player
     * @param magickData
     */
    public static void deserializePlayer(PlayerEntity player, NbtCompound magickData)
    {
        PLAYER_TO_MAGICKDATA.put(player.getUuid(), new PlayerRuntimeData(magickData));
    }

    /**
     * This Method registers a (new) Player to the MagickaMachine, iff he isn't registered in the first place.
     *
     * @param player - Player to become a magic user.
     */
    public static void registerPlayer(ServerPlayerEntity player)
    {
        if (!PLAYER_TO_MAGICKDATA.containsKey(player.getUuid()))
        {
            PLAYER_TO_MAGICKDATA.put(player.getUuid(), new PlayerRuntimeData(null));

            player.sendMessage(Text.of("A new sense has awakened inside you..."));
            player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 0.8f, 0.5f);

            S2CUpdater.serverToClientFullSynch(player, PLAYER_TO_MAGICKDATA.get(player.getUuid()));
        }

    }


    /**
     * This method fetches the PlayerRuntimeData for further use.
     * @param player - PlayerEntity of which the corresponding data needs to be fetched.
     * @return PlayerRuntimeData.
     * ToDo; This is unsafe. This Data could be null.
     */
    public static PlayerRuntimeData getPlayerRuntimeData(PlayerEntity player)
    {
        return PLAYER_TO_MAGICKDATA.get(player.getUuid());
    }




    /**
     * This Method processes a tick on the ServerSide.
     * @param player
     */
    private static void serverTick(ServerPlayerEntity player)
    {
        // If the player isn't a magic user (not in Relation) then do nothing (Safety).
        if (!PLAYER_TO_MAGICKDATA.containsKey(player.getUuid()) || player.getWorld().isClient() )  {return;}

        PlayerRuntimeData playerData = PLAYER_TO_MAGICKDATA.get(player.getUuid());

        // If the ClientSideRepresentation has not been established yet, do it.
        if (playerData.loginRefresh()) {S2CUpdater.serverToClientFullSynch(player, playerData);}


        switch (playerData.getState())
        {
            case MANA_ACTIVE_CAST ->
            {
                playerData.getCastMachine().tick(player);
            }


            case MANA_PASSIVE_REGENERATION ->
            {
                // Perhaps in the future we can make the server skip ticks if it is running behind, whilst smoothly
                // regenerating on the client.
                playerData.getManaManager().tick();

                // Update maximum Mana and Mana Regeneration every 5 Seconds. "Expensive Update"
                if (player.getWorld().getTime() % (20*5) == 0)
                {
                    recalculateManaManager(player);
                }
            }


        }
    }


    /**
     * This Method initiates a recalculation of the maximum mana and its regeneration.
     * Necessary to make items such as Potions take effect immediately.
     * This method is private, because it asserts the player is registered.
     * @param player - Player whose Mana needs to be recalculated.
     */
    private static void recalculateManaManager(ServerPlayerEntity player)
    {
        PlayerRuntimeData playerRuntimeData = PLAYER_TO_MAGICKDATA.get(player.getUuid());
        playerRuntimeData.getManaManager().recalculateRegeneration(player);
        playerRuntimeData.getManaManager().recalculateMaximumMana(player);
        S2CUpdater.serverToClientFullSynch(player, playerRuntimeData);
    }

    /**
     * This Method provides public access to recalculateManaManager with null checking.
     * It will refresh the Players ManaManager and send an update to the client.
     * @param player - PlayerEntity which needs to be recalculated.
     */
    public static void recalculateManaManagerSafe(PlayerEntity player)
    {
        if (player instanceof ServerPlayerEntity serverPlayer)
        {
            if (PLAYER_TO_MAGICKDATA.containsKey(player.getUuid()))
            {
                recalculateManaManager(serverPlayer);
            }
            else
            {
                registerPlayer(serverPlayer);
            }
        }

    }


    /**
     * This Method checks wether the given player is part of this API or not.
     * This allows external code to quickly check for null.
     * @param sPlayer - Player to Null Check.
     * @return - Boolean: Is the PlayerRuntimeData of a given player non-null?
     */
    public static boolean isPlayerMagicuser(ServerPlayerEntity sPlayer)
    {
        return PLAYER_TO_MAGICKDATA.containsKey(sPlayer.getUuid());
    }





}
