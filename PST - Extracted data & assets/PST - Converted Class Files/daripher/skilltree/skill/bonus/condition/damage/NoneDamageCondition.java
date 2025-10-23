/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.damagesource.DamageSource
 */
package daripher.skilltree.skill.bonus.condition.damage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTDamageConditions;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;

public enum NoneDamageCondition implements DamageCondition
{
    INSTANCE;


    @Override
    public boolean met(DamageSource source) {
        return true;
    }

    @Override
    public DamageCondition.Serializer getSerializer() {
        return (DamageCondition.Serializer)PSTDamageConditions.NONE.get();
    }

    public static class Serializer
    implements DamageCondition.Serializer {
        @Override
        public DamageCondition deserialize(JsonObject json) throws JsonParseException {
            return INSTANCE;
        }

        @Override
        public void serialize(JsonObject json, DamageCondition condition) {
            if (condition != INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition deserialize(CompoundTag tag) {
            return INSTANCE;
        }

        @Override
        public CompoundTag serialize(DamageCondition condition) {
            if (condition != INSTANCE) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public DamageCondition deserialize(FriendlyByteBuf buf) {
            return INSTANCE;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, DamageCondition condition) {
            if (condition != INSTANCE) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition createDefaultInstance() {
            return INSTANCE;
        }
    }
}

