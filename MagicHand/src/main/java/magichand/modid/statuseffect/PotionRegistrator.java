package magichand.modid.statuseffect;

import magichand.modid.MagicHand;
import magichand.modid.util.TimeCalculator;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PotionRegistrator {


    public static final Potion MANA_REGENERATION_POTION = new Potion(
            new StatusEffectInstance(StatusEffectRegistrator.MANA_REGENERATION, TimeCalculator.minutesToTick(8)));


    public static final Potion MANA_INSTANT_POTION = new Potion(
            new StatusEffectInstance(StatusEffectRegistrator.MANA_INSTANT, 1));


    public static void initializeAndRegister()
    {
        Registry.register(Registries.POTION, new Identifier(MagicHand.MOD_ID, "mana-regeneration-potion"), MANA_REGENERATION_POTION);
        Registry.register(Registries.POTION, new Identifier(MagicHand.MOD_ID, "mana-instant-potion"), MANA_INSTANT_POTION);
    }

}
