package com.github.wolfshotz.wyrmroost.blocks;

import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IForgeShearable;

public class CrevasseCottonBlock extends BushBlock implements IForgeShearable
{
    static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);

    public CrevasseCottonBlock()
    {
        super(WRBlocks.replaceablePlant());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return SHAPE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.isClientSide)
        {
            double x = entity.getX() - entity.xOld;
            double y = entity.getY() - entity.yOld;
            double z = entity.getZ() - entity.zOld;
            double sq = x * x + y * y + z * z;
            if (RANDOM.nextDouble() < sq * 5)
            {
                x += Mafs.nextDouble(level.random) * 0.15;
                z += Mafs.nextDouble(level.random) * 0.15;
                level.addParticle(ParticleTypes.END_ROD, entity.getRandomX(0.3), entity.getY() + 0.5, entity.getRandomZ(0.3), x * 0.35, y * 0.05, z * 0.35);
            }
        }
    }
}
