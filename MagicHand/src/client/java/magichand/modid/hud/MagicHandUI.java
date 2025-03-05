package magichand.modid.hud;

import magichand.modid.ClientRepresentation.ClientRepresentationInterface;
import magichand.modid.MagicHand;
import magichand.modid.ClientRepresentation.PEClientRepresentation;
import magichand.modid.util.Rational;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;


// ToDo; This class is an utter mess and needs to be rewritten completely, as all rendering code.
public class MagicHandUI implements HudRenderCallback {

    private static final Identifier ICONS = new Identifier(MagicHand.MOD_ID,"textures/gui/icons.png");


    MinecraftClient client = MinecraftClient.getInstance();





    static int manaBarWidthOffset = 91;
    static int manaBarHeightOffset = 64;


    static int chargerBarWidthOffset = 91;
    static int chargerBarHeightOffset = 74;


    static int textWidthBonusOffset = 30;
    static int textHeightBonusOffset = 10;




    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        //PlayerRuntimeData runtimeData = MagickaMachine.getPlayerRuntimeData(client.player);

        PEClientRepresentation clientData = ClientRepresentationInterface.clientRepresentation;

        if (clientData == null)
        {
            return;
        }

        if (clientData.hidden)
        {
            return;
        }



        int width, height;
        width = client.getWindow().getScaledWidth();
        height = client.getWindow().getScaledHeight();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;


        Rational manaFull = clientData.mana;

        Rational mainCharger = clientData.mainCharge;
        Rational offCharger = clientData.offCharge;


        int mana = manaFull.getNumerator();
        int maxMana = manaFull.getDenominator();

        if (mana > maxMana) {mana = maxMana;}

        drawContext.drawCenteredTextWithShadow(textRenderer, Text.of(mana + " / " + maxMana),
                (width / 2 )
                ,
                height - (manaBarHeightOffset + textHeightBonusOffset), 0xFFFFFF);




        if (mainCharger != null)
        {
            renderCharger(drawContext, Hand.MAIN_HAND, mainCharger);
        }

        if (offCharger != null)
        {
            renderCharger(drawContext, Hand.OFF_HAND, offCharger);
        }

        renderManaBar(drawContext, mana, maxMana);



    }

    private void renderCharger(DrawContext drawContext, Hand hand, Rational charger) {

        int barWidth = drawContext.getScaledWindowWidth() / 2 - chargerBarWidthOffset;
        int barHeight = drawContext.getScaledWindowHeight() - chargerBarHeightOffset;

        if (hand == Hand.MAIN_HAND) {barWidth += 120;}


        int chargerBarWidthPixels = 62;

        float percentageCharge = (float) charger.getNumerator() / ((float) (charger.getDenominator() * 2));

        int translatedChargerPercentageWith = (int) (percentageCharge * (float) chargerBarWidthPixels) ;




        drawContext.drawTexture(ICONS, barWidth, barHeight, u, v3, chargerBarWidthPixels, 5);

        if (translatedChargerPercentageWith > 0)
        {
            drawContext.drawTexture(ICONS, barWidth, barHeight, u, v4, translatedChargerPercentageWith, 5);
        }



    }


    static int u = 0;
    static int u2 = 0;
    static int v = 0;
    static int v2 = 5;

    static int u3 = 0;
    static int v3 = 10;

    static int u4 = 0;
    static int v4 = 15;



    public void renderManaBar(DrawContext drawContext, int mana, int maxMana)
    {
        int barWidth = drawContext.getScaledWindowWidth() / 2 - manaBarWidthOffset;
        int barHeight = drawContext.getScaledWindowHeight() - manaBarHeightOffset;

        int manaBarTextureWidthPixels = 183;
        float percentageMana = (float) mana / (float) maxMana;
        int translatedManaPercentageWidth = (int)(percentageMana * (float) manaBarTextureWidthPixels);


        Sprite s;


        drawContext.drawTexture(ICONS, barWidth, barHeight, u, v, manaBarTextureWidthPixels, 5);
        if (translatedManaPercentageWidth > 0) {
            drawContext.drawTexture(ICONS, barWidth, barHeight, u2, v2, translatedManaPercentageWidth, 5);
        }


    }




}
