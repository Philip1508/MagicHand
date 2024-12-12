package magichand.modid.statuseffect;

import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.manaregeneration.ManaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public class ExtendedStatusEffect extends StatusEffect {
    protected ExtendedStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }


    @Override
    public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        System.out.println("Hello?");
        if (this == StatusEffectRegistrator.MANA_INSTANT && target instanceof PlayerEntity player)
        {
            ManaManager manaManager = MagickaMachine.getPlayerRuntimeData(player).getManaManager();

            // Restores 20% Mana.
            int restoredMana = (int) (manaManager.getMaxMana() * 0.2d);
            manaManager.increaseMana(restoredMana);

        }


    }
}
