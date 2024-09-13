package magichand.modid.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class Projectile extends ProjectileEntity {



    private int age;
    private int maxAge = 50;



    public Projectile(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
        // This needs to be destroyed immidiatly as it must be a "reload" and not an "active" instance.
        age = maxAge+1;
    }



    private Projectile(
            World world,
            LivingEntity owner,
            Vec3d pos
    )
    {
        this(EntityRegistrator.PROJECTILE, world);
        this.setOwner(owner);
        this.setPosition(pos);
        this.age = 0;
    }

    public static Projectile create(
            World world,
            LivingEntity owner,
            Vec3d pos
    )
    {
        return new Projectile(world,owner,pos);
    }



    @Override
    public void tick()
    {

        if (++age > maxAge)
        {
            serverDiscard();
        }


        EntityHitResult entityHitResult = this.getEntityCollision(this.getPos(), this.getPos().add(this.getVelocity()));

        if (entityHitResult != null)
        {
            // ToDo; Code for Effect Application goes here.
            entityHitResult.getEntity().damage(getDamageSources().indirectMagic(this,getOwner()),0);
        }

        Entity owner = getOwner();
        if (owner != null && owner.distanceTo(this) > 10)
        {
            this.setVelocity(this.getVelocity().multiply(0.75));
        }





        this.move(MovementType.SELF, this.getVelocity());


    }




    @Override
    protected void initDataTracker() {

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
        return ProjectileUtil.getEntityCollision(
                this.getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit
        );
    }

}
