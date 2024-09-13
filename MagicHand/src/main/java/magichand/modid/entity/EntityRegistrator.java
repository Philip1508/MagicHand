package magichand.modid.entity;

import magichand.modid.MagicHand;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class EntityRegistrator {

    public static EntityType<Projectile> PROJECTILE;
    public static EntityType<SprayMagicProjectile> SPRAY_PROJECTILE;




    public static void initializeAndRegister()
    {
        PROJECTILE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(MagicHand.MOD_ID, "projectile"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, Projectile::new).dimensions(EntityDimensions.changing(0.5f,0.5f)).build()
        );


        SPRAY_PROJECTILE = Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(MagicHand.MOD_ID, "spray-projectile"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, SprayMagicProjectile::new).dimensions(EntityDimensions.changing(0.2f,0.2f)).build()
        );

    }



}
