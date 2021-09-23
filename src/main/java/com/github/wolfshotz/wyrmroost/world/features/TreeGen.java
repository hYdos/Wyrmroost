package com.github.wolfshotz.wyrmroost.world.features;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Just an extension of {@link Tree} to allow use of vanilla sapling blocks.
 *
 * This makes the configred feature using abstract away from using the vanilla {@link BaseTreeFeatureConfig}
 * in favor of using just a normal feature.
 * In fact, it also allows more than just tree features. If for whatever reason a datapack replaces the tree configured
 * features, then the saplings growing will grow that replaced feature.
 *
 */
public class TreeGen extends AbstractTreeGrower
{
    private final Supplier<ConfiguredFeature<?, ?>> treeFeature;

    public TreeGen(Supplier<ConfiguredFeature<?, ?>> treeFeature)
    {
        this.treeFeature = treeFeature;
    }

    @Override
    public boolean growTree(ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, Random random)
    {
        return treeFeature.get().place(level, generator, random, pos);
    }

    @Nullable
    @Override
    protected ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random random, boolean beehive)
    {
        return null;
    }
}
