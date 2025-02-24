package magichand.modid.playerextension;

import magichand.modid.playerextension.maskedconstants.ClientPlayerRepresentationConstants;
import magichand.modid.util.Rational;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This Class contains the Data a client needs to individually represent his Data on the HUD.
 * Since this data is only used to consume, it is not protected; Everything is public without getters and setters.
 *
 */
public class PEClientRepresentation {

    public final PlayerEntity player;
    static final int DEFAULT_LIFETIME = 6*20;
    int lifetime = DEFAULT_LIFETIME;

    public Rational mana;
    public Rational manaRegeneration;
    public Rational manaFractional;
    public MagickaMachineState state;


    /**
     * Constructor of PEClientRepresentation
     * @param player - PlayerEntity
     * @param mana - Players Mana as Rational
     * @param manaRegeneration - Mana Regeneration Rational
     * @param manaFractional - Mana Fractional as Rational (partially regenerated)
     * @param state - State of the Players machine.
     */
    public PEClientRepresentation(PlayerEntity player, Rational mana, Rational manaRegeneration, Rational manaFractional, MagickaMachineState state)
    {
        if (player instanceof ServerPlayerEntity)
        {
            throw new IllegalArgumentException
                    ("The Player of the PEClientRepresentation must not be a ServerPlayerEntity" +
                    ". Something has gone wrong.");
        }

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
