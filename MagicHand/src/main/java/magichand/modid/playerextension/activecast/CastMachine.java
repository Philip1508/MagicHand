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

public class CastMachine {

    private PlayerEntity player;

    private boolean mainHandFiring = false;
    private int mainHandPreviousNodeUUID = 0;


    private boolean offHandFiring = false;
    private int offHandPreviousNodeUUID = 0;



    private int regenerationCooldown = (20) * 3;



    public CastMachine(PlayerEntity player)
    {
        this.player = player;
    }



    public void tick()
    {

        boolean active = mainHandFiring || offHandFiring;

        // If the cast is not active anymore, then we must set the player on his way to start regenerating mana again!
        if (!active)
        {

            if (regenerationCooldown < 0)
            {
                MagickaMachine.getPlayerRuntimeData(player).setState(MagickaMachineState.MANA_PASSIVE_REGENERATION);
                regenerationCooldown = (20)*3;


            }
            regenerationCooldown = regenerationCooldown - 1;
        }


        boolean timeForUpdate = player.getWorld().getTime() % 3 == 0;

        if (!timeForUpdate)
        {
            return;
        }

        // If the player puts away the chime, casting must be aborted immidiatly.
        if (!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof AbstractSpellCatalyst))
        {
            disableHand(Hand.MAIN_HAND);
        }
        if (!(player.getStackInHand(Hand.OFF_HAND).getItem() instanceof AbstractSpellCatalyst))
        {
            disableHand(Hand.OFF_HAND);
        }

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



    public boolean initiateCast(Hand hand)
    {
        boolean oldState;
        switch (hand)
        {
            case MAIN_HAND -> {
                System.out.println("MainHandFiring before Changes: " + mainHandFiring);

                if (!mainHandFiring)
                {
                    mainHandFiring = true;
                    MagickaMachine.getPlayerRuntimeData(player).setState(MagickaMachineState.MANA_ACTIVE_CAST);
                    return mainHandFiring;
                }

                return false;
            }

            case OFF_HAND ->  {
                oldState = offHandFiring;
                offHandFiring = true;

                return !oldState;
            }
            default -> {return false;}
        }

    }


    public void shoot(World world, PlayerEntity user, Hand hand)
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
        projectile.setVelocity(user, user.getPitch()-3f,relativYaw ,0.0f, 0.8f, 0.3f);

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


    public void disableHand(Hand hand)
    {
        switch (hand)
        {
            case MAIN_HAND -> {mainHandFiring = false;}
            case OFF_HAND -> {offHandFiring = false;}
        }

    }


}
