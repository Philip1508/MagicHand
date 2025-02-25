package magichand.modid.items;

import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.PlayerRuntimeData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RuntimeDataTester extends Item {
    public RuntimeDataTester(Settings settings) {
        super(settings);
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (entity instanceof ServerPlayerEntity user)
        {
            MagickaMachine.registerPlayer(user);
        }

        super.inventoryTick(stack, world, entity, slot, selected);


    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (user instanceof ServerPlayerEntity serverPlayer)
        {

            if (!world.isClient() && hand == Hand.OFF_HAND)
            {
                MagickaMachine.registerPlayer(serverPlayer);
            }


            if (!world.isClient() && hand == Hand.MAIN_HAND)
            {
                MagickaMachine.getPlayerRuntimeData(user).getManaManager().decreaseMana(10);
            }

        }


        return TypedActionResult.pass(user.getStackInHand(hand));


    }
}
