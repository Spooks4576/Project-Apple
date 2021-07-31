package com.skizzium.projectapple.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.skizzium.projectapple.entity.Skizzie;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class SkizzieModel<T extends Skizzie> extends EntityModel<T> {
    private final ModelPart lowerBody;
    private final ModelPart upperBody;
    private final ModelPart head;

    public SkizzieModel(ModelPart part) {
        this.lowerBody = part.getChild("lowerBody");
        this.upperBody = part.getChild("upperBody");
        this.head = part.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("lowerBody", CubeListBuilder.create().texOffs(26, 16).addBox(-4.0F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F, true), PartPose.offsetAndRotation(2.0F, 17.0F, -0.5F, 0.5F, 0.0F, 0.0F));
        part.addOrReplaceChild("upperBody", CubeListBuilder.create().texOffs(23, 25).addBox(-4.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, true)
                                                                                .texOffs(0, 16).addBox(-8.0F, 1.5F, 0.5F, 11.0F, 2.0F, 2.0F, true)
                                                                                .texOffs(0, 20).addBox(-8.0F, 4.0F, 0.5F, 11.0F, 2.0F, 2.0F, true)
                                                                                .texOffs(0, 24).addBox(-8.0F, 6.5F, 0.5F, 11.0F, 2.0F, 2.0F, true), PartPose.offset(2.0F, 7.0F, -0.5F));
        part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, true), PartPose.offset(0.0F, 3.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float f, float f1, float f2, float f3, float f4) {
        this.head.yRot = f3 / (180F / (float) Math.PI);
        this.head.xRot = f4 / (180F / (float) Math.PI);
    }

    @Override
    public void renderToBuffer(PoseStack matrix, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha){
        lowerBody.render(matrix, buffer, light, overlay);
        upperBody.render(matrix, buffer, light, overlay);
        head.render(matrix, buffer, light, overlay);
    }
}
