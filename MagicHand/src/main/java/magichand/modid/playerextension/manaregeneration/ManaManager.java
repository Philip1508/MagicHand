package magichand.modid.playerextension.manaregeneration;

import magichand.modid.MagicHand;
import magichand.modid.enchantments.MaximumManaEnchantment;
import magichand.modid.enchantments.RegenerateManaEnchantment;
import magichand.modid.playerextension.DataPipe;
import magichand.modid.playerextension.maskedconstants.PlayerDataSerializerNbtConstants;
import magichand.modid.statuseffect.StatusEffectRegistrator;
import magichand.modid.util.Rational;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ManaManager {


    private final DataPipe sharedData;


    // This is equal to 1%!
    private static int BASE_REGENERATION = 100;

    // These Constants define scaling behavior on additional mana and regeneration of mana.
    private int additionalRegenerationScalingBase = 25;
    private int additionalManaScalingBase = 25;

    // The Players current Mana
    private int mana;

    // The Maximum Mana of a player, before "temporary" increases of maximum Mana.
    private int baseMaxMana;
    private int maxMana;

    // This Rational represents the current decimal places part of the mana.


    private Rational fractionalMana;
    // This Rational represents the amount of fractional Mana added to the fractional Mana per Tick.
    private Rational regenerationalFactor;


    public ManaManager(@Nullable NbtCompound serializedManaManagerCompbound, DataPipe sharedData)
    {
        this.sharedData = sharedData;

        if (serializedManaManagerCompbound == null)
        {
            this.mana = 100;
            this.baseMaxMana = 100;
            this.maxMana = 100;
            this.fractionalMana = new Rational(0);
            this.regenerationalFactor = new Rational(0);
        }
        else
        {
            int mana = serializedManaManagerCompbound.getInt(PlayerDataSerializerNbtConstants.MANA);
            int maxMana = serializedManaManagerCompbound.getInt(PlayerDataSerializerNbtConstants.MAX_MANA);
            int bonusMaxMana = serializedManaManagerCompbound.getInt(PlayerDataSerializerNbtConstants.BONUS_MAX_MANA);

            this.mana = mana;
            this.baseMaxMana = maxMana;
            this.maxMana = bonusMaxMana;


            NbtCompound nbtFractional = serializedManaManagerCompbound.getCompound(PlayerDataSerializerNbtConstants.FRACTIONAL_MANA);
            NbtCompound nbtRegenerational = serializedManaManagerCompbound.getCompound(PlayerDataSerializerNbtConstants.MANA_REGENERATION_FRACTION);


            try {
                this.fractionalMana = Rational.rationalFromNbt(nbtFractional);
                this.regenerationalFactor = Rational.rationalFromNbt(nbtRegenerational);
            }
            catch (Exception e)
            {
                MagicHand.LOGGER.warn(e.getMessage());
                this.fractionalMana = new Rational(0);
                this.regenerationalFactor = new Rational(0);
            }





        }



    }


    public void tick()
    {
        if (fractionalMana == null || regenerationalFactor == null)
        {
            recalculateRegeneration(null);
            recalculateRegeneration(null);
        }

        // On each Tick we want to advance the amount of mana the player has regenerated. This is why we add the
        // Regenerational Factor, calculated in recalculateRegeneration(), to the fractionalMana.
        this.fractionalMana = fractionalMana.add(regenerationalFactor);

        // If we have regenerated an entire point of Mana, then we want to reset the fractional Mana (subtract all
        // whole points and add those to the integer Mana).
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

            // If we pass a certain mana threshold, we want to remove the mana burnout.
            if (sharedData.getManaBurnoutState())
            {
                if (mana > 15)
                {
                    sharedData.setManaBurnoutState(false);
                }

            }


        }



    }


    /**
     * This Method recalculates how much Mana per Tick is regenerated.
     * @param player - If given a player, it will scan for Enchantments and Potion Status Effects to consider in the
     *               calculation.
     */
    public void recalculateRegeneration(@Nullable PlayerEntity player)
    {

        int scaling = BASE_REGENERATION;

        AtomicInteger bonusRegeneration = new AtomicInteger(0);
        if (player != null)
        {

            // First, we shall scan the Players equipment for enhancing enchantments.
            Iterable<ItemStack> playerArmorItems = player.getArmorItems();

            playerArmorItems.forEach(itemStack -> {

                NbtList enchantments = itemStack.getEnchantments();
                Map<Enchantment, Integer> x = EnchantmentHelper.fromNbt(enchantments);

                x.forEach((enchantment, level) ->
                {
                    if (enchantment instanceof RegenerateManaEnchantment)
                    {
                        bonusRegeneration.addAndGet(level);
                    }
                });

            });

            // Now, we shall scan wether the player has consumed a Potion boosting Mana Regeneration.

            StatusEffectInstance manaRegenerationStatusEffectInstance = player
                    .getStatusEffect(StatusEffectRegistrator.MANA_REGENERATION);

            if (manaRegenerationStatusEffectInstance != null)
            {
                // We want to fetch the Amplifier of said Potion.
                int potionStrength = manaRegenerationStatusEffectInstance.getAmplifier();
                // We must increment by 1, as the default amplifier starts with 0.
                potionStrength += 1;

                // Each Potion gives 1% Bonus Regeneration.
                bonusRegeneration.addAndGet(potionStrength * 4 );

            }


        }

        scaling = scaling + additionalRegenerationScalingBase * bonusRegeneration.get();



        // The Mana in and on itself could be a Rational from the get go, this would save constructor call.
        Rational maxMana = new Rational(this.maxMana);

        // Der Denominator 10000 gibt hierbei die Genauigkeit an. So So ist das Ratio 100 + 25*S / 10000 Möglich
        Rational percentagePerSecond = maxMana.multiply(new Rational(scaling, 10000));

        // We Multiply by 1/20th to adjust to Minecraft Tick Speed
        Rational percentagePerTick = percentagePerSecond.multiply(new Rational(1, 20));

        percentagePerTick.shorten();

        this.fractionalMana = new Rational(0, percentagePerTick.getDenominator());
        this.regenerationalFactor = percentagePerTick;

        //System.out.println(scaling);
    }

    /**
     * This Method recalculates how much Mana the Player has / Should have.
     * @param player - If given a player, it will scan for Enchantments and Potion Status Effects to consider in the
     *               calculation.
     */
    public void recalculateMaximumMana(@Nullable PlayerEntity player)
    {

        AtomicInteger bonusMana = new AtomicInteger(0);
        if (player != null)
        {
            Iterable<ItemStack> playerArmorItems = player.getArmorItems();


            playerArmorItems.forEach(itemStack -> {

                NbtList enchantments = itemStack.getEnchantments();
                Map<Enchantment, Integer> x = EnchantmentHelper.fromNbt(enchantments);

                x.forEach((enchantment, level) ->
                {
                    if (enchantment instanceof MaximumManaEnchantment)
                    {
                        bonusMana.addAndGet(level);
                    }
                });

            });
        }

        maxMana = baseMaxMana + bonusMana.get() * additionalManaScalingBase;

        //System.out.println("Maximum Mana: " + maxMana);




    }

    /**
     * This Method serializes the ManaManager of a Player to an NbtCompound.
     * @return NbtCompound - Serialized ManaManager
     */
    public NbtCompound serialize()
    {
        NbtCompound serializedManaManager = new NbtCompound();
        serializedManaManager.putInt(PlayerDataSerializerNbtConstants.MANA, this.mana);
        serializedManaManager.putInt(PlayerDataSerializerNbtConstants.MAX_MANA, this.baseMaxMana);
        serializedManaManager.putInt(PlayerDataSerializerNbtConstants.BONUS_MAX_MANA, this.maxMana);

        serializedManaManager.put(PlayerDataSerializerNbtConstants.FRACTIONAL_MANA,
                Rational.rationalToNbt(fractionalMana) );
        serializedManaManager.put(PlayerDataSerializerNbtConstants.MANA_REGENERATION_FRACTION,
                Rational.rationalToNbt(regenerationalFactor) );

        return serializedManaManager;

    }


    /**
     * Method to decrease the mana of a player.
     * @param amount - Mana to decrease.
     * @return Boolean: Was mana subtracted? False iff mana - amount < 0.
     */
    public boolean decreaseMana(int amount)
    {
        if (mana - amount >= 0)
        {
            setMana(mana - amount);
            return true;
        }

        return false;
    }

    public int getMana()
    {
        return mana;
    }

    public void setMana(int mana)
    {
        this.mana = mana;
    }

    public void increaseMana(int amount)
    {
        if (amount + this.mana < maxMana)
        {
            setMana(amount+this.mana);
        }
        else
        {
            this.mana = maxMana;
        }
    }

    public int getMaxMana()
    {
        return maxMana;
    }


    public Rational getFullManaAsRational()
    {
        if (maxMana == 0)
        {
            return new Rational(mana, 100);
        }

        return new Rational(mana, maxMana);
    }
    public Rational getFractionalMana()
    {
        return fractionalMana;
    }
    public Rational getRegenerationalFactor()
    {
        return regenerationalFactor;
    }



}
