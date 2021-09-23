package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.blocks.tile.WRSignBlockEntity;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class WRBlockEntities<T extends BlockEntity> extends BlockEntityType<T>
{
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Wyrmroost.MOD_ID);

    public static final RegistryObject<BlockEntityType<?>> CUSTOM_SIGN = register("sign", WRSignBlockEntity::new, () -> SignRenderer::new);

    @Nullable private final Supplier<Function<BlockEntityRenderDispatcher, BlockEntityRenderer<T>>> renderer;

    public WRBlockEntities(Supplier<? extends T> factory, Supplier<Function<BlockEntityRenderDispatcher, BlockEntityRenderer<T>>> renderer)
    {
        super(factory, null, null);
        this.renderer = renderer;
    }

    @Override
    public boolean isValid(Block block)
    {
        return block instanceof Validator && ((Validator) block).isValidEntity(this);
    }

    public void callBack()
    {
        if (ModUtils.isClient() && renderer != null)
            ClientRegistry.bindTileEntityRenderer(this, renderer.get());
    }

    public static <T extends BlockEntity> RegistryObject<BlockEntityType<?>> register(String name, Supplier<T> factory, @Nullable Supplier<Function<BlockEntityRenderDispatcher, BlockEntityRenderer<T>>> renderer)
    {
        return REGISTRY.register(name, () -> new WRBlockEntities<>(factory, renderer));
    }

    public interface Validator
    {
        boolean isValidEntity(BlockEntityType<?> type);
    }
}
