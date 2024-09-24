package magichand.modid.playerextension.manaregeneration;

import magichand.modid.playerextension.NbtConstants;
import magichand.modid.util.Rational;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class ManaManager {

    // This is equal to 1%!
    private static int BASE_REGENERATION = 100;

    volatile private int  dummyScaling = 8;

    private int mana;
    private int maxMana;

    private Rational fractionalMana;
    private Rational regenerationalFactor;


    public ManaManager(@Nullable NbtCompound serializedManaManagerCompbound)
    {
        if (serializedManaManagerCompbound == null)
        {
            this.mana = 100;
            this.maxMana = 100;
        }
        else
        {
            this.mana = serializedManaManagerCompbound.getInt(NbtConstants.MANA);
            this.maxMana = serializedManaManagerCompbound.getInt(NbtConstants.MAX_MANA);
        }



    }



    public void tick()
    {
        if (fractionalMana == null || regenerationalFactor == null)
        {
            recalculateRegeneration();
        }

        this.fractionalMana = fractionalMana.add(regenerationalFactor);

        if (fractionalMana.greaterOne())
        {
            int regeneratedPoints = fractionalMana.getWholeAndFlatten();
            if (this.mana + regeneratedPoints > maxMana)
            {
                this.mana = maxMana;
            }
            else
            {
                this.mana += regeneratedPoints;
            }

        }

    }



    public void recalculateRegeneration()
    {

        int scaling = BASE_REGENERATION + 25 * dummyScaling;

        Rational maxMana = new Rational(this.maxMana);

        Rational percentagePerSecond = maxMana.multiply(new Rational(scaling, 10000));

        Rational percentagePerTick = percentagePerSecond.multiply(new Rational(1, 20));

        percentagePerTick.shorten();

        this.fractionalMana = new Rational(0, percentagePerTick.getDenominator());
        this.regenerationalFactor = percentagePerTick;
    }


    public NbtCompound serialize()
    {
        NbtCompound serializedManaManager = new NbtCompound();
        serializedManaManager.putInt(NbtConstants.MANA, this.mana);
        serializedManaManager.putInt(NbtConstants.MAX_MANA, this.maxMana);
        return serializedManaManager;

    }



    public void decreaseMana(int amount)
    {
            if (amount <= mana)
        {
            mana -= amount;
        }

    }

    public int getMana()
    {
        return mana;
    }




}
