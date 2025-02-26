package magichand.modid.networking;

import magichand.modid.networking.headers.UpdaterHeaderConstants;
import magichand.modid.playerextension.MagickaMachineState;
import magichand.modid.playerextension.PlayerRuntimeData;
import magichand.modid.playerextension.manaregeneration.ManaManager;
import magichand.modid.playerextension.maskedconstants.ClientPlayerRepresentationConstants;
import magichand.modid.util.Rational;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.core.jmx.Server;

public class S2CUpdater {



    private static NbtCompound manaToNbt(ManaManager manaManager)
    {


        NbtCompound manaNbt = new NbtCompound();

        manaNbt.put(ClientPlayerRepresentationConstants.C_MANAMANAGER_MANA,
                Rational.rationalToNbt(manaManager.getFullManaAsRational()));

        manaNbt.put(ClientPlayerRepresentationConstants.C_MANAMANAGER_FRACTIONALMANA,
                Rational.rationalToNbt(manaManager.getFractionalMana()));

        manaNbt.put(ClientPlayerRepresentationConstants.C_MANAMANAGER_REGENERATIONALMANA,
                Rational.rationalToNbt(manaManager.getRegenerationalFactor()));

        return manaNbt;
    }


    public static void serverToClientUpdateMana(ServerPlayerEntity player, ManaManager manaManager)
    {

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt(UpdaterHeaderConstants.UPDATE_HEADER_KEY, UpdaterHeaderConstants.UPDATE_MANA_HEADERNUMBER);
        nbtCompound.put(UpdaterHeaderConstants.UPDATE_MANA_NBTKEY, manaToNbt(manaManager));

        sendMessage(player, nbtCompound);
    }


    private static NbtCompound stateToNbt(MagickaMachineState state)
    {
        NbtCompound stateNbt = new NbtCompound();

        switch (state)
        {
            case MANA_PASSIVE_REGENERATION -> {
                stateNbt.putString(ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_KEY,
                        ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_PASSIVE);
            }

            case MANA_ACTIVE_CAST -> {
                stateNbt.putString(ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_KEY,
                        ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_ACTIVE);
            }
        }

        return stateNbt;


    }

    public static void serverToClientUpdateState(ServerPlayerEntity player, MagickaMachineState state)
    {

        NbtCompound nbtCompound = new NbtCompound();

        nbtCompound.putInt(UpdaterHeaderConstants.UPDATE_HEADER_KEY, UpdaterHeaderConstants.UPDATE_STATE);
        nbtCompound.put(UpdaterHeaderConstants.UPDATE_STATE_NBTKEY, stateToNbt(state));


        sendMessage(player, nbtCompound);
    }

    public static void serverToClientFullSynch(ServerPlayerEntity player, PlayerRuntimeData runtimeData)
    {
        NbtCompound nbtCompound = new NbtCompound();

        nbtCompound.putInt(UpdaterHeaderConstants.UPDATE_HEADER_KEY, UpdaterHeaderConstants.UPDATE_FULL);

        nbtCompound.put(UpdaterHeaderConstants.UPDATE_MANA_NBTKEY, manaToNbt(runtimeData.getManaManager()));
        nbtCompound.put(UpdaterHeaderConstants.UPDATE_STATE_NBTKEY, stateToNbt(runtimeData.getState()));

        sendMessage(player, nbtCompound);


    }


    private static void sendMessage(ServerPlayerEntity player, NbtCompound nbtCompound)
    {
        PacketByteBuf packet = PacketByteBufs.create();

        packet.writeNbt(nbtCompound);


        ServerPlayNetworking.send(player, PacketRegistrator.RUNTIMEDATA_S2C, packet);

    }






}
