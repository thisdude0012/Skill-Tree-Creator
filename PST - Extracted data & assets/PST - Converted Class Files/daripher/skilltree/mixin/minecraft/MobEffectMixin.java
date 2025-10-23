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
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.common.extensions.IForgeMobEffect
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.init.PSTDamageTypes;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.LethalPoisonBonus;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgeMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MobEffect.class})
public abstract class MobEffectMixin
implements IForgeMobEffect {
    @Inject(method={"applyEffectTick"}, at={@At(value="HEAD")}, cancellable=true)
    public void inflictPoisonDamage(LivingEntity livingEntity, int amplifier, CallbackInfo callbackInfo) {
        if (this != MobEffects.f_19614_) {
            return;
        }
        MobEffectMixin.handlePoisonDamage(livingEntity);
        callbackInfo.cancel();
    }

    private static void handlePoisonDamage(LivingEntity livingEntity) {
        LivingEntity attacker = livingEntity.m_21232_();
        float damage = 1.0f;
        boolean isLowHealth = livingEntity.m_21223_() <= damage;
        boolean isPoisonLethal = MobEffectMixin.isPoisonLethal(attacker);
        if (isLowHealth && !isPoisonLethal) {
            return;
        }
        DamageSources damageSources = livingEntity.m_269291_();
        DamageSource damageSource = damageSources.m_269425_();
        if (attacker instanceof Player) {
            Player player = (Player)attacker;
            Registry damageTypes = player.m_9236_().m_9598_().m_175515_(Registries.f_268580_);
            Holder.Reference damageType = damageTypes.m_246971_(PSTDamageTypes.POISON);
            damageSource = new DamageSource((Holder)damageType, (Entity)player, null);
            livingEntity.m_6598_(player);
        }
        SkillBonusHandler.forcefullyInflictDamage(damageSource, damage, (Entity)livingEntity);
    }

    private static boolean isPoisonLethal(LivingEntity attacker) {
        Player player;
        return attacker instanceof Player && !SkillBonusHandler.getSkillBonuses(player = (Player)attacker, LethalPoisonBonus.class).isEmpty();
    }
}

