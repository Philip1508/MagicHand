package magichand.modid.playerextension;

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

    private MagickaMachineState state = MagickaMachineState.MANA_PASSIVE_REGENERATION;

    private int regenerationCooldown = (20) * 3;

    




    public PlayerRuntimeData(PlayerEntity player, NbtCompound magickData) {
        this.player = player;

        NbtCompound manaManagerCompbound = magickData.getCompound(NbtConstants.MANA_MANAGER);

        this.manaManager = new ManaManager(manaManagerCompbound);



    }



    public PlayerRuntimeData(PlayerEntity player)
    {
        this.player = player;
        this.manaManager = new ManaManager(null);
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
    


    public void setRegenerationCooldown(int cooldown)
    {
        this.regenerationCooldown = cooldown;
    }
    public int getRegenerationCooldown()
    {
        return this.regenerationCooldown;
    }

    public void setState(MagickaMachineState state)
    {
        this.state = state;
    }
    public MagickaMachineState getState()
    {
        return state;
    }
    

    

}
