package magichand.modid.ClientRepresentation;

import magichand.modid.playerextension.MagickaMachineState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public abstract class ClientRepresentationInterface {

    public static PEClientRepresentation clientRepresentation;

    public static void clientTickExtension(ClientPlayerEntity clientPlayerEntity) {

        if (clientRepresentation == null) {return;}

        clientTick(clientPlayerEntity);
    }


    /**
     * This Method processes a tick on the ClientSide.
     *
     * @param cPlayer
     */
    private static void clientTick(ClientPlayerEntity cPlayer) {


        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null || !(networkHandler.getSessionId().equals(clientRepresentation.sessionId)))
        {
            clientRepresentation = null;
            return;
        }



        clientRepresentation.hudInactivityCheck(cPlayer);

        if (clientRepresentation.state == MagickaMachineState.MANA_PASSIVE_REGENERATION) {
            clientRepresentation.manaFractional = clientRepresentation.manaFractional.add(clientRepresentation.manaRegeneration);
            if (clientRepresentation.manaFractional.greaterOne()) {
                int regeneratedPoints = clientRepresentation.manaFractional.getWholeAndFlatten();
                if (clientRepresentation.mana.getNumerator() + regeneratedPoints > clientRepresentation.mana.getDenominator()) {
                    clientRepresentation.mana.setNumerator(clientRepresentation.mana.getDenominator());
                } else {
                    clientRepresentation.mana.setNumerator(clientRepresentation.mana.getNumerator() + regeneratedPoints);
                }

            }
        }





        if (clientRepresentation.notRefreshed())
        {
            clientRepresentation = null;
        }

    }



}
