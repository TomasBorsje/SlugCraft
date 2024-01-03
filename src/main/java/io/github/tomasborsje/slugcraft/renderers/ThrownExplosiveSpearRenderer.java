package io.github.tomasborsje.slugcraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.tomasborsje.slugcraft.SlugCraft;
import io.github.tomasborsje.slugcraft.entities.ThrownExplosiveSpear;
import io.github.tomasborsje.slugcraft.models.ExplosiveSpearModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ThrownExplosiveSpearRenderer extends EntityRenderer<ThrownExplosiveSpear> {
    public static final ResourceLocation EXPLOSIVE_SPEAR_LOCATION = new ResourceLocation(SlugCraft.MODID, "textures/item/explosive_spear.png");
    private final ExplosiveSpearModel model;

    public ThrownExplosiveSpearRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ExplosiveSpearModel(context.bakeLayer(ExplosiveSpearModel.LAYER_LOCATION));
    }


    public void render(ThrownExplosiveSpear p_116111_, float p_116112_, float p_116113_, PoseStack p_116114_, MultiBufferSource p_116115_, int p_116116_) {
        p_116114_.pushPose();
        p_116114_.mulPose(Axis.YP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.yRotO, p_116111_.getYRot()) - 90.0F));
        p_116114_.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(p_116113_, p_116111_.xRotO, p_116111_.getXRot()) + 90.0F));
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(p_116115_, this.model.renderType(this.getTextureLocation(p_116111_)), false, false);
        this.model.renderToBuffer(p_116114_, vertexconsumer, p_116116_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        p_116114_.popPose();
        super.render(p_116111_, p_116112_, p_116113_, p_116114_, p_116115_, p_116116_);
    }

    public ResourceLocation getTextureLocation(ThrownExplosiveSpear p_116109_) {
        return EXPLOSIVE_SPEAR_LOCATION;
    }
}
