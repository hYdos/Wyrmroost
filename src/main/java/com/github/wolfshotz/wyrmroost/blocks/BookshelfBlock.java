package com.github.wolfshotz.wyrmroost.blocks;

import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class BookshelfBlock extends Block
{
    public BookshelfBlock()
    {
        super(WRBlocks.properties(Material.WOOD, SoundType.WOOD).strength(1.5f));
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos)
    {
        return 1;
    }
}
