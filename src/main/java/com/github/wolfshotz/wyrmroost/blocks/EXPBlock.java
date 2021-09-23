package com.github.wolfshotz.wyrmroost.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.util.Mth;

import java.util.Random;

public class EXPBlock extends OreBlock
{
    private final int minXp, maxXp;

    public EXPBlock(int minXp, int maxXp, Block.Properties properties)
    {
        super(properties);
        this.minXp = minXp;
        this.maxXp = maxXp;
    }

    @Override
    protected int xpOnDrop(Random random)
    {
        return Mth.nextInt(random, minXp, maxXp);
    }
}