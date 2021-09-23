package com.github.wolfshotz.wyrmroost.entities.projectile;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.registry.WREffects;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class SoulCrystalEntity extends ThrowableItemProjectile
{
    public static final String DATA_DRAGON = "DragonData";
    public static final byte BREAK_EVENT = 1;

    public SoulCrystalEntity(EntityType<? extends ThrowableItemProjectile> type, Level world)
    {
        super(type, world);
    }

    public SoulCrystalEntity(ItemStack stack, LivingEntity thrower, Level world)
    {
        super(WREntities.SOUL_CRYSTAL.get(), thrower, world);
        setItem(stack);
    }

    @Override
    protected Item getDefaultItem()
    {
        return WRItems.SOUL_CRYSTAL.get();
    }

    @Override
    protected void onHit(HitResult result)
    {
        remove();
        Entity thrower = getOwner();
        ItemStack stack = getItem();

        if (!(thrower instanceof Player) || !releaseDragon(level, (Player) thrower, stack, new BlockPos(result.getLocation()), thrower.getDirection()).consumesAction())
            super.onHit(result);

        if (stack.getDamageValue() >= stack.getMaxDamage()) level.broadcastEntityEvent(this, BREAK_EVENT);
        else restoreStack(level, (LivingEntity) thrower, result.getLocation(), stack);
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        super.onHitEntity(result);
        Entity thrower = getOwner();
        ItemStack stack = getItem();
        if (thrower instanceof Player) captureDragon((Player) thrower, level, stack, result.getEntity());
    }

    @Override
    public void handleEntityEvent(byte event)
    {

        if (event == BREAK_EVENT)
        {
            ModUtils.playLocalSound(level, blockPosition(), SoundEvents.GLASS_BREAK, 1f, 1.75f);
            ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, getItem());
            for (int i = 0; i < 8; ++i)
                level.addParticle(data, getX(), getY(), getZ(), Mafs.nextDouble(random) * 0.25, random.nextDouble() * 0.15, Mafs.nextDouble(random) * 0.25);
        }
        else super.handleEntityEvent(event);
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static boolean containsDragon(ItemStack stack)
    {
        return stack.hasTag() && stack.getTag().contains(DATA_DRAGON);
    }

    public static boolean isSuitableEntity(Entity entity)
    {
        return entity instanceof TamableAnimal && entity.getType().is(WREntities.Tags.SOUL_BEARERS);
    }

    public static InteractionResult captureDragon(@Nullable Player player, Level level, ItemStack stack, Entity target)
    {
        if (containsDragon(stack)) return InteractionResult.PASS;
        if (!isSuitableEntity(target)) return fail(player, "not_suitable");
        TamableAnimal dragon = (TamableAnimal) target;
        if (dragon.getOwner() != player) return fail(player, "not_owner");
        if (level.isClientSide) return InteractionResult.CONSUME;
        if (dragon.hasEffect(WREffects.SOUL_WEAKNESS.get())) return fail(player, "weak");

        if (!dragon.getPassengers().isEmpty()) dragon.ejectPassengers();
        if (dragon instanceof TameableDragonEntity) ((TameableDragonEntity) dragon).dropStorage();

        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag dragonTag = dragon.serializeNBT();
        if (player != null) dragonTag.putString("OwnerName", player.getName().getString());
        tag.put(DATA_DRAGON, dragonTag); // Serializing the dragons data, including its id.
        stack.setTag(tag);
        dragon.restrictTo(BlockPos.ZERO, -1);
        dragon.remove();
        level.playSound(null, dragon.blockPosition(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.AMBIENT, 1, 1);
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult releaseDragon(Level level, Player player, ItemStack stack, BlockPos pos, Direction direction)
    {
        if (!containsDragon(stack)) return InteractionResult.PASS;

        CompoundTag tag = stack.getTag().getCompound(DATA_DRAGON);
        EntityType<?> type = EntityType.byString(tag.getString("id")).orElse(null);
        TamableAnimal dragon;

        // just in case...
        if (type == null || (dragon = (TamableAnimal) type.create(level)) == null)
        {
            Wyrmroost.LOG.error("Something went wrong summoning from a SoulCrystal!");
            return InteractionResult.FAIL;
        }

        // Ensuring the owner is the one summoning
        if (!tag.getUUID("Owner").equals(player.getUUID())) return fail(player, "not_owner");

        EntityDimensions size = dragon.getDimensions(dragon.getPose());
        if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty())
            pos = pos.relative(direction);

        // check area for collision to ensure the area is safe.
        dragon.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        AABB aabb = dragon.getBoundingBox();
        if (!level.noCollision(dragon, new AABB(aabb.minX, dragon.getEyeY() - 0.35, aabb.minZ, aabb.maxX, dragon.getEyeY() + 0.35, aabb.maxZ)))
            return fail(player, "fail");

        // Spawn the entity on the server side only
        if (!level.isClientSide)
        {
            // no conflicting id's!
            UUID id = dragon.getUUID();
            dragon.deserializeNBT(tag);
            dragon.setUUID(id);
            dragon.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.yRot, 0f);

            if (stack.hasCustomHoverName()) dragon.setCustomName(stack.getHoverName());
            stack.removeTagKey(DATA_DRAGON);
            level.addFreshEntity(dragon);
            level.playSound(null, dragon.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.AMBIENT, 1, 1);

            float thiccness = dragon.getBbWidth() + dragon.getBbWidth();
            dragon.addEffect(new MobEffectInstance(WREffects.SOUL_WEAKNESS.get(), (int) thiccness * 200));
            if (!player.abilities.instabuild && thiccness > 5) stack.hurt((int) (thiccness * 0.675f), level.random, (ServerPlayer) player);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static void restoreStack(Level level, @Nullable LivingEntity player, Vec3 origin, ItemStack stack)
    {
        if (player == null)
            Containers.dropItemStack(level, origin.x(), origin.y(), origin.z(), stack);
        else
        {
            ItemEntity entity = new ItemEntity(level, origin.x(), origin.y(), origin.z(), stack);
            double x = player.getX() - origin.x();
            double y = player.getY() - origin.y() + 0.675;
            double z = player.getZ() - origin.z();
            entity.setDeltaMovement(x * 0.1D, y * 0.1 + Math.sqrt(Math.sqrt(x * x + y * y + z * z)) * 0.08, z * 0.1);
            level.addFreshEntity(entity);
        }
    }

    private static InteractionResult fail(Player player, String message)
    {
        player.displayClientMessage(new TranslatableComponent("item.wyrmroost.soul_crystal." + message).withStyle(ChatFormatting.RED), true);
        return InteractionResult.FAIL;
    }
}
