package com.github.wolfshotz.wyrmroost.blocks.tile;

import com.github.wolfshotz.wyrmroost.registry.WRBlockEntities;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class WRSignBlockEntity extends SignBlockEntity
{
    @Override
    public BlockEntityType<?> getType()
    {
        return WRBlockEntities.CUSTOM_SIGN.get();
    }
}
