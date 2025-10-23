/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.PotionItem
 *  net.minecraft.world.item.enchantment.EnchantmentCategory
 *  net.minecraft.world.level.ItemLike
 */
package daripher.skilltree.init;

import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.ItemLike;

public class PSTEnchantmentCategories {
    public static EnchantmentCategory SHIELD = EnchantmentCategory.create((String)"shield", item -> EquipmentCondition.isShield(new ItemStack((ItemLike)item)));
    public static EnchantmentCategory POTION = EnchantmentCategory.create((String)"potion", item -> item instanceof PotionItem);
}

