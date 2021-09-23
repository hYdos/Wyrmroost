package com.github.wolfshotz.wyrmroost.client;

import com.github.wolfshotz.wyrmroost.registry.WRWorld;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class WRDimensionRenderInfo extends DimensionSpecialEffects
{
    private final int weatherTime = 0;

    public WRDimensionRenderInfo()
    {
        super(128f, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
//        setWeatherRenderHandler(this::renderWeather);
//        setWeatherParticleRenderHandler(this::renderWeatherParticles);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 color, float density)
    {
        return color.multiply(density * 0.94F + 0.06F, density * 0.94F + 0.06F, density * 0.91F + 0.09F);
    }

    @Override
    public boolean isFoggyAt(int p_230493_1_, int p_230493_2_)
    {
        return false;
    }

    private void renderWeather(int ticks, float partialTicks, ClientLevel world, Minecraft minecraft, LightTexture lightTexture, double x, double y, double z)
    {

    }

    private void renderWeatherParticles(int ticks, ClientLevel level, Minecraft client, Camera renderInfo)
    {
    }

    // woulda put it in static block but class loading :)
    public static void init()
    {
        try
        {
            Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> infoMap = ObfuscationReflectionHelper.getPrivateValue(DimensionSpecialEffects.class, null, "field_239208_a_");
            infoMap.put(WRWorld.THE_WYRMROOST.location(), new WRDimensionRenderInfo());
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Could not apply Wyrmroost Dimension Render info.\n" + e);
        }
    }
}
