/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.material.Fluids
 */
package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluids;

public record UnderwaterCondition() implements LivingCondition
{
    @Override
    public boolean isConditionMet(LivingEntity living) {
        return living.getEyeInFluidType() == Fluids.f_76193_.getFluidType();
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        String key = this.getDescriptionId();
        MutableComponent targetDescription = Component.m_237115_((String)"%s.target.%s".formatted(new Object[]{key, target.getName()}));
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, targetDescription});
    }

    @Override
    public LivingCondition.Serializer getSerializer() {
        return (LivingCondition.Serializer)PSTLivingConditions.UNDERWATER.get();
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

    public static class Serializer
    implements LivingCondition.Serializer {
        @Override
        public LivingCondition deserialize(JsonObject json) throws JsonParseException {
            return new UnderwaterCondition();
        }

        @Override
        public void serialize(JsonObject json, LivingCondition condition) {
            if (!(condition instanceof UnderwaterCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public LivingCondition deserialize(CompoundTag tag) {
            return new UnderwaterCondition();
        }

        @Override
        public CompoundTag serialize(LivingCondition condition) {
            if (!(condition instanceof UnderwaterCondition)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public LivingCondition deserialize(FriendlyByteBuf buf) {
            return new UnderwaterCondition();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
            if (!(condition instanceof UnderwaterCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public LivingCondition createDefaultInstance() {
            return new UnderwaterCondition();
        }
    }
}

