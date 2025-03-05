package magichand.modid.playerextension.activecast;

import magichand.modid.MagicHand;
import magichand.modid.entity.Projectile;
import magichand.modid.entity.SprayMagicProjectile;
import magichand.modid.networking.S2CUpdater;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.manaregeneration.ManaManager;
import magichand.modid.util.PlayerHandOffset;
import magichand.modid.util.Rational;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpellChargeMachine {

    private final ServerPlayerEntity player;
    private final ManaManager manaManager;
    private final Hand hand;


    private final int spellCostPerSecond;
    private final Rational spellCostRationalTick;



    private Rational fractionalMana = new Rational(0);

    private final Rational chargeLevel = new Rational(0);

    private int previousNodeUUID = 0;
    private final SpellType spellType = SpellType.SPRAY;



    public SpellChargeMachine(ServerPlayerEntity player, Hand hand)
    {


        this.player = player;
        this.hand = hand;
        this.manaManager = MagickaMachine.getPlayerRuntimeData(player).getManaManager();

        // ToDo; Will be replaced by ItemStack Information?
        this.spellCostPerSecond = 10;
        this.spellCostRationalTick = new Rational(spellCostPerSecond,20);

        // This sets ticks until fully charged.
        this.chargeLevel.setDenominator(6);


    }


    /**
     * Tick of SpellCharger.
     * @return Boolean:
     */
    public SpellChargerState tick()
    {

        if (fullyCharged() && spellType != SpellType.SPRAY)
        {
            return SpellChargerState.WAITING_FOR_RELEASE;
        }


        // Subtract Mana if player isn't creative.
        if (!player.isCreative())
        {
            fractionalMana = fractionalMana.add(spellCostRationalTick);

            boolean outOfMana = false;

            if (fractionalMana.greaterOne())
            {
                boolean manaSubtracted = manaManager.decreaseMana(fractionalMana.getWholeAndFlatten());

                if (manaSubtracted)
                {
                    S2CUpdater.serverToClientUpdateMana(player, manaManager);
                }

                outOfMana |= !manaSubtracted;
            }


            outOfMana |= manaManager.getMana() == 0;

            if (outOfMana)
            {
                return SpellChargerState.OUT_OF_MANA;
            }
        }


        switch (spellType)
        {
            case SPRAY ->
            {
                incrementChargelevel();
                if (mediumCharged())
                {
                    shoot(player.getWorld(), player);
                }
            }
            default -> {
                incrementChargelevel();
                if (fullyCharged())
                {
                    return SpellChargerState.WAITING_FOR_RELEASE;
                }

            }
        }


        return SpellChargerState.CONTINUE;




    }








    /**
     * This Method deploys a projectile.
     * @param world - World of projectile.
     * @param user - Caster
     */
    private void shoot(World world, PlayerEntity user)
    {

        Vec3d appliedHandOffset = PlayerHandOffset.getAppliedPlayerHandOffset(user, hand);

        // ToDo; Abstract for Dynamic Distance!
        float relativYaw = user.getYaw()-2.7f;
        if (hand == Hand.OFF_HAND)
        {
            relativYaw = user.getYaw()+2.7f;
        }


        switch (spellType)
        {
            case SELF -> {}
            case PROJECTILE -> {
                Projectile projectile = Projectile.create(player.getWorld(), player, appliedHandOffset);
                projectile.setVelocity(user, user.getPitch()+0.5f, relativYaw, 0.0f, 0.8f, 0.3f);

                user.getWorld().spawnEntity(projectile);
            }
            case SPRAY -> {
                // Instantiation of the actual Node
                SprayMagicProjectile projectile = SprayMagicProjectile.create(world, user, appliedHandOffset);
                projectile.setVelocity(user, user.getPitch()+0.5f,relativYaw ,0.0f, 0.8f, 0.3f);

                projectile.setVelocity(projectile.getVelocity().getX(),
                        projectile.getVelocity().getY()-user.getVelocity().getY(),
                        projectile.getVelocity().getZ());


                projectile.setPreviousShot(previousNodeUUID);
                previousNodeUUID = projectile.getId();

                // Spawning of the Entity.
                user.getWorld().spawnEntity(projectile);

            }
        }



    }


    public boolean minimumCharged()
    {
        return chargeLevel.getNumerator() >= 4;
    }

    public boolean mediumCharged()
    {
        return chargeLevel.getNumerator() >= chargeLevel.getDenominator();
    }

    public boolean fullyCharged()
    {
        if (spellType == SpellType.SPRAY) {return false;}
        return chargeLevel.getNumerator() >= chargeLevel.getDenominator() * 2;
    }


    public void pop() {

        S2CUpdater.serverToClientUpdateCharger(player, new Rational(0,1), hand);

        if (spellType == SpellType.PROJECTILE)
        {
            shoot(player.getWorld(), player);
        }


    }


    private void incrementChargelevel()
    {
        if (chargeLevel.getNumerator() < chargeLevel.getDenominator()*2)
        {
            chargeLevel.setNumerator(chargeLevel.getNumerator()+1);
            S2CUpdater.serverToClientUpdateCharger(player, chargeLevel, hand);
        }
    }





}
