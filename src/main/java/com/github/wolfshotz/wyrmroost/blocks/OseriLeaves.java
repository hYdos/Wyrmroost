package com.github.wolfshotz.wyrmroost.blocks;

import com.github.wolfshotz.wyrmroost.client.particle.PetalParticle;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class OseriLeaves extends LeavesBlock
{
    private final int particleColor;

    public OseriLeaves(int particleColor, Properties properties)
    {
        super(properties);
        this.particleColor = particleColor;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
    {
        super.animateTick(state, level, pos, random);
        if (random.nextDouble() < 0.05) PetalParticle.play(level, pos, random, particleColor);
    }

}
