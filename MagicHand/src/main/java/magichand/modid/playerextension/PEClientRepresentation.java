package magichand.modid.playerextension;

import magichand.modid.playerextension.maskedconstants.ClientPlayerRepresentationConstants;
import magichand.modid.util.Rational;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PEClientRepresentation {

    public final PlayerEntity player;

    static final int DEFAULT_LIFETIME = 6*20;
    int lifetime = DEFAULT_LIFETIME;

    public Rational mana;
    public Rational manaRegeneration;
    public Rational manaFractional;
    public MagickaMachineState state;


    public PEClientRepresentation(PlayerEntity player, Rational mana, Rational manaRegeneration, Rational manaFractional, MagickaMachineState state)
    {
        this.player = player;

        this.mana = mana;
        this.manaRegeneration = manaRegeneration;
        this.manaFractional = manaFractional;
        this.state = state;

    }


    /**
     * Checks if the ClientRepresentation is dead.
     * @return - True, iff too old, false if not.
     */
    public boolean notRefreshed()
    {
        lifetime -= 1;
        if (lifetime < 0)
        {
            return true;
        }
        return false;
    }

    /**
     * Refreshes the Lifetime of the ClientRepresentation. Called on every incoming UpdateMessage.
     */
    public void refresh()
    {
        lifetime = DEFAULT_LIFETIME;
    }


}
