package magichand.modid.items;

import magichand.modid.MagicHand;
import magichand.modid.entity.SprayMagicProjectile;
import magichand.modid.items.spellcatalysts.RenderAbstractionInterface;
import magichand.modid.networking.PacketRegistrator;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.PlayerRuntimeData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;


public class SprayDebugHand extends Item implements RenderAbstractionInterface {


    public static final String ACTIVE_CAST = "ACTIVE_CAST";

    public static final String CURRENTLY_CASTING = "CURRENTLY_CASTING";
    public static final String CASTED_HAND = "CASTED_HAND";
    public static final String REMAINING = "TICKS_LEFT";
    public static final String UUID_NODE = "UUID_PREVIOUS";



    public SprayDebugHand(Settings settings) {
        super(settings);
    }



    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        // There is some serious insanity going on here
        // May just put the entire firing onto the network stack, just put 2 boolean into the packet.
        ItemStack mainHand = user.getMainHandStack();
        ItemStack offHand = user.getOffHandStack();

        ItemStack stackInhand = user.getStackInHand(hand);

        NbtCompound nbtCompound = stackInhand.getOrCreateSubNbt(ACTIVE_CAST);


        Hand bootlegHand = hand;

        if (mainHand.isOf(ItemRegistrator.DEBUG_SPRAY_HAND) && offHand.isOf(ItemRegistrator.DEBUG_SPRAY_HAND))
        {

            NbtCompound nbt = offHand.getOrCreateSubNbt(ACTIVE_CAST);
            //shoot(world, user, hand, user.getStackInHand(hand));

            // ToDo; Seperate for when already firing vs. start of firing sequence...


            nbt.putBoolean(CURRENTLY_CASTING, true);
            nbt.putInt(CASTED_HAND, 1);
            nbt.putInt(REMAINING,  3);
            bootlegHand = Hand.OFF_HAND;
            if (!nbtCompound.getBoolean(CURRENTLY_CASTING))
            {
                shoot(world, user, hand, user.getStackInHand(hand));
            }


        }
        else
        {


            if (hand == Hand.MAIN_HAND && offHand.getUseAction() != UseAction.NONE)
            {
                return  TypedActionResult.pass(user.getStackInHand(hand));
            }

            NbtCompound nbt = user.getStackInHand(hand).getOrCreateSubNbt(ACTIVE_CAST);
            int castedHand = bootlegHand == Hand.MAIN_HAND ? 0:1;


            nbt.putBoolean(CURRENTLY_CASTING, true);
            nbt.putInt(CASTED_HAND, castedHand);
            nbt.putInt(REMAINING,  3);

            if (!nbtCompound.getBoolean(CURRENTLY_CASTING))
            {
                shoot(world, user, hand, user.getStackInHand(hand));
            }



        }








