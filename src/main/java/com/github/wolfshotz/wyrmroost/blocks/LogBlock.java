package com.github.wolfshotz.wyrmroost.blocks;

import net.minecraft.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;

public class LogBlock extends RotatedPillarBlock
{
    private final Supplier<Block> stripped;

    public LogBlock(Properties props, Supplier<Block> stripped)
    {
        super(props);
        this.stripped = stripped;
    }

    public LogBlock(MaterialColor top, MaterialColor bark, Supplier<Block> stripped)
    {
        this(properties(top, bark), stripped);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, Level level, BlockPos pos, Player player, ItemStack stack, ToolType toolType)
    {
        return toolType == ToolType.AXE && stripped != null?
                stripped.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)) :
                super.getToolModifiedState(state, level, pos, player, stack, toolType);
    }

    public static BlockBehaviour.Properties properties(MaterialColor top, MaterialColor bark)
    {
        return BlockBehaviour.Properties
                .of(Material.WOOD, state -> (top == bark || state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y)? top : bark)
                .strength(2f)
                .harvestTool(ToolType.AXE)
                .sound(SoundType.WOOD);
    }
}
