package magichand.modid.items.spellcatalysts;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SpellCatalyst extends AbstractSpellCatalyst {


    public SpellCatalyst(Settings settings) {
        super(settings);
    }




    @Override
    public float getScaling() {
        return 1.0f;
    }


}
