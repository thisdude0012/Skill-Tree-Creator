/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.LivingEntity
 */
package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public enum NoneLivingCondition implements LivingCondition
{
    INSTANCE;


    @Override
    public boolean isConditionMet(LivingEntity living) {
        return true;
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        return bonusTooltip;
    }

    @Override
    public LivingCondition.Serializer getSerializer() {
        return (LivingCondition.Serializer)PSTLivingConditions.NONE.get();
    }

    public static class Serializer
    implements LivingCondition.Serializer {
        @Override
        public LivingCondition deserialize(JsonObject json) throws JsonParseException {
            return INSTANCE;
        }

        @Override
        public void serialize(JsonObject json, LivingCondition condition) {
            if (condition != INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public LivingCondition deserialize(CompoundTag tag) {
            return INSTANCE;
        }

        @Override
        public CompoundTag serialize(LivingCondition condition) {
            if (condition != INSTANCE) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public LivingCondition deserialize(FriendlyByteBuf buf) {
            return INSTANCE;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
            if (condition != INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public LivingCondition createDefaultInstance() {
            return INSTANCE;
        }
    }
}

