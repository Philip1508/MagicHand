package magichand.modid.items.spellcatalysts;

import net.minecraft.item.Item;

public abstract class AbstractSpellCatalyst extends Item implements RenderAbstractionInterface {


    public AbstractSpellCatalyst(Settings settings) {
        super(settings);
    }




    public abstract float getScaling();




}
