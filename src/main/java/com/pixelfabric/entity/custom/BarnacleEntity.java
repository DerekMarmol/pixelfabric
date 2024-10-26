package com.pixelfabric.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ArmorItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;
import java.util.ArrayList;
import java.util.List;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BarnacleEntity extends WaterCreatureEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.barnacle.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.barnacle.walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("animation.barnacle.attack");

    private float bonusDamage = 0.0F;
    private float bonusArmor = 0.0F;
    private int healingPower = 0;
    private boolean isAttacking = false;
    private int itemsDevoured = 0;
    private static final int MAX_ITEMS = 10;

    // Lista para almacenar los items importantes devorados
    private List<ItemStack> devouredItems = new ArrayList<>();

    public BarnacleEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.0f);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    public static DefaultAttributeContainer.Builder createDevourerAttributes() {
        return WaterCreatureEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.4D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.4D, true));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 12.0F));
        this.goalSelector.add(3, new SwimAroundGoal(this, 1.2D, 10));
        this.goalSelector.add(4, new WanderAroundGoal(this, 1.0D));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean tryAttack(Entity target) {
        this.isAttacking = true;
        if (target instanceof PlayerEntity player) {
            float devourChance = 0.4f + (itemsDevoured * 0.05f);
            if (random.nextFloat() < devourChance && itemsDevoured < MAX_ITEMS) {
                // Pequeña probabilidad de intentar devorar armadura
                if (random.nextFloat() < 0.15f) {
                    devourRandomArmor(player);
                } else {
                    devourRandomItem(player);
                }
            }

            // Aplica el daño base más el bonus
            float totalDamage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + bonusDamage;
            player.damage(this.getDamageSources().mobAttack(this), totalDamage);
        }

        this.isAttacking = false;
        return true;
    }

    private void devourRandomArmor(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        List<Integer> armorSlots = new ArrayList<>();

        // Buscar slots con armadura equipada
        for (int i = 0; i < 4; i++) {
            if (!inventory.getArmorStack(i).isEmpty()) {
                armorSlots.add(i);
            }
        }

        if (!armorSlots.isEmpty()) {
            int randomArmorSlot = armorSlots.get(random.nextInt(armorSlots.size()));
            ItemStack armorStack = inventory.getArmorStack(randomArmorSlot);

            if (!armorStack.isEmpty()) {
                ItemStack armorCopy = armorStack.copy();
                inventory.setStack(randomArmorSlot + 36, ItemStack.EMPTY); // +36 para acceder a los slots de armadura

                // Guardar el item para posible drop al morir
                devouredItems.add(armorCopy);

                // Aumentar resistencia basado en el material de la armadura
                float armorBonus = getArmorBonus(armorStack);
                bonusArmor += armorBonus;
                this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(4.0D + bonusArmor);

                // Efectos visuales y mensaje
                spawnItemParticles();
                playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 0.5F);
                player.sendMessage(Text.literal("§4¡El Devorador ha consumido tu " + armorCopy.getName().getString() + " y aumentado su resistencia!"), true);

                itemsDevoured++;
            }
        }
    }

    private void devourRandomItem(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        int slot = random.nextInt(36);
        ItemStack stack = inventory.getStack(slot);

        if (!stack.isEmpty()) {
            ItemStack stackToDevour;
            if (stack.getCount() > 1) {
                // Si es un stack, solo tomar uno
                stackToDevour = stack.split(1);
            } else {
                stackToDevour = stack.copy();
                inventory.removeStack(slot);
            }

            // Guardar items importantes
            if (stackToDevour.getItem() instanceof net.minecraft.item.SwordItem ||
                    stackToDevour.getItem() instanceof ArmorItem ||
                    isValuableItem(stackToDevour)) {
                devouredItems.add(stackToDevour.copy());
            }

            // Efectos visuales y sonoros
            spawnItemParticles();
            playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 0.8F + random.nextFloat() * 0.4F);

            // Beneficios según el tipo de item
            if (stackToDevour.getItem() instanceof net.minecraft.item.SwordItem) {
                float swordBonus = getSwordBonus(stackToDevour);
                bonusDamage += swordBonus;
                player.sendMessage(Text.literal("§c¡El Devorador ha consumido tu " + stackToDevour.getName().getString() + " y se ha fortalecido!"), true);
            } else if (stackToDevour.isFood()) {
                int foodHealing = stackToDevour.getItem().getFoodComponent().getHunger() * 2;
                healingPower += foodHealing;
                heal(foodHealing);
                player.sendMessage(Text.literal("§c¡El Devorador ha consumido tu " + stackToDevour.getName().getString() + " y se ha curado!"), true);
            } else {
                player.sendMessage(Text.literal("§c¡El Devorador ha consumido tu " + stackToDevour.getName().getString() + "!"), true);
            }

            itemsDevoured++;

            this.getWorld().addParticle(ParticleTypes.CRIMSON_SPORE,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    0.0D, 0.0D, 0.0D);
        }
    }

    private float getArmorBonus(ItemStack armor) {
        if (armor.isOf(Items.NETHERITE_HELMET) || armor.isOf(Items.NETHERITE_CHESTPLATE) ||
                armor.isOf(Items.NETHERITE_LEGGINGS) || armor.isOf(Items.NETHERITE_BOOTS)) return 3.0F;
        if (armor.isOf(Items.DIAMOND_HELMET) || armor.isOf(Items.DIAMOND_CHESTPLATE) ||
                armor.isOf(Items.DIAMOND_LEGGINGS) || armor.isOf(Items.DIAMOND_BOOTS)) return 2.5F;
        if (armor.isOf(Items.IRON_HELMET) || armor.isOf(Items.IRON_CHESTPLATE) ||
                armor.isOf(Items.IRON_LEGGINGS) || armor.isOf(Items.IRON_BOOTS)) return 2.0F;
        return 1.0F;
    }

    private boolean isValuableItem(ItemStack stack) {
        return stack.isOf(Items.DIAMOND) || stack.isOf(Items.NETHERITE_INGOT) ||
                stack.isOf(Items.ENCHANTED_GOLDEN_APPLE) || stack.isOf(Items.TOTEM_OF_UNDYING);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        // Dropear items importantes al morir
        if (!this.getWorld().isClient) {
            for (ItemStack item : devouredItems) {
                // 70% de probabilidad de recuperar cada item importante
                if (random.nextFloat() < 0.7f) {
                    this.dropStack(item);
                }
            }
        }
    }

    private float getSwordBonus(ItemStack sword) {
        if (sword.isOf(Items.NETHERITE_SWORD)) return 4.0F;
        if (sword.isOf(Items.DIAMOND_SWORD)) return 3.0F;
        if (sword.isOf(Items.IRON_SWORD)) return 2.0F;
        if (sword.isOf(Items.STONE_SWORD)) return 1.5F;
        return 1.0F;
    }

    private void spawnItemParticles() {
        for (int i = 0; i < 20; i++) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.getWorld().addParticle(
                    ParticleTypes.PORTAL,
                    this.getX() + (double)(this.random.nextFloat() * this.getWidth()) - this.getWidth()/2,
                    this.getY() + 1.0D + (double)(this.random.nextFloat() * this.getHeight()),
                    this.getZ() + (double)(this.random.nextFloat() * this.getWidth()) - this.getWidth()/2,
                    d0, d1, d2
            );
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("BonusDamage", this.bonusDamage);
        nbt.putFloat("BonusArmor", this.bonusArmor);
        nbt.putInt("HealingPower", this.healingPower);
        nbt.putInt("ItemsDevoured", this.itemsDevoured);

        // Guardar items devorados
        NbtList itemsList = new NbtList();
        for (ItemStack item : devouredItems) {
            NbtCompound itemTag = new NbtCompound();
            item.writeNbt(itemTag);
            itemsList.add(itemTag);
        }
        nbt.put("DevouredItems", itemsList);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.bonusDamage = nbt.getFloat("BonusDamage");
        this.bonusArmor = nbt.getFloat("BonusArmor");
        this.healingPower = nbt.getInt("HealingPower");
        this.itemsDevoured = nbt.getInt("ItemsDevoured");

        // Cargar items devorados
        NbtList itemsList = nbt.getList("DevouredItems", 10);
        this.devouredItems.clear();
        for (int i = 0; i < itemsList.size(); i++) {
            NbtCompound itemTag = itemsList.getCompound(i);
            this.devouredItems.add(ItemStack.fromNbt(itemTag));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (this.isAttacking) {
                return state.setAndContinue(ATTACK_ANIM);
            }
            if (state.isMoving()) {
                return state.setAndContinue(WALK_ANIM);
            }
            return state.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_GUARDIAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GUARDIAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GUARDIAN_DEATH;
    }
}