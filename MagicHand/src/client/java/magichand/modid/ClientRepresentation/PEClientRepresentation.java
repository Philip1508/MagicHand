package magichand.modid.ClientRepresentation;

import magichand.modid.items.spellcatalysts.AbstractSpellCatalyst;
import magichand.modid.playerextension.MagickaMachineState;
import magichand.modid.util.Rational;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * This Class contains the Data a client needs to individually represent his Data on the HUD.
 * Since this data is only used to consume, it is not protected; Everything is public without getters and setters.
 *
 */
public class PEClientRepresentation {

    public final UUID sessionId;


    static final int DEFAULT_LIFETIME = 6*20;
    int lifetime = DEFAULT_LIFETIME;

    public Rational mana;
    public Rational manaRegeneration;
    public Rational manaFractional;
    public MagickaMachineState state;


    private static final int DEFAULT_HIDEAWAY_TIME = 3*20;
    private int hideawayTime = DEFAULT_HIDEAWAY_TIME;
    public boolean hidden = false;


    /**
     * Constructor of PEClientRepresentation
     * @param player - PlayerEntity
     * @param mana - Players Mana as Rational
     * @param manaRegeneration - Mana Regeneration Rational
     * @param manaFractional - Mana Fractional as Rational (partially regenerated)
     * @param state - State of the Players machine.
     */
    public PEClientRepresentation(UUID sessionId, Rational mana, Rational manaRegeneration, Rational manaFractional, MagickaMachineState state)
    {


        this.sessionId = sessionId;


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


    public void hudInactivityCheck(ClientPlayerEntity player)
    {
        boolean mainHandHolding = player.getMainHandStack().getItem() instanceof AbstractSpellCatalyst;
        boolean offHandHolding = player.getOffHandStack().getItem() instanceof AbstractSpellCatalyst;

        if (!mainHandHolding && !offHandHolding && !hidden && hideawayTime >= 0)
        {
            hideawayTime -= 1;
            if (hideawayTime < 0) {hidden = true;}
        }


        if (hidden && mainHandHolding || offHandHolding)
        {
            hidden = false;
            hideawayTime = DEFAULT_HIDEAWAY_TIME;
        }
    }

    public void unhide()
    {
        hidden = false;
        hideawayTime = DEFAULT_HIDEAWAY_TIME;
    }




}
