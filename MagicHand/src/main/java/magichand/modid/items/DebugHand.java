package magichand.modid.items;

import magichand.modid.entity.Projectile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class DebugHand extends Item {


    public DebugHand(Settings settings) {
        super(settings);
    }



    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {

        ItemStack mainHand = user.getMainHandStack();
        ItemStack offHand = user.getOffHandStack();



        if (hand == Hand.MAIN_HAND && mainHand.isOf(ItemRegistrator.DEBUG_HAND) && offHand.isOf(ItemRegistrator.DEBUG_HAND))
        {
            offHand.use(world, user, Hand.OFF_HAND);
            return  TypedActionResult.pass(user.getStackInHand(hand));
        }

        if (hand == Hand.MAIN_HAND && mainHand.isOf(ItemRegistrator.DEBUG_HAND) && !offHand.isOf(ItemRegistrator.DEBUG_HAND))
        {
            shoot(world, user, hand);
            return  TypedActionResult.success(user.getStackInHand(hand));
        }

        if (hand == Hand.OFF_HAND)
        {
            shoot(world, user, hand);
            return  TypedActionResult.success(user.getStackInHand(hand));
        }



        return  TypedActionResult.success(user.getStackInHand(hand));



    }

    public TypedActionResult<ItemStack> useSwing(World world, PlayerEntity user, Hand hand)
    {

        ItemStack mainHand = user.getMainHandStack();
        ItemStack offHand = user.getOffHandStack();

        if (hand == Hand.MAIN_HAND)
        {
            if (mainHand.isOf(ItemRegistrator.DEBUG_HAND) && offHand.isOf(ItemRegistrator.DEBUG_HAND))
            {
                offHand.use(world, user, Hand.OFF_HAND);
            }
        }




        return TypedActionResult.success(user.getStackInHand(hand));


    }





    private void shoot(World world, PlayerEntity user, Hand hand)
    {

        if (world.isClient())
        {
            return;
        }



        Vec3d handCoordinates = getHandPosOffset(user, hand);

        Vec3d appliedHandOffset = new Vec3d(user.getX() + handCoordinates.getX(), user.getY() + handCoordinates.getY()+1, user.getZ() + handCoordinates.getZ());



        Projectile projectile = Projectile.create(world, user,appliedHandOffset);
        projectile.setVelocity(user, user.getPitch(), user.getYaw(),0.0f, 0.8f, 0.3f);

        user.getWorld().spawnEntity(projectile);

    }





    private final Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * (float) (Math.PI / 180.0);
        float g = -yaw * (float) (Math.PI / 180.0);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
    }

    private Vec3d getHandPosOffset(PlayerEntity player, Hand hand) {




        int offSet = 80;

        if (hand == Hand.OFF_HAND)
        {
            offSet = -80;
        }




        return getRotationVector(0.0F, player.getYaw() + offSet).multiply(0.5);

    }








}
