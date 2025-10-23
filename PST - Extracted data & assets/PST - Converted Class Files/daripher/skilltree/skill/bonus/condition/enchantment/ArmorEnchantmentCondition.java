/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.item.enchantment.EnchantmentCategory
 */
package daripher.skilltree.skill.bonus.condition.enchantment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTEnchantmentConditions;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ArmorEnchantmentCondition
implements EnchantmentCondition {
    @Override
    public boolean met(EnchantmentCategory category) {
        return category == EnchantmentCategory.ARMOR || category == EnchantmentCategory.ARMOR_CHEST || category == EnchantmentCategory.ARMOR_FEET || category == EnchantmentCategory.ARMOR_HEAD || category == EnchantmentCategory.ARMOR_LEGS;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return Objects.hash(this.getSerializer());
    }

    @Override
    public EnchantmentCondition.Serializer getSerializer() {
        return (EnchantmentCondition.Serializer)PSTEnchantmentConditions.ARMOR.get();
    }

    public static class Serializer
    implements EnchantmentCondition.Serializer {
        @Override
        public EnchantmentCondition deserialize(JsonObject json) throws JsonParseException {
            return new ArmorEnchantmentCondition();
        }

        @Override
        public void serialize(JsonObject json, EnchantmentCondition condition) {
            if (!(condition instanceof ArmorEnchantmentCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public EnchantmentCondition deserialize(CompoundTag tag) {
            return new ArmorEnchantmentCondition();
        }

        @Override
        public CompoundTag serialize(EnchantmentCondition condition) {
            if (!(condition instanceof ArmorEnchantmentCondition)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public EnchantmentCondition deserialize(FriendlyByteBuf buf) {
            return new ArmorEnchantmentCondition();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, EnchantmentCondition condition) {
            if (!(condition instanceof ArmorEnchantmentCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public EnchantmentCondition createDefaultInstance() {
            return new ArmorEnchantmentCondition();
        }
    }
}

