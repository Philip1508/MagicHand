package magichand.modid.items;

import magichand.modid.MagicHand;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ItemRegistrator {



    private static final List<Item> ALL_ITEMS = new ArrayList<>();

    public static Item DEBUG_HAND;
    public static Item DEBUG_SPRAY_HAND;


    public static void initializeAndRegister()
    {


        DEBUG_HAND = Registry.register(Registries.ITEM, new Identifier(MagicHand.MOD_ID,"debughand"),new DebugHand(new FabricItemSettings().maxCount(1)));
        DEBUG_SPRAY_HAND = Registry.register(Registries.ITEM, new Identifier(MagicHand.MOD_ID,"debug-spray-hand"),new SprayDebugHand(new FabricItemSettings().maxCount(1)));
        ALL_ITEMS.add(DEBUG_HAND);
        ALL_ITEMS.add(DEBUG_SPRAY_HAND);

        Registry.register(Registries.ITEM, new Identifier(MagicHand.MOD_ID, "runtimedata-tester"), new RuntimeDataTester(new FabricItemSettings()));



    }

}
