package magichand.modid.playerextension;

import magichand.modid.playerextension.manaregeneration.ManaManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerRuntimeData {

    // This reference is necessary for doing stuff like scanning the inventory.
    PlayerEntity player;
    ManaManager manaManager;

    




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
    



    

    

}