            return  TypedActionResult.pass(user.getStackInHand(hand));





    }







    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {





        if (entity instanceof LivingEntity living)
        {
            if ( !(living.getMainHandStack().equals(stack) || living.getOffHandStack().equals(stack)) )
            {
                return;
            }
        }

        NbtCompound nbt = stack.getOrCreateSubNbt(ACTIVE_CAST);

        boolean activeCast = nbt.getBoolean(CURRENTLY_CASTING);
        int activeHand = nbt.getInt(CASTED_HAND);
        int ticksleft = nbt.getInt(REMAINING);





        if (activeHand == -1 && false)
        {
            MagicHand.LOGGER.info("Infogram Start");
            MagicHand.LOGGER.info(String.valueOf(activeCast));
            MagicHand.LOGGER.info(String.valueOf(activeHand));
            MagicHand.LOGGER.info(String.valueOf(ticksleft));
            MagicHand.LOGGER.info("Infogram End");
        }





        if (ticksleft == 0 || !activeCast)
        {


            nbt.putBoolean(CURRENTLY_CASTING, false);
            nbt.putInt(CASTED_HAND, -1);
            return;
        }

        Hand hand = Hand.MAIN_HAND;



        if (activeHand == 1)
        {
            hand = Hand.OFF_HAND;
        }







        if (activeCast && entity instanceof PlayerEntity player && world.getTime() % 3 == 0)
        {

            if ((!player.getStackInHand(hand).isOf(ItemRegistrator.DEBUG_SPRAY_HAND)))
            {
                nbt.putBoolean(CURRENTLY_CASTING, false);
                return;
            }
            shoot(world, player, hand, stack);
            nbt.putInt(REMAINING, ticksleft-1);

            if (ticksleft <= 2 && entity instanceof ServerPlayerEntity user && activeHand == 2)
            {
                PacketByteBuf packet = PacketByteBufs.create();
                packet.writeBoolean(false);
                MagicHand.LOGGER.info("Sending out KeepalivePing");
                ServerPlayNetworking.send(user,PacketRegistrator.STILL_ACTIVE, packet);
            }


        }



    }


    /**
     * This Function shoots a spray node. Mind that it imprints the previous node.
     * This also holds the Mana Check right now.
     * @param world
     * @param user
     * @param hand
     * @param stack
     */
    private void shoot(World world, PlayerEntity user, Hand hand, ItemStack stack)
    {

        // This is the Mana Check. it returns True if Mana was subtracted.

        PlayerRuntimeData data = MagickaMachine.getPlayerRuntimeData(user);

        if (data == null)
        {
            return;
        }

        boolean x = data.getManaManager().decreaseMana(3);

        if (!x)
        {
            return;
        }
        // If a shot was made, Mode is switched to regeneration cooldown.
        MagickaMachine.activatePlayerManaRegenerationCooldown(user);


        // Here we calculate the handCoordinateOffset and apply it to the users coordinates.
        Vec3d handCoordinates = getHandPosOffset(user, hand);
        Vec3d appliedHandOffset = new Vec3d(user.getX() + handCoordinates.getX(), user.getY() + handCoordinates.getY()+1, user.getZ() + handCoordinates.getZ());


        // This code describes the Pitch and Yad Adjusments, so that the Node flies from the Hand do the center of the
        // Screen
        // This may be fixed by using the appliedHandOffset to RayCast? A Sloppy implementation would be better
        // than magic numbers.
        float relativYaw = user.getYaw()-2.7f;

        if (hand == Hand.OFF_HAND)
        {
            relativYaw = user.getYaw()+2.7f;
        }


        // The NbtManipulations which fech the Cast Instance and the previous Node.
        NbtCompound nbt = stack.getOrCreateSubNbt(ACTIVE_CAST);
        int previousShot = nbt.getInt(UUID_NODE);

        // Instantiation of the actual Node
        SprayMagicProjectile projectile = SprayMagicProjectile.create(world, user, appliedHandOffset);
        projectile.setVelocity(user, user.getPitch()-3f,relativYaw ,0.0f, 0.8f, 0.3f);
        projectile.setPreviousShot(previousShot);


        // This is "now" the "previous Node" for the potential next Node
        nbt.putInt(UUID_NODE, projectile.getId());






        // Spawning of the Entity.
        user.getWorld().spawnEntity(projectile);

    }






    /**
     * See getHandPosOffset
     * @param pitch
     * @param yaw
     * @return
     */
    private Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * (float) (Math.PI / 180.0);
        float g = -yaw * (float) (Math.PI / 180.0);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
    }


    /***
     * This most unlikely should be within the item itself.
     * @param player
     * @param hand
     * @return
     */
    private Vec3d getHandPosOffset(PlayerEntity player, Hand hand) {

        int offSet = 80;

        if (hand == Hand.OFF_HAND)
        {
            offSet = -80;
        }

        return getRotationVector(0.0F, player.getYaw() + offSet).multiply(0.5);

    }



    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }


    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }


    /**
     * This solely holds debugging information for the NbtChaos.
     * @param stack
     * @param world
     * @param tooltip the list of tooltips to show
     * @param context
     */



}
