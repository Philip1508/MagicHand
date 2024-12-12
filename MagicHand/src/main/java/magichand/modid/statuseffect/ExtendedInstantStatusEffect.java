package magichand.modid.statuseffect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ExtendedInstantStatusEffect extends ExtendedStatusEffect {
    protected ExtendedInstantStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }


    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 1;
    }


}
