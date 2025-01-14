package com.github.wolfshotz.wyrmroost.client;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.blocks.ThinLogBlock;
import com.github.wolfshotz.wyrmroost.client.render.RenderHelper;
import com.github.wolfshotz.wyrmroost.client.render.TarragonTomeRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.projectile.BreathWeaponRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.items.LazySpawnEggItem;
import com.github.wolfshotz.wyrmroost.registry.*;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.CameraType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * EventBus listeners on CLIENT distribution
 * Also a client helper class because yes.
 */
@SuppressWarnings("unused")
public class ClientEvents
{
    public static boolean keybindFlight = true;

    public static void init()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        bus.addListener(ClientEvents::clientSetup);
        bus.addListener(ClientEvents::stitchTextures);
        bus.addListener(ClientEvents::itemColors);
        bus.addListener(ClientEvents::bakeParticles);
        bus.addListener(ClientEvents::bakeModels);

        forgeBus.addListener(RenderHelper::renderWorld);
        forgeBus.addListener(RenderHelper::renderOverlay);
        forgeBus.addListener(RenderHelper::renderEntities);
        forgeBus.addListener(ClientEvents::cameraPerspective);

        WRDimensionRenderInfo.init();
    }

    // ====================
    //       Mod Bus
    // ====================

    private static void clientSetup(final FMLClientSetupEvent event)
    {
        WRKeybind.registerKeys();

        ThinLogBlock.consumeThinLogs(b -> ItemBlockRenderTypes.setRenderLayer(b, RenderType.cutout()),
                WRBlocks.DYING_CORIN_WOOD,
                WRBlocks.RED_CORIN_WOOD,
                WRBlocks.TEAL_CORIN_WOOD,
                WRBlocks.SILVER_CORIN_WOOD,
                WRBlocks.PRISMARINE_CORIN_WOOD);

        event.enqueueWork(() ->
        {
            WRIO.screenSetup();

            WoodType.values().filter(w -> w.name().contains(Wyrmroost.MOD_ID)).forEach(Sheets::addWoodType);

            for (BlockEntityType<?> entry : ModUtils.getRegistryEntries(WRBlockEntities.REGISTRY))
                if (entry instanceof WRBlockEntities<?>) ((WRBlockEntities<?>) entry).callBack();
        });
    }

    private static void bakeParticles(ParticleFactoryRegisterEvent event)
    {
        for (ParticleType<?> entry : ModUtils.getRegistryEntries(WRParticles.REGISTRY))
            if (entry instanceof WRParticles<?>) ((WRParticles<?>) entry).bake();
    }

    private static void stitchTextures(TextureStitchEvent.Pre evt)
    {
        if (evt.getMap().location() == TextureAtlas.LOCATION_BLOCKS)
            evt.addSprite(BreathWeaponRenderer.BLUE_FIRE);
    }

    private static void itemColors(ColorHandlerEvent.Item evt)
    {
        ItemColors handler = evt.getItemColors();
        ItemColor eggFunc = (stack, tintIndex) -> ((LazySpawnEggItem<?>) stack.getItem()).getColor(tintIndex);
        for (LazySpawnEggItem<?> e : LazySpawnEggItem.SPAWN_EGGS) handler.register(eggFunc, e);

        handler.register((stack, index) -> ((DyeableLeatherItem) stack.getItem()).getColor(stack), WRItems.LEATHER_DRAGON_ARMOR.get());
    }

    private static void bakeModels(ModelRegistryEvent event)
    {
        ModelLoader.addSpecialModel(TarragonTomeRenderer.SPRITE_MODEL_LOCATION);
    }

    // =====================
    //      Forge Bus
    // =====================

    private static void cameraPerspective(EntityViewRenderEvent.CameraSetup event)
    {
        Minecraft mc = getClient();
        Entity entity = mc.player.getVehicle();
        if (!(entity instanceof TameableDragonEntity)) return;
        CameraType view = mc.options.getCameraType();

        if (view != CameraType.FIRST_PERSON)
            ((TameableDragonEntity) entity).setMountCameraAngles(view == CameraType.THIRD_PERSON_BACK, event);
    }

    // =====================

    // for class loading issues
    public static Minecraft getClient()
    {
        return Minecraft.getInstance();
    }

    public static ClientLevel getLevel()
    {
        return getClient().level;
    }

    public static Player getPlayer()
    {
        return getClient().player;
    }

    public static Vec3 getProjectedView()
    {
        return getClient().gameRenderer.getMainCamera().getPosition();
    }

    public static float getPartialTicks()
    {
        return getClient().getFrameTime();
    }

    public static <T extends Entity & ItemSupplier> ThrownItemRenderer<T> spriteRenderer(EntityRenderDispatcher m)
    {
        return new ThrownItemRenderer<>(m, getClient().getItemRenderer());
    }

    public static double getViewCollision(double wanted, Entity entity)
    {
        Camera info = getClient().gameRenderer.getMainCamera();
        Vec3 position = info.getPosition();
        Vector3f forwards = info.getLookVector();
        for (int i = 0; i < 8; ++i)
        {
            float f = (float) ((i & 1) * 2 - 1);
            float f1 = (float) ((i >> 1 & 1) * 2 - 1);
            float f2 = (float) ((i >> 2 & 1) * 2 - 1);
            f = f * 0.1F;
            f1 = f1 * 0.1F;
            f2 = f2 * 0.1F;
            Vec3 vector3d = position.add(f, f1, f2);
            Vec3 vector3d1 = new Vec3(position.x - forwards.x() * wanted + f + f2, position.y - forwards.y() * wanted + f1, position.z - forwards.z() * wanted + f2);
            HitResult rtr = entity.level.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            if (rtr.getType() != HitResult.Type.MISS)
            {
                double distance = rtr.getLocation().distanceTo(position);
                if (distance < wanted) wanted = distance;
            }
        }

        return wanted;
    }
}
