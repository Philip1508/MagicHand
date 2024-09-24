package magichand.modid.items;

import magichand.modid.playerextension.MagickaMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RuntimeDataTester extends Item {
    public RuntimeDataTester(Settings settings) {
        super(settings);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && hand == Hand.OFF_HAND)
        {
            MagickaMachine.registerPlayer(user);
        }


        if (!world.isClient() && hand == Hand.MAIN_HAND)
        {
            MagickaMachine.getPlayerRuntimeData(user).getManaManager().decreaseMana(10);
        }

        return TypedActionResult.pass(user.getStackInHand(hand));


    }
}
