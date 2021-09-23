package com.github.wolfshotz.wyrmroost.blocks;

import net.minecraft.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class EmberBlock extends Block
{
    public EmberBlock()
    {
        super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.NETHER)
                .requiresCorrectToolForDrops()
                .lightLevel(s -> 3)
                .randomTicks()
                .strength(0.5f)
                .isValidSpawn((state, level, pos, type) -> type.fireImmune())
                .hasPostProcess((s, l, p) -> true)
                .emissiveRendering((s, l, p) -> true)
                .sound(SoundType.SAND));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, Entity entity)
    {
        if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity))
            entity.hurt(DamageSource.HOT_FLOOR, 1.0F);

        super.stepOn(level, pos, entity);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rng)
    {
        BlockPos up = pos.above();
        if (level.getFluidState(pos).is(FluidTags.WATER))
        {
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
            level.sendParticles(ParticleTypes.LARGE_SMOKE, (double) up.getX() + 0.5D, (double) up.getY() + 0.25D, (double) up.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
        }
        BlockState block = level.getBlockState(pos);
        if (block.getBlock() instanceof FallingBlock)
        {
            level.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, block), up.getX() + 0.5, up.getY(), up.getZ() + 0.5, 8, 0, 0.25, 0, 0);
        }
    }
}
