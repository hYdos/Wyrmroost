package com.github.wolfshotz.wyrmroost.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ThinLogBlock extends LogBlock implements SimpleWaterloggedBlock
{
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape[] SHAPES = {
            Block.box(0, 4, 4, 16, 12, 12), // x
            Block.box(4, 0, 4, 12, 16, 12), // y
            Block.box(4, 4, 0, 12, 12, 16)  // z
    };

    public ThinLogBlock(Properties props, Supplier<Block> stripped)
    {
        super(props, stripped);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    public ThinLogBlock(MaterialColor top, MaterialColor bark, Supplier<Block> stripped)
    {
        this(properties(top, bark), stripped);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return SHAPES[state.getValue(LogBlock.AXIS).ordinal()];
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return state.getValue(WATERLOGGED)? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return super.getStateForPlacement(ctx).setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    public static WoodGroup.Builder thinLogGroup(MaterialColor color, MaterialColor logColor)
    {
        return WoodGroup.builder(color, logColor)
                .log(stripped -> new ThinLogBlock(color, logColor, stripped))
                .strippedLog(() -> new ThinLogBlock(color, color, null))
                .wood(stripped -> new ThinLogBlock(logColor, logColor, stripped))
                .strippedWood(() -> new ThinLogBlock(color, color, null));
    }

    public static void consumeThinLogs(Consumer<Block> con, WoodGroup... groups)
    {
        for (WoodGroup group : groups)
        {
            con.accept(group.getLog());
            con.accept(group.getStrippedLog());
            con.accept(group.getWood());
            con.accept(group.getStrippedWood());
        }
    }
}
