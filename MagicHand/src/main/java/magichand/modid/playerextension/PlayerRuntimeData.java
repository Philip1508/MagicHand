package magichand.modid.playerextension;

import magichand.modid.playerextension.activecast.CastMachine;
import magichand.modid.playerextension.manaregeneration.ManaManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

/**
 * This Class represents a Data Structure which holds the runtime Data of a Player.
 * This Includes a reference to the player himself in order to grant future access to the inventory.
 * In Addition, it holds a players ManaManager.
 * It will most likely also hold a spell inventory in the future.
 */
public class PlayerRuntimeData {

    // This reference is necessary for enabling tasks such as scanning the inventory.
    private PlayerEntity player;
    private ManaManager manaManager;

    private CastMachine castMachine;

    private MagickaMachineState state = MagickaMachineState.MANA_PASSIVE_REGENERATION;

    private int regenerationCooldown = (20) * 3;

    




    public PlayerRuntimeData(PlayerEntity player, NbtCompound magickData) {
        this.player = player;

        NbtCompound manaManagerCompbound = magickData.getCompound(NbtConstants.MANA_MANAGER);

        this.manaManager = new ManaManager(manaManagerCompbound);
        this.castMachine = new CastMachine(player);


    }



    public PlayerRuntimeData(PlayerEntity player)
    {
        this.player = player;
        this.manaManager = new ManaManager(null);
        this.castMachine = new CastMachine(player);
    }



    /**
     * Serializes the players Runtime Info to an NbtCompbound
     *
     * */
    public NbtCompound serialize()
    {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put(NbtConstants.MANA_MANAGER, manaManager.serialize());


        return nbtCompound;

    }




    public ManaManager getManaManager()
    {
        return this.manaManager;
    }
    public CastMachine getCastMachine(){return this.castMachine;}


    public void setRegenerationCooldown(int cooldown)
    {
        this.regenerationCooldown = cooldown;
    }


    public void setState(MagickaMachineState state)
    {
        this.state = state;
        // ToDo; Inform Client of new State!
    }
    public MagickaMachineState getState()
    {
        return state;
    }



    

}
