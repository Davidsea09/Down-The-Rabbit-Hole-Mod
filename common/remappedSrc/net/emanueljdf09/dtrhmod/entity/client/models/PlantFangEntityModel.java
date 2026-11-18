package net.emanueljdf09.dtrhmod.entity.client.models;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PlantFangEntityModel<T extends Entity> extends HierarchicalModel<T> {
    private static final String BASE = "base";
    private static final String UPPER_JAW = "upper_jaw";
    private static final String LOWER_JAW = "lower_jaw";
    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart upperJaw;
    private final ModelPart lowerJaw;

    public PlantFangEntityModel(ModelPart root) {
        this.root = root;
        this.base = root.getChild("base");
        this.upperJaw = root.getChild("upper_jaw");
        this.lowerJaw = root.getChild("lower_jaw");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 10.0F, 12.0F, 10.0F), PartPose.offset(-5.0F, 24.0F, -5.0F));
        CubeListBuilder modelPartBuilder = CubeListBuilder.create().texOffs(40, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
        modelPartData.addOrReplaceChild("upper_jaw", modelPartBuilder, PartPose.offset(1.5F, 24.0F, -4.0F));
        modelPartData.addOrReplaceChild("lower_jaw", modelPartBuilder, PartPose.offsetAndRotation(-1.5F, 24.0F, 4.0F, 0.0F, (float)Math.PI, 0.0F));
        return LayerDefinition.create(modelData, 64, 32);
    }

    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = limbAngle * 2.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        f = 1.0F - f * f * f;
        this.upperJaw.zRot = (float)Math.PI - f * 0.35F * (float)Math.PI;
        this.lowerJaw.zRot = (float)Math.PI + f * 0.35F * (float)Math.PI;
        float g = (limbAngle + Mth.sin(limbAngle * 2.7F)) * 0.6F * 12.0F;
        this.upperJaw.y = 24.0F - g;
        this.lowerJaw.y = this.upperJaw.y;
        this.base.y = this.upperJaw.y;
    }

    public ModelPart root() {
        return this.root;
    }
}