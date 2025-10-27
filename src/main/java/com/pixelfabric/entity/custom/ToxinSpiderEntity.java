package com.pixelfabric.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ToxinSpiderEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int rangedAttackCooldown = 0;
    private int meleeAttackCooldown = 0;
    public boolean isRangedAttacking = false;
    public boolean isMeleeAttacking = false; // Nueva variable para ataque cuerpo a cuerpo

    // Cooldowns y configuraciones - MÁS AGRESIVA
    private static final int RANGED_ATTACK_COOLDOWN = 40; // 2 segundos - mucho más rápido
    private static final int MELEE_ATTACK_COOLDOWN = 25; // 1.25 segundos - más rápido
    private static final int RANGED_ATTACK_RANGE = 16; // bloques
    private static final int MELEE_ATTACK_RANGE = 3; // bloques
    private static final int POISON_CLOUD_DURATION = 100; // 5 segundos

    public ToxinSpiderEntity(EntityType<? extends ToxinSpiderEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.0F); // Puede subir bloques más altos
        this.experiencePoints = 12; // Experiencia considerable
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));

        // IMPORTANTE: Cambiar el orden - melee primero para priorizar ataques cercanos
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2D, false) {
            @Override
            protected double getSquaredMaxAttackDistance(LivingEntity entity) {
                return MELEE_ATTACK_RANGE * MELEE_ATTACK_RANGE;
            }

            @Override
            public boolean canStart() {
                LivingEntity target = ToxinSpiderEntity.this.getTarget();
                if (target == null || !target.isAlive()) {
                    return false;
                }

                double distance = ToxinSpiderEntity.this.squaredDistanceTo(target);
                return meleeAttackCooldown <= 0 &&
                        distance <= this.getSquaredMaxAttackDistance(target) &&
                        !isRangedAttacking && // No atacar en melee si está haciendo ataque ranged
                        super.canStart();
            }

            @Override
            public void start() {
                super.start();
                isMeleeAttacking = true;
            }

            @Override
            public void stop() {
                super.stop();
                isMeleeAttacking = false;
            }

            @Override
            protected void attack(LivingEntity target, double squaredDistance) {
                if (squaredDistance <= this.getSquaredMaxAttackDistance(target)) {
                    this.resetCooldown();
                    meleeAttackCooldown = MELEE_ATTACK_COOLDOWN;
                    ToxinSpiderEntity.this.swingHand(net.minecraft.util.Hand.MAIN_HAND); // Activar animación
                    tryAttack(target);
                }
            }
        });

        this.goalSelector.add(3, new ToxinSpiderRangedAttackGoal(this, 1.2D, RANGED_ATTACK_COOLDOWN, RANGED_ATTACK_RANGE)); // Más rápida mientras dispara

        this.goalSelector.add(4, new WanderAroundGoal(this, 0.8));
        this.goalSelector.add(5, new LookAroundGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35) // Más rápida
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 90.0) // 45 corazones - MUCHO más resistente
                .add(EntityAttributes.GENERIC_ARMOR, 6.0) // Más armadura
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 3.0) // Más resistente
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 14.0) // MÁS DAÑO - ahora duele
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0) // Mejor seguimiento
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5) // Más knockback
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5); // Más resistente al knockback
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (!super.tryAttack(target)) {
            return false;
        }

        // Aplicar veneno en ataque cuerpo a cuerpo - MÁS LETAL
        if (target instanceof LivingEntity livingTarget) {
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 140, 2), this); // Veneno III por 7 segundos
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1), this); // Lentitud II por 5 segundos
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 0), this); // Debilidad por 4 segundos
        }

        return true;
    }

    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (this.getWorld().isClient || this.rangedAttackCooldown > 0) {
            return;
        }

        this.isRangedAttacking = true;

        // Crear proyectil personalizado de toxina
        ToxinProjectileEntity projectile = new ToxinProjectileEntity(this.getWorld(), this);

        // PREDICCIÓN DE MOVIMIENTO - Calcular donde estará el target
        Vec3d targetVelocity = target.getVelocity();
        double predictionTime = 1.0; // Predecir 1 segundo adelante

        double predictedX = target.getX() + (targetVelocity.x * predictionTime);
        double predictedY = target.getY() + (targetVelocity.y * predictionTime);
        double predictedZ = target.getZ() + (targetVelocity.z * predictionTime);

        // Posicionar el proyectil desde el centro de la araña
        double spiderCenterX = this.getX();
        double spiderCenterY = this.getY() + this.getHeight() * 0.5F;
        double spiderCenterZ = this.getZ();

        projectile.setPosition(spiderCenterX, spiderCenterY, spiderCenterZ);

        // Calcular dirección hacia la posición predicha del target
        double deltaX = predictedX - spiderCenterX;
        double deltaY = (predictedY + target.getHeight() * 0.5F) - spiderCenterY;
        double deltaZ = predictedZ - spiderCenterZ;

        // Normalizar la dirección
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        if (distance > 0) {
            deltaX /= distance;
            deltaY /= distance;
            deltaZ /= distance;
        }

        // PUNTERÍA ULTRA PRECISA - Sin dispersión aleatoria
        double projectileSpeed = 2.5D; // Velocidad consistente

        // Aplicar compensación de gravedad para tiros a larga distancia
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double gravityCompensation = (horizontalDistance * 0.05D); // Compensar caída

        projectile.setVelocity(
                deltaX * projectileSpeed,
                (deltaY * projectileSpeed) + gravityCompensation,
                deltaZ * projectileSpeed,
                (float)projectileSpeed,
                0.0F  // CERO dispersión - perfecta precisión
        );

        this.getWorld().spawnEntity(projectile);

        // Sonido de lanzamiento venenoso
        this.playSound(SoundEvents.ENTITY_LLAMA_SPIT, 1.2F, 0.8F);

        this.rangedAttackCooldown = RANGED_ATTACK_COOLDOWN;
    }

    @Override
    public void tick() {
        super.tick();

        // OPTIMIZACIÓN: Solo hacer cálculos cada 5 ticks en lugar de cada tick
        if (this.age % 5 != 0) {
            // Solo decrementar cooldowns cada tick
            if (this.rangedAttackCooldown > 0) {
                this.rangedAttackCooldown--;
            }
            if (this.meleeAttackCooldown > 0) {
                this.meleeAttackCooldown--;
            }
            return; // Salir temprano para ahorrar procesamiento
        }

        // Reset ranged attack flag - más rápido
        if (this.rangedAttackCooldown <= 30) {
            this.isRangedAttacking = false;
        }

        // OPTIMIZACIÓN: Inmunidad al veneno - verificar menos frecuentemente
        if (this.hasStatusEffect(StatusEffects.POISON)) {
            this.removeStatusEffect(StatusEffects.POISON);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // Resistencia a ataques de veneno
        if (source.getName().contains("poison")) {
            return false;
        }

        return super.damage(source, amount);
    }

    // Animaciones
    private PlayState movementPredicate(AnimationState event) {
        if (this.isRangedAttacking) {
            return event.setAndContinue(RawAnimation.begin().then("toxin_attack", Animation.LoopType.PLAY_ONCE));
        }

        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
        }
        return event.setAndContinue(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
    }

    private PlayState attackingPredicate(AnimationState event) {
        // Manejar animación de ataque cuerpo a cuerpo
        if (this.handSwinging && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().forceAnimationReset();
            event.setAndContinue(RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        data.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // Sonidos
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SPIDER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.2F; // Sonidos más fuertes por su tamaño
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.rangedAttackCooldown = nbt.getInt("RangedAttackCooldown");
        this.meleeAttackCooldown = nbt.getInt("MeleeAttackCooldown");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("RangedAttackCooldown", this.rangedAttackCooldown);
        nbt.putInt("MeleeAttackCooldown", this.meleeAttackCooldown);
    }

    // Clase interna para el ataque a distancia
    public class ToxinSpiderRangedAttackGoal extends Goal {
        private final ToxinSpiderEntity spider;
        private final double moveSpeed;
        private final int attackCooldown;
        private final float maxAttackDistance;
        private int attackTime = -1;
        private LivingEntity target;

        public ToxinSpiderRangedAttackGoal(ToxinSpiderEntity spider, double moveSpeed, int attackCooldown, float maxAttackDistance) {
            this.spider = spider;
            this.moveSpeed = moveSpeed;
            this.attackCooldown = attackCooldown;
            this.maxAttackDistance = maxAttackDistance * maxAttackDistance;
        }

        @Override
        public boolean canStart() {
            LivingEntity target = this.spider.getTarget();
            if (target != null && target.isAlive()) {
                this.target = target;
                double distanceSquared = this.spider.squaredDistanceTo(target);

                // Solo usar ataque ranged si NO está en rango de melee y SI está en cooldown de melee O está fuera de rango melee
                return this.spider.rangedAttackCooldown <= 0 &&
                        !this.spider.isMeleeAttacking && // No atacar ranged si está atacando melee
                        (distanceSquared > MELEE_ATTACK_RANGE * MELEE_ATTACK_RANGE || this.spider.meleeAttackCooldown > 0) &&
                        distanceSquared <= this.maxAttackDistance;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return this.canStart() || (!this.spider.getNavigation().isIdle() && this.spider.isRangedAttacking);
        }

        @Override
        public void stop() {
            this.target = null;
            this.attackTime = -1;
            this.spider.isRangedAttacking = false;
        }

        @Override
        public void tick() {
            if (this.target == null) return;

            // OPTIMIZACIÓN: Solo calcular distancia cada 3 ticks para mejorar rendimiento
            if (this.spider.age % 3 != 0 && this.attackTime < 10) {
                return;
            }

            double distanceSquared = this.spider.squaredDistanceTo(this.target);
            boolean canSee = this.spider.getVisibilityCache().canSee(this.target);

            // Si el target está muy cerca, cancelar ataque ranged para permitir melee
            if (distanceSquared <= MELEE_ATTACK_RANGE * MELEE_ATTACK_RANGE && this.spider.meleeAttackCooldown <= 0) {
                this.stop();
                return;
            }

            if (distanceSquared <= this.maxAttackDistance && canSee) {
                // MEJORADO: Menos movimiento errático para mejor puntería
                if (this.attackTime < 10) {
                    // OPTIMIZACIÓN: Solo calcular movimiento cada 10 ticks durante posicionamiento
                    if (this.spider.age % 10 == 0) {
                        double angle = this.spider.age * 0.05D; // Rotación más lenta
                        double offsetX = Math.cos(angle) * 1.0D; // Menor radio
                        double offsetZ = Math.sin(angle) * 1.0D;
                        double targetX = this.target.getX() + offsetX;
                        double targetZ = this.target.getZ() + offsetZ;

                        this.spider.getNavigation().startMovingTo(targetX, this.target.getY(), targetZ, this.moveSpeed * 0.7D);
                    }
                } else if (this.attackTime >= 10 && this.attackTime <= 20) {
                    // COMPLETAMENTE QUIETA durante la puntería y disparo
                    if (this.attackTime == 10) { // Solo hacer esto una vez
                        this.spider.getNavigation().stop();
                        this.spider.setVelocity(0, this.spider.getVelocity().y, 0);
                    }
                } else {
                    // Movimiento evasivo después del disparo
                    this.spider.getNavigation().startMovingTo(this.target, this.moveSpeed);
                }

                this.attackTime++;

                if (this.attackTime == 10) { // Tiempo de preparación
                    this.spider.isRangedAttacking = true;
                }

                if (this.attackTime == 17) { // Disparo con más tiempo de preparación
                    this.spider.performRangedAttack(this.target, 1.0F);
                }

                if (this.attackTime >= 25) { // Ciclo completo
                    this.attackTime = -1;
                    this.spider.isRangedAttacking = false;
                }

                // PUNTERÍA MEJORADA - Solo actualizar cada 2 ticks para reducir cálculos
                if (this.spider.age % 2 == 0) {
                    this.spider.getLookControl().lookAt(
                            this.target.getX(),
                            this.target.getEyeY(),
                            this.target.getZ(),
                            10.0F,
                            10.0F
                    );
                }
            } else {
                // Perseguir más agresivamente
                this.spider.getNavigation().startMovingTo(this.target, this.moveSpeed * 1.1D); // 10% más rápido al perseguir
                this.attackTime = -1;
                this.spider.isRangedAttacking = false;
            }
        }
    }
}