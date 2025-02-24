package magichand.modid.items.spellcatalysts;

import magichand.modid.MagicHand;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.MagickaMachineState;
import magichand.modid.playerextension.PlayerRuntimeData;
import magichand.modid.playerextension.activecast.CastMachine;
import magichand.modid.util.CastMachineActivityTuple;
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
import net.minecraft.util.UseAction;
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

        if (player instanceof ServerPlayerEntity sPlayer && MagickaMachine.isPlayerMagicuser(sPlayer))
        {


            CastMachine castMachine = MagickaMachine.getPlayerRuntimeData(sPlayer).getCastMachine();

            CastMachineActivityTuple handStates = castMachine.isActive();


            // If Dual Wied
            if (mainHandStack.getItem() instanceof AbstractSpellCatalyst && offHandStack.getItem() instanceof  AbstractSpellCatalyst)
            {
                if (hand == Hand.MAIN_HAND)
                {
                    if (!handStates.offHandActive)
                    {
                        castMachine.initiateCast(Hand.OFF_HAND);
                    }

                    return  TypedActionResult.pass(sPlayer.getStackInHand(Hand.MAIN_HAND));
                }

            }

            if (hand == Hand.MAIN_HAND && offHandStack.getUseAction() != UseAction.NONE)
            {
                return  TypedActionResult.pass(sPlayer.getStackInHand(hand));
            }

            castMachine.initiateCast(hand);




        }



        // Do Nothing
        return  TypedActionResult.pass(player.getStackInHand(hand));


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
