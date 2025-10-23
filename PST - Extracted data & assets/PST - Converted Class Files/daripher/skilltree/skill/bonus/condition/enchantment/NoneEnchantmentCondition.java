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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public enum NoneEnchantmentCondition implements EnchantmentCondition
{
    INSTANCE;


    @Override
    public boolean met(EnchantmentCategory category) {
        return true;
    }

    @Override
    public EnchantmentCondition.Serializer getSerializer() {
        return (EnchantmentCondition.Serializer)PSTEnchantmentConditions.NONE.get();
    }

    public static class Serializer
    implements EnchantmentCondition.Serializer {
        @Override
        public EnchantmentCondition deserialize(JsonObject json) throws JsonParseException {
            return INSTANCE;
        }

        @Override
        public void serialize(JsonObject json, EnchantmentCondition condition) {
            if (condition != INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public EnchantmentCondition deserialize(CompoundTag tag) {
            return INSTANCE;
        }

        @Override
        public CompoundTag serialize(EnchantmentCondition condition) {
            if (condition != INSTANCE) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public EnchantmentCondition deserialize(FriendlyByteBuf buf) {
            return INSTANCE;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, EnchantmentCondition condition) {
            if (condition != INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public EnchantmentCondition createDefaultInstance() {
            return INSTANCE;
        }
    }
}

