package com.pixelfabric.entity;

import com.pixelfabric.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MoobloomEntity extends CowEntity {
    // Constantes
    private static final int MAX_MILKING_PER_DAY = 2;
    private static final int MINECRAFT_DAY_LENGTH = 24000; // ticks en un día de Minecraft

    // Variables para rastrear el ordeño
    private int milkingCount = 0;
    private long lastDayChecked = 0;

    public MoobloomEntity(EntityType<? extends CowEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.getItem() == Items.BUCKET) {
            // Verificar si podemos ordeñar
            if (canBeMilked()) {
                // Actualizar contador de ordeño
                milkingCount++;

                // Realizar el ordeño
                ItemStack goldenMilkStack = new ItemStack(ModItems.GOLDEN_MILK);

                // Sonidos y efectos de partículas similares a la vaca normal
                playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);

                // Reducir la cubeta vacía y dar la leche dorada
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (itemStack.isEmpty()) {
                    player.setStackInHand(hand, goldenMilkStack);
                } else if (!player.getInventory().insertStack(goldenMilkStack)) {
                    player.dropItem(goldenMilkStack, false);
                }

                return ActionResult.success(this.getWorld().isClient);
            }
            // Si no se puede ordeñar, mostrar partículas de fallo
            if (this.getWorld().isClient) {
                this.getWorld().addParticle(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + 0.5D, this.getZ(),
                        0.0D, 0.0D, 0.0D);
            }
            return ActionResult.CONSUME;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        // Verificar si ha pasado un nuevo día
        long currentDay = this.getWorld().getTime() / MINECRAFT_DAY_LENGTH;
        if (currentDay > lastDayChecked) {
            lastDayChecked = currentDay;
            milkingCount = 0; // Resetear el contador cada día
        }
    }

    private boolean canBeMilked() {
        return milkingCount < MAX_MILKING_PER_DAY;
    }

    // Métodos para guardar/cargar los datos de ordeño
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("MilkingCount", this.milkingCount);
        nbt.putLong("LastDayChecked", this.lastDayChecked);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.milkingCount = nbt.getInt("MilkingCount");
        this.lastDayChecked = nbt.getLong("LastDayChecked");
    }
}
