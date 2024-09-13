package magichand.modid.entity;

import magichand.modid.MagicHand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import java.util.Random;

public class SprayMagicProjectile extends ProjectileEntity  {



    private final int maxAge = 50;
    private float radius = 0.5f;




    private NodeParticleEmitter particleEmitter;

    private Entity previousShot;
    private static final TrackedData<Integer> PREVIOUSNODE_ID = DataTracker.registerData(SprayMagicProjectile.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> AGE = DataTracker.registerData(SprayMagicProjectile.class, TrackedDataHandlerRegistry.INTEGER);

    private int ticksToBreak = 0;

    boolean debug = false;

    public Vec3d pointOfOrigin;

    boolean isDefaultConstructor = false;




    public SprayMagicProjectile(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
        this.isDefaultConstructor = true;
        this.pointOfOrigin = Vec3d.ZERO;

        // This needs to be destroyed immidiatly as it must be a "reload" and not an "active" instance.

    }



    private SprayMagicProjectile(
            World world,
            LivingEntity owner,
            Vec3d pos
    )
    {
        this(EntityRegistrator.SPRAY_PROJECTILE, world);
        this.setOwner(owner);
        this.setPosition(pos);

        dataTracker.set(AGE, 0);

        this.pointOfOrigin = pos;


        this.noClip = true;
        this.setInvisible(true);

    }

    public static SprayMagicProjectile create(
            World world,
            LivingEntity owner,
            Vec3d pos
    )
    {

        SprayMagicProjectile projectile = new SprayMagicProjectile(world, owner, pos);

        return projectile;
    }



    @Override
    public void tick()
    {
        if (getOwner() == null)
        {
            discard();
        }


        if (isDefaultConstructor)
        {
            this.pointOfOrigin = getPos();

            this.ticksToBreak = amountOfTicksToFinalPoint(6);

            isDefaultConstructor = false;
        }

        dataTracker.set(AGE, dataTracker.get(AGE)+1);
        if (dataTracker.get(AGE) > maxAge)
        {
            serverDiscard();
        }



        if (this.getVelocity().lengthSquared() > 0.10)
        {
            radius = radius + 0.25f;
        }

        if (this.getVelocity().lengthSquared() < 0.10)
        {
            radius = radius - 0.075f*2.5f;

            if (radius < 0.3 && age > 30)
            {
                serverDiscard();
            }
        }


        this.checkBlockCollision();

        EntityHitResult entityHitResult = this.getEntityCollision(this.getPos(), this.getPos().add(this.getVelocity()));

        if (entityHitResult != null)
        {
            // ToDo; Code for Effect Application goes here.
            //entityHitResult.getEntity().damage(getDamageSources().indirectMagic(this,getOwner()),2.5f);
            if (entityHitResult.getEntity() instanceof  LivingEntity living)
            {
                living.heal(1.5f + living.getMaxHealth() * 0.025f);
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 7*20,1), getOwner());

                 float percentage = living.getHealth() / living.getMaxHealth();

                try {
                    getOwner().playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, percentage);
                }
                catch (Exception e)
                {
                    MagicHand.LOGGER.info("Caught NullPointerException for SoundEffect of Heal.");
                }

            }
        }


        Entity owner = getOwner();
        //if (owner != null && owner.distanceTo(this) > 8)

        if (dataTracker.get(AGE) - ticksToBreak > ticksToBreak >> 2)
        {
            if (debug)
            {
                Vec3d finalPosition = Vec3d.ZERO.add(pointOfOrigin);




                for (int i = 0; i < (dataTracker.get(AGE)); i++ )
                {
                    finalPosition = finalPosition.add(getVelocity());
                }
                MagicHand.LOGGER.info("Message Start");
                MagicHand.LOGGER.info("Velocity?: " + getVelocity().toString());
                MagicHand.LOGGER.info("Is Client?: "+ String.valueOf(getWorld().isClient()));
                MagicHand.LOGGER.info("Origin Position: " + pointOfOrigin.toString());
                MagicHand.LOGGER.info("Actual Position: " + getPos().toString());
                MagicHand.LOGGER.info("Calculated FPosition: " + finalPosition);
                MagicHand.LOGGER.info("Distance: " + finalPosition.distanceTo(getPos()));
                MagicHand.LOGGER.info("Age: " + dataTracker.get(AGE));
                MagicHand.LOGGER.info("Calculated Ticks: " + amountOfTicksToFinalPoint(8));
                MagicHand.LOGGER.info("Message End");

                debug = !debug;

            }

            this.setVelocity(this.getVelocity().multiply(0.75));
        }





        if(particleEmitter == null)
        {
            previousShot = getWorld().getEntityById(dataTracker.get(PREVIOUSNODE_ID));
            particleEmitter = new NodeParticleEmitter(this, previousShot);
        }

        if (age % 3 == 0)
        {
            particleEmitter.emitParticles();
        }




        this.move(MovementType.SELF, this.getVelocity());


    }




    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(PREVIOUSNODE_ID,0);
        dataTracker.startTracking(AGE,0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }








    public void serverDiscard()
    {
        if (!this.getWorld().isClient())
        {
            discard();
        }

    }



    // ToDo; This will need to be Streched corresponding to the Spread
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {

        Box boundingBoxRadius = new Box(getPos().subtract(radius,radius,radius),getPos().add(radius,radius,radius));


        return ProjectileUtil.getEntityCollision(
                this.getWorld(), this, currentPosition, nextPosition, boundingBoxRadius, this::canHit
        );
    }








    public float getRadius() {
        return radius;
    }


    public void setParticleEmitter(NodeParticleEmitter particleEmitter) {
        this.particleEmitter = particleEmitter;
    }


    public void setPreviousShot(int entity_id) {
        dataTracker.set(PREVIOUSNODE_ID, entity_id);
    }

    public Entity getPreviousShot() {
        return previousShot;
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if (state.getBlock() instanceof PlantBlock)
        {
            return;
        }
        if (state.getBlock() != Blocks.AIR)
        {
            setVelocity(getVelocity().multiply(0.3));
        }
    }




    private int amountOfTicksToFinalPoint(double desiredDistance)
    {
        Vec3d point = Vec3d.ZERO.add(pointOfOrigin);

        int ticks = 0;

        while (++ticks < maxAge)
        {
            if (point.distanceTo(pointOfOrigin) > desiredDistance)
            {
                break;
            }
            point = point.add(getVelocity());

        }

        return ticks;

    }


    public double getFlownDistance()
    {

        int age = dataTracker.get(AGE);

        if (age >= ticksToBreak)
        {
            return 1;
        }

        return (double) dataTracker.get(AGE) / (double) ticksToBreak   ;

    }


}
