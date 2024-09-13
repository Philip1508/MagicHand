package magichand.modid.entity;


import magichand.modid.MagicHand;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;


//ToDo; Rewrite completely.
public class NodeParticleEmitter {

    public static final Random numberGenerator = new Random();
    public static final double phi = Math.PI * (Math.sqrt(5) - 1);

    private static final ParticleEffect PARTICLE_TYPE = ParticleTypes.SMALL_FLAME ;



    SprayMagicProjectile node;



    Entity previousNode;


    public NodeParticleEmitter(SprayMagicProjectile node, Entity previousNode)
    {
        this.node = node;
        this.previousNode = previousNode;
    }



    public void emitParticles()
    {
        // These Routines must only be called within the Ingame-Client, as these are graphical Effects!
        if (!node.getWorld().isClient())
        {
            return;
        }

        emitSelfParticles();
        emitPreviousNodeParticles();


    }

    private void emitPreviousNodeParticles() {

        if (previousNode == null)
        {
            previousNode = node.getPreviousShot();
            return;
        }


        if (previousNode instanceof SprayMagicProjectile previousProjectile)
        {



            Vec3d pos = node.getPos();
            Vec3d previousShotPos = previousProjectile.getPos();

            if (pos.distanceTo(previousShotPos) > 5)
            {
                return;
            }

            float radiusFactor = previousProjectile.getRadius() - node.getRadius();

            Vec3d directionalVector = previousShotPos.subtract(pos);
            Vec3d directionalSpeed = directionalVector.multiply(0.05).multiply(1-node.getFlownDistance());

            if (node.getRadius() * 2 > previousProjectile.getRadius())
            {
                directionalSpeed.multiply(1 - node.getFlownDistance());
            }

            if (node.getVelocity().length() < 0.10)
            {
                directionalSpeed = directionalSpeed.multiply(0);
            }


            for (double j = 0.25; j < 0.9; j = j+0.25)
            {

                float radius = (float) (node.getRadius() + j*radiusFactor);

                Vec3d virtualPos = node.getPos().add(directionalVector.multiply(j));



                for (int i = 0; i < 3; ++i)
                {
                    double y = 1 - ( i / ((double) 1000-1)) * 2;


                    try {
                        double randomRadiusPoint = numberGenerator.nextDouble(radius*0.8,radius*1.2);
                        double intRadius = (Math.sqrt(1 - y * y)) * randomRadiusPoint;

                        double theta = phi * i;

                        y = y * randomRadiusPoint - (radius - 0.2);
                        double x = Math.cos(theta) * intRadius;
                        double z = Math.sin(theta) * intRadius;
                        node.getWorld().addParticle(PARTICLE_TYPE,

                                virtualPos.getX() + x, (virtualPos.getY()+y), virtualPos.getZ() + z,
                                directionalSpeed.getX(),directionalSpeed.getY(),directionalSpeed.getZ());

                    }
                    catch (Exception e)
                    {
                        // Since these are only graphical effects it's okay to just forget about it when it "fails"
                        break;
                    }

                }



            }

        }



        if (previousNode instanceof PlayerEntity player)
        {



            Vec3d pos = node.getPos();

            Vec3d handOffset = getHandPosOffset(player, Hand.OFF_HAND);
            Vec3d appliedHandoffste = player.getPos().add(handOffset);


            Vec3d previousShotPos = appliedHandoffste;

            if (pos.distanceTo(previousShotPos) > 2)
            {
                return;
            }

            float radiusFactor = 0.3f - node.getRadius();

            Vec3d directionalVector = previousShotPos.subtract(pos);



            for (double j = 0.25; j < 0.9; j = j+0.25)
            {

                float radius = (float) (node.getRadius() + j*radiusFactor);

                Vec3d virtualPos = node.getPos().add(directionalVector.multiply(j));



                for (int i = 0; i < 5; ++i)
                {
                    double y = 1 - ( i / ((double) 1000-1)) * 2;


                    try {
                        double randomRadiusPoint = numberGenerator.nextDouble(radius*0.8,radius*1.2);
                        double intRadius = (Math.sqrt(1 - y * y)) * randomRadiusPoint;

                        double theta = phi * i;

                        y = y * randomRadiusPoint - (radius - 0.2);
                        double x = Math.cos(theta) * intRadius;
                        double z = Math.sin(theta) * intRadius;
                        node.getWorld().addParticle(PARTICLE_TYPE,

                                virtualPos.getX() + x, (virtualPos.getY()+y), virtualPos.getZ() + z,
                                0,0,0);

                    }
                    catch (Exception e)
                    {
                        // Since these are only graphical effects it's okay to just forget about it when it "fails"
                        break;
                    }

                }



            }

        }




    }


    private void emitSelfParticles()
    {

        float radius = node.getRadius();

        Vec3d nodeVelocite = node.getVelocity();

        for (int i = 0; i < 8; ++i)
        {
            double y = 1 - ( i / ((double) 1000-1)) * 2;


            try {
                double randomRadiusPoint = numberGenerator.nextDouble(radius*0.8,radius*1.2);
                double intRadius = (Math.sqrt(1 - y * y)) * randomRadiusPoint;

                double theta = phi * i;

                double flightScaleFactor = 1 - node.getFlownDistance();

                //MagicHand.LOGGER.info("FLIGHT SCALE FACTOR:= " + flightScaleFactor);

                Vec3d particleVelocity = nodeVelocite.multiply(flightScaleFactor);

                y = y * randomRadiusPoint - (radius - 0.2);
                double x = Math.cos(theta) * intRadius;
                double z = Math.sin(theta) * intRadius;
                node.getWorld().addParticle(PARTICLE_TYPE,
                        node.getX() + x, (node.getY()+y), node.getZ() + z,
                        particleVelocity.getX(),particleVelocity.getY(),particleVelocity.getZ());

            }
            catch (Exception e)
            {
                // Since these are only graphical effects it's okay to just forget about it when it "fails"
                break;
            }

        }

    }



    // ToDo; AUSLAGERN!
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
