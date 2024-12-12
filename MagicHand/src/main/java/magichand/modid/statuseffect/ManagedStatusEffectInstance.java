package magichand.modid.statuseffect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


/***
 * This is the ManagedStatusEffectInstance
 * This class is supposed to be an extension of the StatusEffectInstance that provides logic that makes it possible
 * to extend the fire of a Flame-Spray or extend the potion duration of a "Brewmaster" Spell.
 * Another Example would be the frost spray stacking to Slowness 2, deteriorating to Slowness 1.
 * Serialization will most likely be a big headache on this one.
 */
public class ManagedStatusEffectInstance extends StatusEffectInstance {
    public ManagedStatusEffectInstance(StatusEffect type) {
        super(type);
    }

    public ManagedStatusEffectInstance(StatusEffect type, int duration) {
        super(type, duration);
    }

    public ManagedStatusEffectInstance(StatusEffect type, int duration, int amplifier) {
        super(type, duration, amplifier);
    }

    public ManagedStatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean visible) {
        super(type, duration, amplifier, ambient, visible);
    }

    public ManagedStatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        super(type, duration, amplifier, ambient, showParticles, showIcon);
    }

    public ManagedStatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, @Nullable StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        super(type, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
    }

    public ManagedStatusEffectInstance(StatusEffectInstance instance) {
        super(instance);
    }
}
