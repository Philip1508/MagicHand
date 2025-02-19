package magichand.modid.items.spellcatalysts;

import magichand.modid.MagicHand;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.PlayerRuntimeData;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.List;

public abstract class AbstractSpellCatalyst extends Item implements RenderAbstractionInterface {


    public AbstractSpellCatalyst(Settings settings) {
        super(settings);
    }



    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();

        // Wenn bereits gecasted wird, dann muss gar nichts getan werden:
        /*
        if (casting)

        {
            return  TypedActionResult.pass(player.getStackInHand(Hand.MAIN_HAND));
        }
        */

        // Wenn man dual wielded, dann wird Use auf beide Hände ausgeführt.



        if (mainHandStack.getItem() instanceof AbstractSpellCatalyst && offHandStack.getItem() instanceof  AbstractSpellCatalyst)
        {
            if (hand == Hand.MAIN_HAND)
            {
                return  TypedActionResult.pass(player.getStackInHand(Hand.MAIN_HAND));
            }




            // Cast Sequenz in MagickaMachine anstoßen?

            return  TypedActionResult.success(player.getStackInHand(Hand.OFF_HAND));

        }


        if (player instanceof ServerPlayerEntity sPlayer)
        {
            MagickaMachine.getPlayerRuntimeData(sPlayer).getCastMachine().initiateCast(hand);
        }

        // Cast Sequenz in MagickaMachine anstoßen?
        return  TypedActionResult.success(player.getStackInHand(hand));


    }




    public abstract float getScaling();


    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }



    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(Text.of("???"));

    }





}
