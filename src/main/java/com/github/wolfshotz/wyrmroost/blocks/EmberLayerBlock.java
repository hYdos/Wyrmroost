package com.github.wolfshotz.wyrmroost.blocks;

import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

public class EmberLayerBlock extends SnowLayerBlock
{
    public EmberLayerBlock()
    {
        super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.NETHER)
                .lightLevel(s -> 3)
                .randomTicks()
                .strength(0.25f)
                .isValidSpawn((state, level, pos, type) -> type.fireImmune())
                .hasPostProcess((s, l, p) -> true)
                .emissiveRendering((s, l, p) -> true)
                .sound(SoundType.SAND));
    }

    // inherit ember block behaviours

    @Override
    public void stepOn(Level level, BlockPos pos, Entity stepping)
    {
        WRBlocks.EMBER_BLOCK.get().stepOn(level, pos, stepping);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rng)
    {
        WRBlocks.EMBER_BLOCK.get().randomTick(state, level, pos, rng);
    }
}
