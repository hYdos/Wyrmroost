package com.github.wolfshotz.wyrmroost.world.features;

import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import java.util.Random;

public class NoExposureReplacementFeature extends Feature<ReplaceBlockConfiguration>
{
    public NoExposureReplacementFeature()
    {
        super(ReplaceBlockConfiguration.CODEC);
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, Random random, BlockPos pos, ReplaceBlockConfiguration config)
    {
        if (level.getBlockState(pos).is(config.target.getBlock()) && checkExposure(level, pos))
            level.setBlock(pos, config.state, 2);

        return true;
    }

    private static boolean checkExposure(WorldGenLevel level, BlockPos initialPos)
    {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (Direction direction : ModUtils.DIRECTIONS)
        {
            BlockState state = level.getBlockState(pos.setWithOffset(initialPos, direction));
            if (state.isAir(level, pos)) return false;
        }
        return true;
    }
}
