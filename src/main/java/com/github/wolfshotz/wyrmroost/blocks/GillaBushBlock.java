package com.github.wolfshotz.wyrmroost.blocks;

import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IForgeShearable;

public class GillaBushBlock extends BushBlock implements IForgeShearable
{
    static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);

    public GillaBushBlock()
    {
        super(WRBlocks.replaceablePlant());
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_)
    {
        return SHAPE;
    }

    @Override
    public BlockBehaviour.OffsetType getOffsetType()
    {
        return BlockBehaviour.OffsetType.XZ;
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entity)
    {
        if (entity instanceof LivingEntity)
        {
            entity.makeStuckInBlock(state, new Vec3(0.5d, 0.6d, 0.5d));
            if (!worldIn.isClientSide && (entity.xOld != entity.getX() || entity.zOld != entity.getZ()))
            {
                if (Math.abs(entity.getX() - entity.xOld) >= 0.003 || Math.abs(entity.getZ() - entity.zOld) >= 0.003)
                    entity.hurt(DamageSource.SWEET_BERRY_BUSH, 1f);
            }
        }
    }
}
