package magichand.modid.playerextension;

import magichand.modid.playerextension.maskedconstants.ClientPlayerRepresentationConstants;
import magichand.modid.util.Rational;
import net.minecraft.nbt.NbtCompound;

public class PEClientRepresentation {

    static final int DEFAULT_LIFETIME = 6*20;
    int lifetime;

    public Rational mana;
    public Rational manaRegeneration;
    public Rational manaFractional;
    public MagickaMachineState state;


    public PEClientRepresentation(Rational mana, Rational manaRegeneration, Rational manaFractional, MagickaMachineState state)
    {
        this.lifetime = 6*20;



        this.mana = mana;
        this.manaRegeneration = manaRegeneration;
        this.manaFractional = manaFractional;
        this.state = state;

    }



    public boolean notRefreshed()
    {
        lifetime -= 1;
        if (lifetime < 0)
        {
            return true;
        }
        return false;
    }

    public void refresh()
    {
        lifetime = DEFAULT_LIFETIME;
    }


}
