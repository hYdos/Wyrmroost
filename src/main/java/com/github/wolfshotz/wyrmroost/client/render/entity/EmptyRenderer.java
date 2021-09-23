package com.github.wolfshotz.wyrmroost.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T>
{
    public EmptyRenderer(EntityRenderDispatcher renderManager)
    {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return null;
    }
}
