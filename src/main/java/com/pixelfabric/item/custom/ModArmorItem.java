package com.pixelfabric.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.pixelfabric.item.ModArmorMaterials;
import com.pixelfabric.item.custom.effects.ArmorEffect;
import com.pixelfabric.item.custom.effects.SummertimeArmorEffect;
import com.pixelfabric.item.custom.effects.WitherArmorEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.UUID;

public class ModArmorItem extends ArmorItem {
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC");
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final ArmorMaterial material;
    private final ArmorEffect armorEffect;

    public ModArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
        this.material = material;

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(getSlotType()));

        // Asigna el modificador de salud adicional para la armadura Wither (ejemplo)
        if (material == ModArmorMaterials.WITHER && type == Type.CHESTPLATE) {
            builder.put(EntityAttributes.GENERIC_MAX_HEALTH,
                    new EntityAttributeModifier(HEALTH_MODIFIER_UUID,
                            "Health modifier", 4.0, EntityAttributeModifier.Operation.ADDITION));
        }

        this.attributeModifiers = builder.build();

        // Asigna el efecto de la armadura según el material
        if (material == ModArmorMaterials.SUMMERTIME) {
            this.armorEffect = (ArmorEffect) new SummertimeArmorEffect();
        } else if (material == ModArmorMaterials.WITHER) {
            this.armorEffect = new WitherArmorEffect();
        } else {
            this.armorEffect = null;
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == this.getSlotType() ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity player) {
            if (hasFullSuitOfArmorOn(player) && hasCorrectArmorOn(material, player)) {
                if (armorEffect != null) {
                    armorEffect.applyEffect(player);
                }
            } else {
                if (armorEffect != null) {
                    armorEffect.removeEffect(player);
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private boolean hasFullSuitOfArmorOn(PlayerEntity player) {
        ItemStack boots = player.getInventory().getArmorStack(0);
        ItemStack leggings = player.getInventory().getArmorStack(1);
        ItemStack breastplate = player.getInventory().getArmorStack(2);
        ItemStack helmet = player.getInventory().getArmorStack(3);

        return !helmet.isEmpty() && !breastplate.isEmpty()
                && !leggings.isEmpty() && !boots.isEmpty();
    }

    private boolean hasCorrectArmorOn(ArmorMaterial material, PlayerEntity player) {
        for (ItemStack armorStack : player.getInventory().armor) {
            if (!(armorStack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }

        ArmorItem boots = ((ArmorItem) player.getInventory().getArmorStack(0).getItem());
        ArmorItem leggings = ((ArmorItem) player.getInventory().getArmorStack(1).getItem());
        ArmorItem breastplate = ((ArmorItem) player.getInventory().getArmorStack(2).getItem());
        ArmorItem helmet = ((ArmorItem) player.getInventory().getArmorStack(3).getItem());

        return helmet.getMaterial() == material && breastplate.getMaterial() == material &&
                leggings.getMaterial() == material && boots.getMaterial() == material;
    }
}
