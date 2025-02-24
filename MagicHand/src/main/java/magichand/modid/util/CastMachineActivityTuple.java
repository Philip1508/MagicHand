package magichand.modid.util;

/**
 * This Class contains information which hand of the player is firing.
 * Attributes are public, and accessed publicly, but are also final.
 */
public class CastMachineActivityTuple {
    public final boolean mainHandActive;
    public final boolean offHandActive;

    /**
     * Constructor of CastMachineActivityTuple.
     * @param mainHandActive - Boolean, wether mainHand is actively firing.
     * @param offHandActive - Boolean, wether offHand is actively firing.
     */
    public CastMachineActivityTuple(boolean mainHandActive, boolean offHandActive) {
        this.mainHandActive = mainHandActive;
        this.offHandActive = offHandActive;
    }


}
