package com.github.wolfshotz.wyrmroost.blocks;

import net.minecraft.block.*;
import net.minecraft.world.item.Item;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.WeepingVinesPlant;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;

public class GrowingPlantBodyBlock extends GrowingPlantBodyBlock
{
    private final Supplier<Block> tip;

    public GrowingPlantBodyBlock(Properties properties, Supplier<Block> tip)
    {
        super(properties, Direction.DOWN, WeepingVinesPlant.SHAPE, false);
        this.tip = tip;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockPos below = pos.relative(growthDirection.getOpposite());
        BlockState belowState = worldIn.getBlockState(below);
        Block belowBlock = belowState.getBlock();

        return canAttachToBlock(belowBlock) && (belowBlock == getHeadBlock() || belowBlock == getBodyBlock() || Block.isFaceFull(belowState.getCollisionShape(worldIn, pos), growthDirection));
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        BlockPos tipPos = getHeadPos(worldIn, pos, state);
        if (tipPos != null)
        {
            BlockState tip = worldIn.getBlockState(tipPos);
            return getHeadBlock().isValidBonemealTarget(worldIn, tipPos, tip, isClient);
        }
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel worldIn, Random rand, BlockPos pos, BlockState state)
    {
        BlockPos headPos = getHeadPos(worldIn, pos, state);
        if (headPos != null)
        {
            BlockState bstate = worldIn.getBlockState(headPos);
            ((GrowingPlantHeadBlock) bstate.getBlock()).performBonemeal(worldIn, rand, headPos, bstate);
        }
    }

    @Nullable
    private BlockPos getHeadPos(BlockGetter reader, BlockPos pos, BlockState state)
    {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        BlockState bstate = state;
        int j = getHeadBlock().getMaxGrowthHeight();
        for (int i = 0; i < j && bstate.is(state.getBlock()); i++)
        {
            if ((bstate = reader.getBlockState(mutable.move(growthDirection))).is(getHeadBlock()))
                return mutable.immutable();
        }

        return null;
    }

    @Override
    public Item asItem()
    {
        return getHeadBlock().asItem();
    }

    @Override
    protected GrowingPlantBlock getHeadBlock()
    {
        return (GrowingPlantBlock) tip.get();
    }
}
