/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.player.Player
 */
package daripher.skilltree.skill.bonus.condition.damage;

import daripher.skilltree.init.PSTRegistries;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public interface DamageCondition {
    public boolean met(DamageSource var1);

    default public String getDescriptionId() {
        ResourceLocation id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey((Object)this.getSerializer());
        Objects.requireNonNull(id);
        return "damage_condition.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
    }

    default public MutableComponent getTooltip() {
        return Component.m_237115_((String)this.getDescriptionId());
    }

    default public MutableComponent getTooltip(String type) {
        return Component.m_237115_((String)(this.getDescriptionId() + "." + type));
    }

    public Serializer getSerializer();

    default public DamageSource createDamageSource(Player player) {
        throw new UnsupportedOperationException("Can not create damage source from " + this.getDescriptionId());
    }

    default public boolean canCreateDamageSource() {
        return false;
    }

    public static interface Serializer
    extends daripher.skilltree.data.serializers.Serializer<DamageCondition> {
        public DamageCondition createDefaultInstance();
    }
}

