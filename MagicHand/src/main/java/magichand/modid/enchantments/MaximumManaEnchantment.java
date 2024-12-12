package magichand.modid.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class MaximumManaEnchantment extends Enchantment {
    public MaximumManaEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(rarity, target, slotTypes);
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }


    // This must not interfere with Vanilla Gameplay.
    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }
}
