package com.github.wolfshotz.wyrmroost.blocks;

import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

public class CanisRootBlock extends CropBlock
{
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(0, 0, 0, 16, 13, 16),
            Block.box(0, 0, 0, 16, 14, 16)
    };

    public CanisRootBlock()
    {
        super(WRBlocks.plant().randomTicks().instabreak().sound(SoundType.CROP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AGE);
    }

    @Override
    public IntegerProperty getAgeProperty()
    {
        return AGE;
    }

    @Override
    public int getMaxAge()
    {
        return 3;
    }

    @Override
    protected ItemLike getBaseSeedId()
    {
        return WRBlocks.CANIS_ROOT.get().asItem();
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return SHAPE_BY_AGE[state.getValue(getAgeProperty())];
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        if (random.nextInt(3) != 0) super.randomTick(state, level, pos, random);
    }
}
