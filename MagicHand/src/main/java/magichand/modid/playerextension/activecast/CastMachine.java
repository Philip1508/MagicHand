package magichand.modid.playerextension.activecast;

import magichand.modid.MagicHand;
import magichand.modid.entity.SprayMagicProjectile;
import magichand.modid.items.spellcatalysts.AbstractSpellCatalyst;
import magichand.modid.networking.PacketRegistrator;
import magichand.modid.networking.S2CUpdater;
import magichand.modid.playerextension.DataPipe;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.MagickaMachineState;
import magichand.modid.playerextension.PlayerRuntimeData;
import magichand.modid.util.CastMachineActivityTuple;
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


    private boolean active = false;

    private final DataPipe sharedData;


    private boolean mainHandFiring = false;
    private SpellChargeMachine mainHandCharger;



    private boolean offHandFiring = false;
    private SpellChargeMachine offHandCharger;



    private static final int REGENERATION_COOLDOWN_DEFAULT = (20) * 3;

    private int regenerationCooldown = REGENERATION_COOLDOWN_DEFAULT;



    public CastMachine(DataPipe sharedData)
    {
        this.sharedData = sharedData;
    }


    /**
     * This Method is the tick for the CastMachine.
     */
    public void tick(ServerPlayerEntity player)
    {
        // If the player puts away the chime casting must be aborted immidiatly.

        if (!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof AbstractSpellCatalyst)
                || sharedData.getManaBurnoutState()
                && mainHandFiring)
        { disableHand(Hand.MAIN_HAND); }

        if (!(player.getStackInHand(Hand.OFF_HAND).getItem() instanceof AbstractSpellCatalyst)
                || sharedData.getManaBurnoutState()
                && offHandFiring)
        { disableHand(Hand.OFF_HAND); }


        this.active = mainHandFiring || offHandFiring;
        // If the cast is not active anymore, then we must set the player on his way to start regenerating mana again!
        if (!active)
        {
            if ((regenerationCooldown -=1) < 0)
            {
                MagickaMachine.getPlayerRuntimeData(player).setState(player, MagickaMachineState.MANA_PASSIVE_REGENERATION);
                regenerationCooldown = REGENERATION_COOLDOWN_DEFAULT;


                return;
            }
        }



        if (mainHandFiring)
        {
            SpellChargerState mainHandChargerState = mainHandCharger.tick();
            activateManaBurnout(mainHandChargerState);

            if (mainHandChargerState == SpellChargerState.FULLY_CHARGED)
            {
                disableHand(Hand.MAIN_HAND);
            }

        }

        if (offHandFiring)
        {
            SpellChargerState offHandChargerState = offHandCharger.tick();
            activateManaBurnout(offHandChargerState);

            if (offHandChargerState == SpellChargerState.FULLY_CHARGED)
            {
                disableHand(Hand.OFF_HAND);
            }

        }



        if (mainHandFiring || offHandFiring)
        {
            ServerPlayNetworking.send(player, PacketRegistrator.S2C_KEEPALIVE, PacketByteBufs.create());
        }

    }


    /**
     * This method initiates a Spellcast for a given hand.
     * @param hand
     * @return Boolean, wether cast has been initiated (true) or not (false, iff already casting)
     */
    public boolean initiateCast(ServerPlayerEntity player, Hand hand)
    {
        if (sharedData.getManaBurnoutState()) {return false;}


        switch (hand)
        {
            case MAIN_HAND -> {
                if (!mainHandFiring)
                {
                    mainHandFiring = true;
                    mainHandCharger = new SpellChargeMachine(player, Hand.MAIN_HAND);
                    MagickaMachine.getPlayerRuntimeData(player).setState(player, MagickaMachineState.MANA_ACTIVE_CAST);
                    return true;
                }
                return false;
            }

            case OFF_HAND ->  {
                if (!offHandFiring)
                {
                    offHandFiring = true;
                    offHandCharger = new SpellChargeMachine(player, Hand.OFF_HAND);
                    MagickaMachine.getPlayerRuntimeData(player).setState(player, MagickaMachineState.MANA_ACTIVE_CAST);
                    return true;
                }
                return false;

            }
            default -> {return false;}
        }

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
            case MAIN_HAND -> {
                mainHandFiring = false;
                popSpellCharger(hand);
            }
            case OFF_HAND -> {
                popSpellCharger(hand);
                offHandFiring = false;
            }
        }

    }


    /**
     * This method returns an instance of a class which contains information as booleans, which hand is active and which
     * is not.
     * @return - CastMachineActivityTuple, containing public final attributes about hand activity state.
     */
    public CastMachineActivityTuple isActive()
    {
        return new CastMachineActivityTuple(mainHandFiring, offHandFiring);
    }


    public void popSpellCharger(Hand hand)
    {
        switch (hand)
        {
            case MAIN_HAND ->
            {
                if (mainHandCharger != null)
                {
                    mainHandCharger.pop();
                    mainHandCharger = null;
                }
            }

            case OFF_HAND ->
            {
                if (offHandCharger != null)
                {
                    offHandCharger.pop();
                    offHandCharger = null;
                }
            }
        }


    }


    private void activateManaBurnout(SpellChargerState state)
    {
        if (state == SpellChargerState.OUT_OF_MANA)
        {
            this.sharedData.setManaBurnoutState(true);
        }
    }


}
