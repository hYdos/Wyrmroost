package com.github.wolfshotz.wyrmroost.blocks;

import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import net.minecraft.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StoneButtonBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StoneGroup
{
    public final RegistryObject<Block> stone;
    public final RegistryObject<Block> stairs;
    public final RegistryObject<Block> wall;
    public final RegistryObject<Block> slab;
    public final RegistryObject<Block> pressurePlate;
    public final RegistryObject<Block> button;
    public final RegistryObject<Block> cracked;
    public final RegistryObject<Block> chiseled;

    private StoneGroup(String name, Builder builder)
    {
        this.stone = registerInfested(name, builder.stone, builder.infested);
        this.stairs = builder.stairs == null? null : register(name + "_stairs", () -> builder.stairs.apply(getStone()));
        this.wall = register(name + "_wall", builder.wall);
        this.slab = register(name + "_slab", builder.slab);
        this.pressurePlate = builder.pressurePlate == null? null : register(name + "_pressure_plate", () -> builder.pressurePlate.apply(BlockBehaviour.Properties.copy(getStone())));
        this.button = register(name + "_button", builder.button);
        this.cracked = registerInfested("cracked_" + name, builder.cracked, builder.infested);
        this.chiseled = registerInfested("chiseled_" + name, builder.chiseled, builder.infested);
    }

    public Block getStone()
    {
        return stone.get();
    }

    public Block getStairs()
    {
        return stairs.get();
    }

    public Block getWall()
    {
        return wall.get();
    }

    public Block getSlab()
    {
        return slab.get();
    }

    public Block getPressurePlate()
    {
        return pressurePlate.get();
    }

    public Block getButton()
    {
        return button.get();
    }

    public Block getCracked()
    {
        return cracked.get();
    }

    public Block getChiseled()
    {
        return chiseled.get();
    }

    @Nullable
    private static RegistryObject<Block> register(String name, @Nullable Supplier<Block> sup)
    {
        return sup == null? null : WRBlocks.register(name, sup);
    }

    @Nullable
    private static RegistryObject<Block> registerInfested(String name, @Nullable Supplier<Block> sup, boolean infested)
    {
        if (sup == null) return null;
        else
        {
            RegistryObject<Block> callback = WRBlocks.register(name, sup);
            if (infested) WRBlocks.register("infested_" + name, () -> new InfestedBlock(callback.get(), BlockBehaviour.Properties.copy(callback.get())));
            return callback;
        }
    }

    public static Builder builder(BlockBehaviour.Properties props)
    {
        return new Builder(props
                .requiresCorrectToolForDrops()
                .harvestTool(ToolType.PICKAXE));
    }

    public static Builder base(BlockBehaviour.Properties props)
    {
        return builder(props)
                .stairs()
                .wall()
                .slab();
    }

    public static class Builder
    {
        private final BlockBehaviour.Properties props;
        private boolean infested = false;
        private final Supplier<Block> stone;
        private Function<Block, Block> stairs;
        private Supplier<Block> wall;
        private Supplier<Block> slab;
        private Function<BlockBehaviour.Properties, Block> pressurePlate;
        private Supplier<Block> button;
        private Supplier<Block> cracked;
        private Supplier<Block> chiseled;

        public Builder(BlockBehaviour.Properties props)
        {
            this.props = props;
            this.stone = () -> new Block(props);
        }

        public Builder stairs()
        {
            this.stairs = stone -> new StairBlock(stone::defaultBlockState, props);
            return this;
        }

        public Builder wall()
        {
            this.wall = () -> new WallBlock(props);
            return this;
        }

        public Builder slab()
        {
            this.slab = () -> new SlabBlock(props);
            return this;
        }

        public Builder pressurePlateAndButton(PressurePlateBlock.Sensitivity sens)
        {
            this.pressurePlate = props -> new PressurePlateBlock(sens, props.noCollission().strength(0.5f));
            this.button = () -> new StoneButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5f));
            return this;
        }

        public Builder cracked()
        {
            this.cracked = () -> new Block(props);
            return this;
        }

        public Builder chiseled()
        {
            this.chiseled = () -> new Block(props);
            return this;
        }

        public Builder infested()
        {
            this.infested = true;
            return this;
        }

        public StoneGroup build(String name)
        {
            return new StoneGroup(name, this);
        }
    }
}
