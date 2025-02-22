package magichand.modid.playerextension.activecast;

import magichand.modid.MagicHand;
import magichand.modid.entity.SprayMagicProjectile;
import magichand.modid.items.spellcatalysts.AbstractSpellCatalyst;
import magichand.modid.networking.PacketRegistrator;
import magichand.modid.networking.S2CUpdater;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.MagickaMachineState;
import magichand.modid.playerextension.PlayerRuntimeData;
import magichand.modid.util.PlayerHandOffset;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * This Class defines a machine which calculates the active firing of spells.
 */
public class CastMachine {

    private PlayerEntity player;

    private boolean active = false;

    private boolean mainHandFiring = false;
    private int mainHandPreviousNodeUUID = 0;


    private boolean offHandFiring = false;
    private int offHandPreviousNodeUUID = 0;



    private static final int REGENERATION_COOLDOWN_DEFAULT = (20) * 3;

    private int regenerationCooldown = REGENERATION_COOLDOWN_DEFAULT;



    public CastMachine(PlayerEntity player)
    {
        this.player = player;
    }


    /**
     * This Method is the tick for the CastMachine.
     */
    public void tick()
    {

        this.active = mainHandFiring || offHandFiring;

        // If the cast is not active anymore, then we must set the player on his way to start regenerating mana again!
        if (!active)
        {
            if ((regenerationCooldown -=1) < 0)
            {
                MagickaMachine.getPlayerRuntimeData(player).setState(MagickaMachineState.MANA_PASSIVE_REGENERATION);
                regenerationCooldown = REGENERATION_COOLDOWN_DEFAULT;
            }
        }


        boolean timeForUpdate = player.getWorld().getTime() % 3 == 0;
        if (!timeForUpdate)
        {
            return;
        }


        // If the player puts away the chime, casting must be aborted immidiatly.
        if (!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof AbstractSpellCatalyst))
        { disableHand(Hand.MAIN_HAND); }
        if (!(player.getStackInHand(Hand.OFF_HAND).getItem() instanceof AbstractSpellCatalyst))
        { disableHand(Hand.OFF_HAND); }

        // If the cast is active, we must check if the player is still holding down the mouse buttons!
        if (active && player instanceof ServerPlayerEntity sPlayer)
        {



            PacketByteBuf packet = PacketByteBufs.create();
            packet.writeBoolean(false);
            MagicHand.LOGGER.info("Sending out KeepalivePing");
            ServerPlayNetworking.send(sPlayer, PacketRegistrator.S2C_KEEPALIVE, packet);

            if (mainHandFiring)
            {
                shoot(player.getWorld(), player, Hand.MAIN_HAND);
            }


            if (offHandFiring)
            {
                shoot(player.getWorld(), player, Hand.OFF_HAND);
            }


        }








    }


    /**
     * This method initiates a Spellcast for a given hand.
     * @param hand
     * @return Boolean, wether cast has been initiated (true) or not (false, iff already casting)
     */
    public boolean initiateCast(Hand hand)
    {
        switch (hand)
        {
            case MAIN_HAND -> {
                if (!mainHandFiring)
                {
                    mainHandFiring = true;
                    MagickaMachine.getPlayerRuntimeData(player).setState(MagickaMachineState.MANA_ACTIVE_CAST);
                    return true;
                }
                return false;
            }

            case OFF_HAND ->  {
                if (!offHandFiring)
                {
                    offHandFiring = true;
                    MagickaMachine.getPlayerRuntimeData(player).setState(MagickaMachineState.MANA_ACTIVE_CAST);
                    return true;
                }
                return false;

            }
            default -> {return false;}
        }

    }


    /**
     * This Method deploys a projectile.
     * @param world - World of projectile.
     * @param user - Caster
     * @param hand - Hand where the projectile is being cast.
     */
    private void shoot(World world, PlayerEntity user, Hand hand)
    {

        PlayerRuntimeData data = MagickaMachine.getPlayerRuntimeData(user);

        boolean enoughMana = data.getManaManager().decreaseMana(3);
        S2CUpdater.serverToClientUpdateMana(player, data.getManaManager());

        if (!enoughMana)
        {
            // ToDo; Abort Mission Code / Disable Cast Sequence!
            mainHandFiring = false;
            offHandFiring = false;
            return;
        }

        Vec3d appliedHandOffset = PlayerHandOffset.getAppliedPlayerHandOffset(user, hand);

        // ToDo; Abstract for Dynamic Distance!
        float relativYaw = user.getYaw()-2.7f;
        if (hand == Hand.OFF_HAND)
        {
            relativYaw = user.getYaw()+2.7f;
        }


        // Instantiation of the actual Node
        SprayMagicProjectile projectile = SprayMagicProjectile.create(world, user, appliedHandOffset);
        projectile.setVelocity(user, user.getPitch()+0.5f,relativYaw ,0.0f, 0.8f, 0.3f);

        projectile.setVelocity(projectile.getVelocity().getX(),
                projectile.getVelocity().getY()-user.getVelocity().getY(),
                projectile.getVelocity().getZ());







        switch (hand)
        {
            case MAIN_HAND -> {
                projectile.setPreviousShot(mainHandPreviousNodeUUID);
                mainHandPreviousNodeUUID = projectile.getId();
            }

            case OFF_HAND -> {
                projectile.setPreviousShot(offHandPreviousNodeUUID);
                offHandPreviousNodeUUID = projectile.getId();
            }
        }


        // Spawning of the Entity.
        user.getWorld().spawnEntity(projectile);

    }


    /**
     * This method disables a casting hand.
     * Usually called from the networking if a player doesn't hold down the mouse buttons anymore.
     * @param hand
     */
    public void disableHand(Hand hand)
    {
        switch (hand)
        {
            case MAIN_HAND -> {mainHandFiring = false;}
            case OFF_HAND -> {offHandFiring = false;}
        }

    }


    public boolean[] isActive()
    {
        boolean[] arr = new boolean[2];
        arr[0] = mainHandFiring;
        arr[1] = offHandFiring;

        return arr;
    }


}
