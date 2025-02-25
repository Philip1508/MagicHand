package magichand.modid.playerextension.activecast;

import magichand.modid.networking.S2CUpdater;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.playerextension.manaregeneration.ManaManager;
import magichand.modid.util.Rational;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class SpellChargeMachine {

    private ServerPlayerEntity player;
    private ManaManager manaManager;


    private int spellCostPerSecond;

    private Rational spellCostRationalTick;

    private Rational fractionalMana = new Rational(0);

    private Rational powerlevel = new Rational(0);
    boolean shotReady;


    public SpellChargeMachine(ServerPlayerEntity player)
    {
        // Debug Hardcoded to 3.


        this.player = player;
        this.manaManager = MagickaMachine.getPlayerRuntimeData(player).getManaManager();

        // ToDo; Will be replaced by ItemStack Information?
        this.spellCostPerSecond = 10;
        this.spellCostRationalTick = new Rational(spellCostPerSecond,20);


    }


    /**
     * Tick of SpellCharger.
     * @return Boolean:
     */
    public boolean tick()
    {

        fractionalMana = fractionalMana.add(spellCostRationalTick);
        if (fractionalMana.greaterOne())
        {
            boolean manaSubtracted = manaManager.decreaseMana(fractionalMana.getWholeAndFlatten());

            if (manaSubtracted)
            {
                S2CUpdater.serverToClientUpdateMana(player, manaManager);
            }

            return manaSubtracted;
        }

        powerlevel.setNumerator(powerlevel.getNumerator()+1);


        return manaManager.getMana() != 0;




    }


    /**
     * Determines wether the shot is ready.
     * @return - Null, if not ready; Powerlevel of the Spell if ready
     */
    public boolean shotReady()
    {
        return powerlevel.getNumerator() % 1 == 0;
    }

}
