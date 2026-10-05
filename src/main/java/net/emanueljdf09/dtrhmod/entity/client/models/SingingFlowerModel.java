package net.emanueljdf09.dtrhmod.entity.client.models;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SingingFlowerModel extends GeoModel<SingingFlowerEntity> {

    @Override
    public Identifier getModelResource(SingingFlowerEntity entity) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "geo/entity/singing_flower.geo.json");
    }

    @Override
    public Identifier getTextureResource(SingingFlowerEntity entity) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "textures/entity/singing_flower_" + entity.getVariant() + ".png");
    }

    @Override
    public Identifier getAnimationResource(SingingFlowerEntity entity) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "animations/entity/singing_flower.animation.json");
    }
}