package magichand.modid.hud;

import magichand.modid.MagicHand;
import magichand.modid.playerextension.PEClientRepresentation;
import magichand.modid.playerextension.MagickaMachine;
import magichand.modid.util.Rational;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MagicHandUI implements HudRenderCallback {

    private static final Identifier ICONS = new Identifier(MagicHand.MOD_ID,"textures/gui/icons.png");


    MinecraftClient client = MinecraftClient.getInstance();





    static int manaBarWidthOffset = 91;
    static int manaBarHeightOffset = 64;
    static int textWidthBonusOffset = 30;
    static int textHeightBonusOffset = 10;




    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        //PlayerRuntimeData runtimeData = MagickaMachine.getPlayerRuntimeData(client.player);

        PEClientRepresentation clientData = MagickaMachine.clientRepresentation;

        if (clientData == null)
        {
            return;
        }


        int width, height;
        width = client.getWindow().getScaledWidth();
        height = client.getWindow().getScaledHeight();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;


        Rational manaFull = clientData.mana;


        int mana = manaFull.getNumerator();
        int maxMana = manaFull.getDenominator();

        if (mana > maxMana) {mana = maxMana;}

        drawContext.drawCenteredTextWithShadow(textRenderer, Text.of(String.valueOf(mana) + " / " + String.valueOf(maxMana)),
                (width / 2 )
                ,
                height - (manaBarHeightOffset + textHeightBonusOffset), 0xFFFFFF);

        renderManaBar(drawContext, mana, maxMana);


    }


    static int u = 0;
    static int u2 = 0;
    static int v = 0;
    static int v2 = 5;



    public void renderManaBar(DrawContext drawContext, int mana, int maxMana)
    {
        int barWidth = drawContext.getScaledWindowWidth() / 2 - manaBarWidthOffset;
        int barHeight = drawContext.getScaledWindowHeight() - manaBarHeightOffset;

        int manaBarTextureWidthPixels = 183;
        float percentageMana = (float) mana / (float) maxMana;
        int translatedManaPercentageWidth = (int)(percentageMana * 183.0F);



        drawContext.drawTexture(ICONS, barWidth, barHeight, u, v, manaBarTextureWidthPixels, 5);
        if (translatedManaPercentageWidth > 0) {
            drawContext.drawTexture(ICONS, barWidth, barHeight, u2, v2, translatedManaPercentageWidth, 5);
        }




    }


}
