package magichand.modid.networking;


import magichand.modid.ClientRepresentation.ClientRepresentationInterface;
import magichand.modid.networking.headers.UpdaterHeaderConstants;
import magichand.modid.playerextension.MagickaMachineState;
import magichand.modid.ClientRepresentation.PEClientRepresentation;
import magichand.modid.playerextension.maskedconstants.ClientPlayerRepresentationConstants;
import magichand.modid.util.Rational;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.Objects;
import java.util.UUID;

// Hier müssen die Spieler Laufzeitdaten empfangen werden.
public class RuntimeDataInformationPacket {


    public static void receive(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler,
                               PacketByteBuf packetByteBuf, PacketSender packetSender) {


        NbtCompound nbt = packetByteBuf.readUnlimitedNbt();

        if (nbt == null) {return;}

        int type = nbt.getInt(UpdaterHeaderConstants.UPDATE_HEADER_KEY);

        if (type != 1 && ClientRepresentationInterface.clientRepresentation == null) {return;}



        switch (type)
        {
            case UpdaterHeaderConstants.UPDATE_FULL -> {
                if (ClientRepresentationInterface.clientRepresentation == null ||
                        !clientPlayNetworkHandler.getSessionId()
                                .equals(ClientRepresentationInterface.clientRepresentation.sessionId))
                {
                    generateClientRepresentation(clientPlayNetworkHandler.getSessionId(), nbt);
                }


                NbtCompound manaNbt = nbt.getCompound(UpdaterHeaderConstants.UPDATE_MANA_NBTKEY);
                NbtCompound stateNbt = nbt.getCompound(UpdaterHeaderConstants.UPDATE_STATE_NBTKEY);

                updateClientMana(manaNbt);
                updateClientState(stateNbt);

            }

            case UpdaterHeaderConstants.UPDATE_MANA_HEADERNUMBER -> {
                NbtCompound manaNbt = nbt.getCompound(UpdaterHeaderConstants.UPDATE_MANA_NBTKEY);
                updateClientMana(manaNbt);
                ClientRepresentationInterface.clientRepresentation.unhide();

            }

            case UpdaterHeaderConstants.UPDATE_STATE -> {
                NbtCompound stateNbt = nbt.getCompound(UpdaterHeaderConstants.UPDATE_STATE_NBTKEY);
                updateClientState(stateNbt);

            }


        }

        ClientRepresentationInterface.clientRepresentation.refresh();




    }


    private static void generateClientRepresentation(UUID sessionId, NbtCompound nbtCompound)
    {


        NbtCompound manaNbt = nbtCompound.getCompound(UpdaterHeaderConstants.UPDATE_MANA_NBTKEY);

        Rational mana = Rational.rationalFromNbt(manaNbt.getCompound(ClientPlayerRepresentationConstants.C_MANAMANAGER_MANA));
        Rational manaFractional = Rational.rationalFromNbt(manaNbt.getCompound(ClientPlayerRepresentationConstants.C_MANAMANAGER_FRACTIONALMANA));
        Rational manaRegenerational = Rational.rationalFromNbt(manaNbt.getCompound(ClientPlayerRepresentationConstants.C_MANAMANAGER_REGENERATIONALMANA));



        NbtCompound stateNbt = nbtCompound.getCompound(UpdaterHeaderConstants.UPDATE_STATE_NBTKEY);

        MagickaMachineState state = stateNbtToState(stateNbt);


        ClientRepresentationInterface.clientRepresentation = new PEClientRepresentation(sessionId ,mana, manaRegenerational, manaFractional, state);



    }

    private static void updateClientMana(NbtCompound manaNbt)
    {
        PEClientRepresentation clientRepresentation = ClientRepresentationInterface.clientRepresentation;


        Rational mana = Rational.rationalFromNbt(manaNbt.getCompound(ClientPlayerRepresentationConstants.C_MANAMANAGER_MANA));
        Rational manaFractional = Rational.rationalFromNbt(manaNbt.getCompound(ClientPlayerRepresentationConstants.C_MANAMANAGER_FRACTIONALMANA));
        Rational manaRegenerational = Rational.rationalFromNbt(manaNbt.getCompound(ClientPlayerRepresentationConstants.C_MANAMANAGER_REGENERATIONALMANA));

        clientRepresentation.mana = mana;
        clientRepresentation.manaRegeneration = manaRegenerational;
        clientRepresentation.manaFractional = manaFractional;

    }

    private static void updateClientState(NbtCompound stateNbt)
    {
        PEClientRepresentation clientRepresentation = ClientRepresentationInterface.clientRepresentation;

        clientRepresentation.state = stateNbtToState(stateNbt);



    }


    private static MagickaMachineState stateNbtToState(NbtCompound stateNbt)
    {

        if (Objects.equals(stateNbt.getString(ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_KEY),
                ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_PASSIVE))
        {
            return MagickaMachineState.MANA_PASSIVE_REGENERATION;
        }


        if (Objects.equals(stateNbt.getString(ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_KEY),
                ClientPlayerRepresentationConstants.C_CASTMACHINE_STATE_ACTIVE))
        {
            return MagickaMachineState.MANA_ACTIVE_CAST;
        }

        return MagickaMachineState.MANA_ACTIVE_CAST;



    }




}
