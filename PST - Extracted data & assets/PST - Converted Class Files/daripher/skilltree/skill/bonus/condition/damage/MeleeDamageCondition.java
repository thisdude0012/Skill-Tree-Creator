/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.player.Player
 */
package daripher.skilltree.skill.bonus.condition.damage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTDamageConditions;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public record MeleeDamageCondition() implements DamageCondition
{
    @Override
    public boolean met(DamageSource source) {
        return source.m_7640_() instanceof Player;
    }

    @Override
    public DamageCondition.Serializer getSerializer() {
        return (DamageCondition.Serializer)PSTDamageConditions.MELEE.get();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return this.getSerializer().hashCode();
    }

    public static class Serializer
    implements DamageCondition.Serializer {
        @Override
        public DamageCondition deserialize(JsonObject json) throws JsonParseException {
            return new MeleeDamageCondition();
        }

        @Override
        public void serialize(JsonObject json, DamageCondition condition) {
            if (!(condition instanceof MeleeDamageCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition deserialize(CompoundTag tag) {
            return new MeleeDamageCondition();
        }

        @Override
        public CompoundTag serialize(DamageCondition condition) {
            if (!(condition instanceof MeleeDamageCondition)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public DamageCondition deserialize(FriendlyByteBuf buf) {
            return new MeleeDamageCondition();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, DamageCondition condition) {
            if (!(condition instanceof MeleeDamageCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition createDefaultInstance() {
            return new MeleeDamageCondition();
        }
    }
}

