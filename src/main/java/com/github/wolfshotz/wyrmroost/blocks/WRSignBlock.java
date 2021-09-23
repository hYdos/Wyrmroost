package com.github.wolfshotz.wyrmroost.blocks;

import com.github.wolfshotz.wyrmroost.blocks.tile.WRSignBlockEntity;
import com.github.wolfshotz.wyrmroost.registry.WRBlockEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WRSignBlock extends StandingSignBlock implements WRBlockEntities.Validator
{
    public WRSignBlock(Properties properties, WoodType type)
    {
        super(properties, type);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter level)
    {
        return new WRSignBlockEntity();
    }

    @Override
    public boolean isValidEntity(BlockEntityType<?> type)
    {
        return type == WRBlockEntities.CUSTOM_SIGN.get();
    }

    public static class Wall extends WallSignBlock implements WRBlockEntities.Validator
    {
        public Wall(Properties properties, WoodType type)
        {
            super(properties, type);
        }

        @Override
        public boolean hasTileEntity(BlockState state)
        {
            return true;
        }

        @Override
        public BlockEntity newBlockEntity(BlockGetter level)
        {
            return new WRSignBlockEntity();
        }

        @Override
        public boolean isValidEntity(BlockEntityType<?> type)
        {
            return type == WRBlockEntities.CUSTOM_SIGN.get();
        }
    }
}
