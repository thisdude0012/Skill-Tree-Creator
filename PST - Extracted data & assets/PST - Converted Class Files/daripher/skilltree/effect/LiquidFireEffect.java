/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageSources
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectCategory
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package daripher.skilltree.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiquidFireEffect
extends MobEffect {
    public LiquidFireEffect() {
        super(MobEffectCategory.HARMFUL, 16401420);
    }

    public boolean m_8093_() {
        return true;
    }

    public void m_19461_(@Nullable Entity source, @Nullable Entity indirectSource, @NotNull LivingEntity target, int amplifier, double health) {
        float damage = (int)(health * (double)(6 << amplifier) + 0.5);
        DamageSources damageSources = target.m_269291_();
        if (source == null) {
            target.m_6469_(damageSources.m_269549_(), damage);
        } else {
            Registry damageTypes = target.m_9236_().m_9598_().m_175515_(Registries.f_268580_);
            Holder.Reference damageType = damageTypes.m_246971_(DamageTypes.f_268468_);
            DamageSource damageSource = new DamageSource((Holder)damageType, source, indirectSource);
            target.m_6469_(damageSource, damage);
        }
        target.m_20254_((int)damage / 2);
    }
}

