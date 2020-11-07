package com.github.wolfshotz.wyrmroost.client.render.entity.royal_red;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.render.entity.AbstractDragonRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoyalRedEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class RoyalRedRenderer extends AbstractDragonRenderer<RoyalRedEntity, RoyalRedModel>
{
    public static final ResourceLocation CHILD = Wyrmroost.rl(BASE_PATH + "royal_red/child.png");
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[4];

    public RoyalRedRenderer(EntityRendererManager manager)
    {
        super(manager, new RoyalRedModel(), 2.5f);
        addLayer(new ArmorLayer(RoyalRedEntity.ARMOR_SLOT));
    }

    @Override
    public ResourceLocation getEntityTexture(RoyalRedEntity entity)
    {
        if (entity.isChild()) return CHILD;
        int index = (entity.isMale()? 0 : 1) + (entity.getVariant() == -1? 2 : 0);
        if (TEXTURES[index] == null)
        {
            String path = BASE_PATH + "royal_red/" + ((index & 1) != 0? "female" : "male");
            if ((index & 2) != 0) path += "_spe";
            return TEXTURES[index] = Wyrmroost.rl(path + ".png");
        }
        return TEXTURES[index];
    }
}