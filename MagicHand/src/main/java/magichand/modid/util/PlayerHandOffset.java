package magichand.modid.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlayerHandOffset {

    /**
     * See getHandPosOffset
     * @param pitch
     * @param yaw
     * @return
     */
    private static Vec3d getRotationVector(float pitch, float yaw) {
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
    private static Vec3d getHandPosOffset(PlayerEntity player, Hand hand) {

        int offSet = 80;

        if (hand == Hand.OFF_HAND)
        {
            offSet = -80;
        }

        return getRotationVector(0.0F, player.getYaw() + offSet).multiply(0.5);

    }


    public static Vec3d getAppliedPlayerHandOffset(PlayerEntity user, Hand hand)
    {

        // Here we calculate the handCoordinateOffset and apply it to the users coordinates.
        Vec3d handCoordinates = getHandPosOffset(user, hand);
        Vec3d appliedHandOffset = new Vec3d(user.getX() + handCoordinates.getX(), user.getY() + handCoordinates.getY()+1, user.getZ() + handCoordinates.getZ());
        return appliedHandOffset;
    }

}
