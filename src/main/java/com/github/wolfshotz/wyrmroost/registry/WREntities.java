package com.github.wolfshotz.wyrmroost.registry;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.model.WREntityModel;
import com.github.wolfshotz.wyrmroost.client.model.entity.*;
import com.github.wolfshotz.wyrmroost.client.render.ModelWrappedRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.DragonEggRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.EmptyRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.projectile.BreathWeaponRenderer;
import com.github.wolfshotz.wyrmroost.client.render.entity.projectile.GeodeTippedArrowRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.*;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggEntity;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.wolfshotz.wyrmroost.entities.projectile.GeodeTippedArrowEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.SoulCrystalEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.WindGustEntity;
import com.github.wolfshotz.wyrmroost.entities.projectile.breath.FireBreathEntity;
import com.github.wolfshotz.wyrmroost.items.LazySpawnEggItem;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS;
import static net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType.ON_GROUND;

@SuppressWarnings("unchecked")
public class WREntities<E extends Entity> extends EntityType<E> {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, Wyrmroost.MOD_ID);

    public static final RegistryObject<EntityType<LesserDesertwyrmEntity>> LESSER_DESERTWYRM = creature("lesser_desertwyrm", LesserDesertwyrmEntity::new)
            .size(0.6f, 0.2f)
            .attributes(LesserDesertwyrmEntity::getAttributeMap)
            .spawnPlacement(ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, LesserDesertwyrmEntity::getSpawnPlacement)
            .spawnEgg(0xD6BCBC, 0xDEB6C7)
            .renderModel(() -> LesserDesertwyrmModel::new)
            .packetInterval(5)
            .build();

    public static final RegistryObject<EntityType<OverworldDrakeEntity>> OVERWORLD_DRAKE = creature("overworld_drake", OverworldDrakeEntity::new)
            .size(2.376f, 2.58f)
            .attributes(OverworldDrakeEntity::getAttributeMap)
            .spawnPlacement()
            .spawnEgg(0x788716, 0x3E623E)
            .dragonEgg(new DragonEggProperties(0.35f, 0.6f, 18000))
            .renderModel(() -> OverworldDrakeModel::new)
            .trackingRange(10)
            .build();

    public static final RegistryObject<EntityType<SilverGliderEntity>> SILVER_GLIDER = creature("silver_glider", SilverGliderEntity::new)
            .size(1.5f, 0.75f)
            .attributes(SilverGliderEntity::getAttributeMap)
            .spawnPlacement(NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SilverGliderEntity::getSpawnPlacement)
            .spawnEgg(0xC8C8C8, 0xC4C4C4)
            .dragonEgg(new DragonEggProperties(0.2f, 0.35f, 12000))
            .renderModel(() -> SilverGliderModel::new)
            .trackingRange(8)
            .build();

    public static final RegistryObject<EntityType<RoostStalkerEntity>> ROOSTSTALKER = creature("roost_stalker", RoostStalkerEntity::new)
            .size(0.65f, 0.5f)
            .attributes(RoostStalkerEntity::getAttributeMap)
            .spawnPlacement()
            .spawnEgg(0x52100D, 0x959595)
            .dragonEgg(new DragonEggProperties(0.175f, 0.3f, 6000))
            .renderModel(() -> RoostStalkerModel::new)
            .build();

    public static final RegistryObject<EntityType<ButterflyLeviathanEntity>> BUTTERFLY_LEVIATHAN = ofGroup("butterfly_leviathan", ButterflyLeviathanEntity::new, EntityClassification.WATER_CREATURE)
            .size(4f, 3f)
            .attributes(ButterflyLeviathanEntity::getAttributeMap)
            .spawnPlacement(NO_RESTRICTIONS, Heightmap.Type.OCEAN_FLOOR_WG, ButterflyLeviathanEntity::getSpawnPlacement)
            .spawnEgg(0x17283C, 0x7A6F5A)
            .dragonEgg(new DragonEggProperties(0.5f, 0.8f, 40000).setConditions(Entity::isInWater))
            .renderModel(() -> ButterflyLeviathanModel::new)
            .trackingRange(8)
            .build();

    public static final RegistryObject<EntityType<DragonFruitDrakeEntity>> DRAGON_FRUIT_DRAKE = creature("dragon_fruit_drake", DragonFruitDrakeEntity::new)
            .size(1.5f, 1.9f)
            .attributes(DragonFruitDrakeEntity::getAttributeMap)
            .spawnPlacement(NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DragonFruitDrakeEntity::getSpawnPlacement)
            .spawnEgg(0xe05c9a, 0x788716)
            .dragonEgg(new DragonEggProperties(0.25f, 0.35f, 9600))
            .renderModel(() -> DragonFruitDrakeModel::new)
            .build();

    public static final RegistryObject<EntityType<CanariWyvernEntity>> CANARI_WYVERN = creature("canari_wyvern", CanariWyvernEntity::new)
            .size(0.65f, 0.85f)
            .attributes(CanariWyvernEntity::getAttributeMap)
            .spawnPlacement(NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, TameableDragonEntity::canFlyerSpawn)
            .spawnEgg(0x1D1F28, 0x492E0E)
            .dragonEgg(new DragonEggProperties(0.175f, 0.275f, 6000).setConditions(c -> c.level.getBlockState(c.blockPosition().below()).getBlock() == Blocks.JUNGLE_LEAVES))
            .renderModel(() -> CanariWyvernModel::new)
            .build();

    public static final RegistryObject<EntityType<RoyalRedEntity>> ROYAL_RED = creature("royal_red", RoyalRedEntity::new)
            .size(3f, 3.9f)
            .attributes(RoyalRedEntity::getAttributeMap)
            .spawnPlacement(NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, TameableDragonEntity::canFlyerSpawn)
            .spawnEgg(0x8a0900, 0x0)
            .dragonEgg(new DragonEggProperties(0.45f, 0.7f, 72000))
            .renderModel(() -> RoyalRedModel::new)
            .fireImmune()
            .trackingRange(11)
            .build();

    public static final RegistryObject<EntityType<CoinDragonEntity>> COIN_DRAGON = creature("coin_dragon", CoinDragonEntity::new)
            .size(0.35f, 0.435f)
            .renderModel(() -> CoinDragonModel::new)
            .attributes(CoinDragonEntity::getAttributeMap)
            .trackingRange(2)
            .build();

    public static final RegistryObject<EntityType<AlpineEntity>> ALPINE = creature("alpine", AlpineEntity::new)
            .size(2f, 2f)
            .attributes(AlpineEntity::getAttributeMap)
            .spawnPlacement(NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, TameableDragonEntity::canFlyerSpawn)
            .spawnEgg(0xe3f8ff, 0xa8e9ff)
            .dragonEgg(new DragonEggProperties(0.35f, 0.55f, 12000))
            .renderModel(() -> AlpineModel::new)
            .trackingRange(9)
            .build();

    public static final RegistryObject<EntityType<DragonEggEntity>> DRAGON_EGG = WREntities.<DragonEggEntity>ofGroup("dragon_egg", DragonEggEntity::new, EntityClassification.MISC)
            .renderer(() -> DragonEggRenderer::new)
            .noSummon()
            .clientFactory(DragonEggEntity::new)
            .trackingRange(4)
            .packetInterval(5)
            .build();

    public static final RegistryObject<EntityType<GeodeTippedArrowEntity>> GEODE_TIPPED_ARROW = WREntities.<GeodeTippedArrowEntity>ofGroup("geode_tipped_arrow", GeodeTippedArrowEntity::new, EntityClassification.MISC)
            .size(0.5f, 0.5f)
            .renderer(() -> GeodeTippedArrowRenderer::new)
            .clientFactory(GeodeTippedArrowEntity::new)
            .trackingRange(4)
            .packetInterval(10)
            .build();

    public static final RegistryObject<EntityType<FireBreathEntity>> FIRE_BREATH = WREntities.<FireBreathEntity>ofGroup("fire_breath", FireBreathEntity::new, EntityClassification.MISC)
            .size(0.75f, 0.75f)
            .renderer(() -> BreathWeaponRenderer::new)
            .noSave()
            .noSummon()
            .packetInterval(10)
            .build();

    public static final RegistryObject<EntityType<WindGustEntity>> WIND_GUST = WREntities.<WindGustEntity>ofGroup("wind_gust", WindGustEntity::new, EntityClassification.MISC)
            .size(4, 4)
            .renderer(() -> EmptyRenderer::new)
            .noSave()
            .noSummon()
            .packetInterval(10)
            .build();

    public static final RegistryObject<EntityType<SoulCrystalEntity>> SOUL_CRYSTAL = WREntities.<SoulCrystalEntity>ofGroup("soul_crystal", SoulCrystalEntity::new, EntityClassification.MISC)
            .size(0.25f, 0.25f)
            .renderer(() -> ClientEvents::spriteRenderer)
            .trackingRange(4)
            .packetInterval(10)
            .build();

    @Nonnull
    public final Supplier<IRenderFactory<E>> renderer;
    @Nullable
    public final AttributeModifierMap.MutableAttribute attributes;
    @Nullable
    public final SpawnPlacementEntry<E> spawnPlacement;
    @Nullable
    public final DragonEggProperties eggProperties;

    public WREntities(EntityType.IFactory<E> factory, EntityClassification group, boolean serialize, boolean summon, boolean fireImmune, boolean spawnsFarFromPlayer, ImmutableSet<Block> immuneTo, EntitySize size, int trackingRange, int tickRate, Predicate<EntityType<?>> velocityUpdateSupplier, ToIntFunction<EntityType<?>> trackingRangeSupplier, ToIntFunction<EntityType<?>> updateIntervalSupplier, BiFunction<FMLPlayMessages.SpawnEntity, World, E> customClientFactory, Supplier<IRenderFactory<E>> renderFactory, AttributeModifierMap.MutableAttribute attributes, SpawnPlacementEntry<E> spawnPlacement, DragonEggProperties props) {
        super(factory, group, serialize, summon, fireImmune, spawnsFarFromPlayer, immuneTo, size, trackingRange, tickRate, velocityUpdateSupplier, trackingRangeSupplier, updateIntervalSupplier, customClientFactory);
        this.renderer = renderFactory;
        this.attributes = attributes;
        this.spawnPlacement = spawnPlacement;
        this.eggProperties = props;
    }

    @SuppressWarnings("unchecked")
    public void callBack() {
        if (FMLEnvironment.dist == Dist.CLIENT) RenderingRegistry.registerEntityRenderingHandler(this, renderer.get());
        if (spawnPlacement != null) {
            SpawnPlacementEntry<MobEntity> entry = (SpawnPlacementEntry<MobEntity>) spawnPlacement;
            EntitySpawnPlacementRegistry.register((WREntities<MobEntity>) this, entry.placement, entry.heightMap, entry.predicate);
        }
    }

    private static <T extends Entity> Builder<T> creature(String name, EntityType.IFactory<T> factory) {
        return new Builder<>(name, factory, EntityClassification.CREATURE);
    }

    private static <T extends Entity> Builder<T> ofGroup(String name, EntityType.IFactory<T> factory, EntityClassification group) {
        return new Builder<>(name, factory, group);
    }

    public static class Attributes {
        public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Wyrmroost.MOD_ID);

        public static final RegistryObject<Attribute> PROJECTILE_DAMAGE = ranged("generic.projectileDamage", 2d, 0, 2048d);

        private static RegistryObject<Attribute> ranged(String name, double defaultValue, double min, double max) {
            return register(name.toLowerCase().replace('.', '_'), () -> new RangedAttribute("attribute.name." + name, defaultValue, min, max));
        }

        private static RegistryObject<Attribute> register(String name, Supplier<Attribute> attribute) {
            return REGISTRY.register(name, attribute);
        }
    }

    public static class Tags {
        public static final ITag.INamedTag<EntityType<?>> SOUL_BEARERS = bind("soul_bearers");

        private static ITag.INamedTag<EntityType<?>> bind(String name) {
            return EntityTypeTags.bind(Wyrmroost.MOD_ID + ":" + name);
        }
    }

    private static class Builder<T extends Entity> {
        private final String name;
        private final EntityType.IFactory<T> factory;
        private final EntityClassification category;
        private ImmutableSet<Block> immuneTo = ImmutableSet.of();
        private boolean serialize = true;
        private boolean summon = true;
        private boolean fireImmune;
        private boolean canSpawnFarFromPlayer;
        private int trackingRange = 5;
        private int packetInterval = 3;
        private boolean updatesVelocity = true;
        private EntitySize size = EntitySize.scalable(0.6f, 1.8f);
        private DragonEggProperties dragonEggProperties;
        private Supplier<IRenderFactory<T>> renderer;
        private Supplier<AttributeModifierMap.MutableAttribute> attributes = () -> null;
        private SpawnPlacementEntry<T> spawnPlacement;
        private BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory;

        private RegistryObject<EntityType<T>> registered;

        private Builder(String name, EntityType.IFactory<T> factory, EntityClassification group) {
            this.name = name;
            this.factory = factory;
            this.category = group;
            this.canSpawnFarFromPlayer = group == EntityClassification.CREATURE || group == EntityClassification.MISC;
        }

        private Builder<T> size(float width, float height) {
            this.size = EntitySize.scalable(width, height);
            return this;
        }

        private Builder<T> noSummon() {
            this.summon = false;
            return this;
        }

        private Builder<T> noSave() {
            this.serialize = false;
            return this;
        }

        private Builder<T> fireImmune() {
            this.fireImmune = true;
            return this;
        }

        private Builder<T> immuneTo(Block... blocks) {
            this.immuneTo = ImmutableSet.copyOf(blocks);
            return this;
        }

        private Builder<T> canSpawnFarFromPlayer() {
            this.canSpawnFarFromPlayer = true;
            return this;
        }

        private Builder<T> trackingRange(int trackingRange) {
            this.trackingRange = trackingRange;
            return this;
        }

        private Builder<T> packetInterval(int rate) {
            this.packetInterval = rate;
            return this;
        }

        private Builder<T> noVelocityUpdates() {
            this.updatesVelocity = false;
            return this;
        }

        private Builder<T> clientFactory(BiFunction<FMLPlayMessages.SpawnEntity, World, T> clientFactory) {
            this.customClientFactory = clientFactory;
            return this;
        }

        private Builder<T> spawnEgg(int primColor, int secColor) {
            WRItems.register(name + "_spawn_egg", () -> new LazySpawnEggItem<>(registered, primColor, secColor));
            return this;
        }

        private Builder<T> renderer(Supplier<IRenderFactory<T>> renderFactory) {
            this.renderer = renderFactory;
            return this;
        }

        private Builder<T> renderModel(Supplier<Supplier<WREntityModel<T>>> model) {
            this.renderer = () -> ModelWrappedRenderer.factory(model);
            return this;
        }

        // needs to be supplier to accomadate for attributes deferred registered
        private Builder<T> attributes(Supplier<AttributeModifierMap.MutableAttribute> map) {
            this.attributes = map;
            return this;
        }

        private <F extends MobEntity> Builder<T> spawnPlacement(EntitySpawnPlacementRegistry.PlacementType type, Heightmap.Type height, EntitySpawnPlacementRegistry.IPlacementPredicate<F> predicate) {
            this.spawnPlacement = new SpawnPlacementEntry<>(height, type, ((EntitySpawnPlacementRegistry.IPlacementPredicate<T>) predicate));
            return this;
        }

        private Builder<T> spawnPlacement() {
            return spawnPlacement(ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkMobSpawnRules);
        }

        private Builder<T> dragonEgg(DragonEggProperties props) {
            this.dragonEggProperties = props;
            return this;
        }

        private RegistryObject<EntityType<T>> build() {
            return registered = REGISTRY.register(name, () -> new WREntities<>(factory, category, serialize, summon, fireImmune, canSpawnFarFromPlayer, immuneTo, size, trackingRange, packetInterval, t -> updatesVelocity, t -> trackingRange, t -> packetInterval, customClientFactory, renderer, attributes.get(), spawnPlacement, dragonEggProperties));
        }
    }

    private static class SpawnPlacementEntry<E extends Entity> {
        final Heightmap.Type heightMap;
        final EntitySpawnPlacementRegistry.PlacementType placement;
        final EntitySpawnPlacementRegistry.IPlacementPredicate<E> predicate;

        SpawnPlacementEntry(Heightmap.Type heightMap, EntitySpawnPlacementRegistry.PlacementType placement, EntitySpawnPlacementRegistry.IPlacementPredicate<E> predicate) {
            this.heightMap = heightMap;
            this.placement = placement;
            this.predicate = predicate;
        }
    }
}
