package magichand.modid.playerextension;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerRuntimeData {

    // This reference is necessary for doing stuff like scanning the inventory.
    PlayerEntity player;

    private int mana;
    private int maxMana;




    public PlayerRuntimeData(PlayerEntity player, NbtCompound magickData) {
        this.player = player;

        this.mana = magickData.getInt(NbtConstants.MANA);
        this.maxMana = magickData.getInt(NbtConstants.MAX_MANA);
    }



    public PlayerRuntimeData(PlayerEntity player)
    {
        this.player = player;
        this.mana = 100;
        this.maxMana = 100;

    }



    /**
     * Serializes the players Runtime Info to an NbtCompbound
     *
     * */
    public NbtCompound serialize()
    {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt(NbtConstants.MANA, mana);
        nbtCompound.putInt(NbtConstants.MAX_MANA, maxMana);

        return nbtCompound;

    }

}
