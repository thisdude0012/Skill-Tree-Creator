/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 */
package daripher.skilltree.skill.bonus.condition.damage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.init.PSTDamageConditions;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public record FireDamageCondition() implements DamageCondition
{
    @Override
    public boolean met(DamageSource source) {
        return source.m_269533_(DamageTypeTags.f_268745_);
    }

    @Override
    public DamageCondition.Serializer getSerializer() {
        return (DamageCondition.Serializer)PSTDamageConditions.FIRE.get();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    @Override
    public DamageSource createDamageSource(Player player) {
        Registry damageTypes = player.m_9236_().m_9598_().m_175515_(Registries.f_268580_);
        Holder.Reference damageType = damageTypes.m_246971_(DamageTypes.f_268468_);
        return new DamageSource((Holder)damageType, null, (Entity)player);
    }

    @Override
    public boolean canCreateDamageSource() {
        return true;
    }

    public int hashCode() {
        return this.getSerializer().hashCode();
    }

    public static class Serializer
    implements DamageCondition.Serializer {
        @Override
        public DamageCondition deserialize(JsonObject json) throws JsonParseException {
            return new FireDamageCondition();
        }

        @Override
        public void serialize(JsonObject json, DamageCondition condition) {
            if (!(condition instanceof FireDamageCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition deserialize(CompoundTag tag) {
            return new FireDamageCondition();
        }

        @Override
        public CompoundTag serialize(DamageCondition condition) {
            if (!(condition instanceof FireDamageCondition)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public DamageCondition deserialize(FriendlyByteBuf buf) {
            return new FireDamageCondition();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, DamageCondition condition) {
            if (!(condition instanceof FireDamageCondition)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public DamageCondition createDefaultInstance() {
            return new FireDamageCondition();
        }
    }
}

