package com.github.wolfshotz.wyrmroost.client.render;

import com.github.wolfshotz.wyrmroost.client.model.WREntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import java.util.function.Supplier;

/**
 * Purpose of this class is to remove the need for creating a different "renderers" for each and every different entity model.
 * Achieved through making similar methods within a "wrapper model."
 * For example, instead of getting the texture from the renderer, we instead do it in the model.
 */
public class ModelWrappedRenderer<T extends Mob, M extends WREntityModel<T>> extends MobRenderer<T, M>
{
    public ModelWrappedRenderer(EntityRenderDispatcher manager, M model)
    {
        super(manager, model, 0f);

        addLayer(new RenderLayer<T, M>(this) // rendering overlays such as glowing eyes, armors, etc.
        {
            @Override
            public void render(PoseStack ms, MultiBufferSource buffer, int light, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float age, float yaw, float pitch)
            {
                model.postProcess(entity, ms, buffer, light, limbSwing, limbSwingAmount, age, yaw, pitch, partialTicks);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> IRenderFactory<T> factory(Supplier<Supplier<WREntityModel<T>>> model)
    {
        return m -> (EntityRenderer<? super T>) new ModelWrappedRenderer<>(m, (WREntityModel<Mob>) model.get().get());
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light)
    {
        this.shadowRadius = model.getShadowRadius(entity);
        super.render(entity, yaw, partialTicks, ms, buffer, light);
    }

    @Override
    protected void scale(T entity, PoseStack ms, float partialTicks)
    {
        model.scale(entity, ms, partialTicks);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return model.getTexture(entity);
    }
}
