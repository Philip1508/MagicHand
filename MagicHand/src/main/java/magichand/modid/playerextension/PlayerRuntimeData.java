package magichand.modid.playerextension;

import magichand.modid.MagicHand;
import magichand.modid.networking.PacketRegistrator;
import magichand.modid.networking.S2CUpdater;
import magichand.modid.playerextension.activecast.CastMachine;
import magichand.modid.playerextension.manaregeneration.ManaManager;
import magichand.modid.playerextension.maskedconstants.ClientPlayerRepresentationConstants;
import magichand.modid.playerextension.maskedconstants.PlayerDataSerializerNbtConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * This Class represents a Data Structure which holds the runtime Data of a Player.
 * This Includes a reference to the player himself in order to grant future access to the inventory.
 * In Addition, it holds a players ManaManager.
 * It will most likely also hold a spell inventory in the future.
 */
public class PlayerRuntimeData {

    // This reference is necessary for enabling tasks such as scanning the inventory.
    private final PlayerEntity player;


    private final DataPipe sharedData;
    private final ManaManager manaManager;
    private final CastMachine castMachine;

    private MagickaMachineState state = MagickaMachineState.MANA_PASSIVE_REGENERATION;





    // ToDo; Mask Constructors via FactoryPattern for uniform constructor!
    public PlayerRuntimeData(PlayerEntity player, @Nullable NbtCompound magickData) {
        this.player = player;

        if (!(player instanceof ServerPlayerEntity))
        {
            throw new IllegalArgumentException("Player in PlayerRuntimeData must not be a ClientPlayer!");
        }

        this.sharedData = new DataPipe();

        if (magickData == null)
        {
            this.manaManager = new ManaManager(null, sharedData);
        }
        else
        {
            NbtCompound manaManagerCompbound = magickData.getCompound(PlayerDataSerializerNbtConstants.MANA_MANAGER);
            this.manaManager = new ManaManager(manaManagerCompbound, sharedData);
        }

        this.castMachine = new CastMachine(player, sharedData);


    }






    /**
     * Serializes the players Runtime Info to an NbtCompbound
     *
     * */
    public NbtCompound serialize()
    {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put(PlayerDataSerializerNbtConstants.MANA_MANAGER, manaManager.serialize());


        return nbtCompound;

    }




    public ManaManager getManaManager()
    {
        return this.manaManager;
    }
    public CastMachine getCastMachine(){
        return this.castMachine;
    }


    /**
     * This method sets the state of the machine.
     * It also sends an update packet to the client player.
     * @param state - New State.
     */
    public void setState(MagickaMachineState state)
    {
        // Server Side Update
        this.state = state;

        // Client Synch Message Update!
        S2CUpdater.serverToClientUpdateState(player, state);


    }


    /**
     * This Method returns the State the machine is currently in.
     * @return - MagickaMachineState
     */
    public MagickaMachineState getState()
    {
        return state;
    }




    /**
     * This method returns a boolean, wether the server must send a full refresh package because a player has just
     * logged on or not.
     * @return - Boolean: Login Refresh Required?
     */
    public boolean loginRefresh()
    {
        return sharedData.loginRefresh();
    }


    /**
     * This method returns the corresponding Player.
     * @return - PlayerEntity
     */
    public PlayerEntity getPlayer()
    {
        return this.player;
    }
    

}
