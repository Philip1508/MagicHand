package magichand.modid.render.entity;

import magichand.modid.MagicHand;
import magichand.modid.entity.Projectile;
import magichand.modid.entity.SprayMagicProjectile;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SprayMagicProjectileRender extends EntityRenderer<SprayMagicProjectile> {


    private static final Identifier TEXTURE = new Identifier(MagicHand.MOD_ID,"textures/entity/restoration_orb.png");

    private static final RenderLayer LAYER = RenderLayer.getItemEntityTranslucentCull(TEXTURE);


    final float aparrentU1 = (0 % 4 * 16 + 0) / 64.0f;
    final float aparrentU2 = (0 % 4 * 16 + 16) / 64.0f;
    final float aparrentV1 = (0 / 4 * 16 + 0) / 64.0f;
    final float aparrentV2 = (0 / 4 * 16 + 16) / 64.0f;

    public SprayMagicProjectileRender(EntityRendererFactory.Context context)
    {
        super(context);
        this.shadowRadius = 0.0f;
        this.shadowOpacity = 0.15f;
    }

    // toDo; Logisch Äquivalentes Refactoring. Unhinged.
    @Override
    public void render(SprayMagicProjectile magicalAttack, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {

        if (magicalAttack.isInvisible())
        {
            return;
        }

        matrixStack.push();


        matrixStack.translate(0.0f, 0.1f, 0.0f);

        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));

        //float apparrentScale = magicalAttack.getOrbSize();
        float apparrentScale = 1;

        if (apparrentScale > 1.5f)
        {
            apparrentScale = 1.5f;
        }


        matrixStack.scale(apparrentScale, apparrentScale, apparrentScale);



        /*
        int orbSize = 0 // restorationOrb.getOrbSize();


        float aparrentU1 = (orbSize % 4 * 16 + 0) / 64.0f;
        float aparrentU2 = (orbSize % 4 * 16 + 16) / 64.0f;
        float aparrentV1 = (orbSize / 4 * 16 + 0) / 64.0f;
        float aparrentV2 = (orbSize / 4 * 16 + 16) / 64.0f; */





        //float r = ((float)magicalAttack.age + tickDelta) / 2.0f;
        float r = 2.0f;




        int[] rgb = new int[3];

        rgb[0] = 0;
        rgb[1] = 244;
        rgb[2] = 244;

        MatrixStack.Entry entry = matrixStack.peek();

        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);

        int light_l = 240;

        SprayMagicProjectileRender.vertex(vertexConsumer, matrix4f, matrix3f, -0.5f, -0.25f, rgb[0], rgb[1], rgb[2], aparrentU1, aparrentV2, light_l);
        SprayMagicProjectileRender.vertex(vertexConsumer, matrix4f, matrix3f, 0.5f, -0.25f, rgb[0], rgb[1], rgb[2], aparrentU2, aparrentV2, light_l);
        SprayMagicProjectileRender.vertex(vertexConsumer, matrix4f, matrix3f, 0.5f, 0.75f, rgb[0], rgb[1], rgb[2], aparrentU2, aparrentV1, light_l);
        SprayMagicProjectileRender.vertex(vertexConsumer, matrix4f, matrix3f, -0.5f, 0.75f, rgb[0], rgb[1], rgb[2], aparrentU1, aparrentV1, light_l);







        matrixStack.pop();


        //super.render(restorationOrb, f, g, matrixStack, vertexConsumerProvider, i);

    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        vertexConsumer.vertex(positionMatrix, x, y, 0.0f).color(red, green, blue, 128).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();

        //vertexConsumer.vertex(positionMatrix, x, y, 0.0f).color(red, green, blue, 128).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f);
    }


    @Override
    public Identifier getTexture(SprayMagicProjectile entity) {
        return TEXTURE;
    }
}



