package xyz.skizzikindustries.projectapple.item.materials;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import xyz.skizzikindustries.projectapple.ModSoundEvents;
import xyz.skizzikindustries.projectapple.ProjectApple;
import xyz.skizzikindustries.projectapple.item.ModItems;

public class CandyArmorMaterial implements IArmorMaterial {
    @Override
    public int getDurabilityForSlot(EquipmentSlotType slot) {
        return new int[]{195, 225, 240, 165}[slot.getIndex()];
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slot) {
        return new int[]{2, 5, 6, 2}[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.SLIME_BLOCK_PLACE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.CANDY_INGOT.get());
    }

    @Override
    public String getName() {
        return ProjectApple.MOD_ID+":"+"candy_armor";
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public float getKnockbackResistance() {
        return 0f;
    }
}
