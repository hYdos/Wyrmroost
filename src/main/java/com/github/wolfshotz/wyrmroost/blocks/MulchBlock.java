package com.github.wolfshotz.wyrmroost.blocks;

import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.AbstractFlowerFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ToolType;

import java.util.List;
import java.util.Random;

public class MulchBlock extends SnowyDirtBlock implements BonemealableBlock
{
    public MulchBlock()
    {
        super(WRBlocks.properties(Material.DIRT, WRSounds.Types.MULCH)
                .strength(0.5f)
                .harvestTool(ToolType.SHOVEL));
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient)
    {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state)
    {
        return true;
    }

    //taken from GrassBlock todo: optimize?
    @SuppressWarnings("unchecked")
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state)
    {
        BlockPos blockpos = pos.above();
        BlockState blockstate = Blocks.GRASS.defaultBlockState();

        label48:
        for (int i = 0; i < 128; ++i)
        {
            BlockPos blockpos1 = blockpos;

            for (int j = 0; j < i / 16; ++j)
            {
                blockpos1 = blockpos1.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!level.getBlockState(blockpos1.below()).is(this) || level.getBlockState(blockpos1).isCollisionShapeFullBlock(level, blockpos1))
                    continue label48;
            }

            BlockState blockstate2 = level.getBlockState(blockpos1);
            if (blockstate2.is(blockstate.getBlock()) && random.nextInt(10) == 0)
                ((BonemealableBlock) blockstate.getBlock()).performBonemeal(level, random, blockpos1, blockstate2);

            if (blockstate2.isAir())
            {
                BlockState blockstate1;
                if (random.nextInt(8) == 0)
                {
                    List<ConfiguredFeature<?, ?>> list = level.getBiome(blockpos1).getGenerationSettings().getFlowerFeatures();
                    if (list.isEmpty()) continue;

                    ConfiguredFeature<?, ?> feature = list.get(0);
                    AbstractFlowerFeature<FeatureConfiguration> flowers = (AbstractFlowerFeature<FeatureConfiguration>) feature.feature;
                    blockstate1 = flowers.getRandomFlower(random, blockpos1, feature.config());
                }
                else blockstate1 = blockstate;

                if (blockstate1.canSurvive(level, blockpos1))
                    level.setBlock(blockpos1, blockstate1, 3);
            }
        }

    }
}
