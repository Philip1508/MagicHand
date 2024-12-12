package magichand.modid.enchantments;

import magichand.modid.MagicHand;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentRegistrator {

    public static Enchantment MANA_REGENERATION_ENCHANTMENT;
    public static Enchantment MANA_MAXIMUM_ENCHANTMENT;


    public static void initializeAndRegister()
    {



        EquipmentSlot[] arr = new EquipmentSlot[]{EquipmentSlot.CHEST,
                EquipmentSlot.FEET,
                EquipmentSlot.HEAD,
                EquipmentSlot.LEGS
        };

        MANA_REGENERATION_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MagicHand.MOD_ID, "mana-regeneration-enchantment"),
                new RegenerateManaEnchantment(Enchantment.Rarity.COMMON, EnchantmentTarget.ARMOR, arr));


        MANA_MAXIMUM_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MagicHand.MOD_ID, "mana-maximum-enchantment"),
                new MaximumManaEnchantment(Enchantment.Rarity.COMMON, EnchantmentTarget.ARMOR, arr));


    }

}
