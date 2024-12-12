package magichand.modid.statuseffect;

import magichand.modid.MagicHand;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class StatusEffectRegistrator {

    public static StatusEffect MANA_REGENERATION;
    public static StatusEffect MANA_INSTANT;


    public static void initializeAndRegister()
    {

        MANA_REGENERATION = Registry.register(Registries.STATUS_EFFECT, new Identifier(MagicHand.MOD_ID, "mana_regeneration" ),
                new ExtendedStatusEffect(StatusEffectCategory.BENEFICIAL, 16777215));

        MANA_INSTANT = Registry.register(Registries.STATUS_EFFECT, new Identifier(MagicHand.MOD_ID, "mana_instant" ),
                new ExtendedInstantStatusEffect(StatusEffectCategory.BENEFICIAL, 16777215));

    }

}
