package net.emanueljdf09.dtrhmod.util;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.util.components.Mirror.MirrorComponent;
import net.emanueljdf09.dtrhmod.fabric.util.components.Mirror.MirrorComponentImpl;
import net.emanueljdf09.dtrhmod.fabric.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.fabric.util.components.ProgressionComponentImpl;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.cca.api.v3.block.BlockComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.block.BlockComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class ModComponents implements EntityComponentInitializer, WorldComponentInitializer, BlockComponentInitializer {


    public static final ComponentKey<ProgressionComponent> PROGRESSION_COMPONENT =
            ComponentRegistry.getOrCreate(
                    ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "progression_component"),
                    ProgressionComponent.class
            );

    public static final ComponentKey<MirrorComponent> MIRROR_COMPONENT =
                ComponentRegistry.getOrCreate(
                        ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mirror_component"),
                        MirrorComponent.class
                );



    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {

        registry.registerForPlayers(
                PROGRESSION_COMPONENT,
                ProgressionComponentImpl::new,
                RespawnCopyStrategy.ALWAYS_COPY
        );

    }


    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(MIRROR_COMPONENT, world -> new MirrorComponentImpl());
    }

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
    }
}
