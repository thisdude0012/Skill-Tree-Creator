/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.ItemSupplier
 *  net.minecraft.world.entity.projectile.ThrowableItemProjectile
 *  net.minecraft.world.entity.projectile.ThrownPotion
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.SelfSplashImmuneBonus;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ThrownPotion.class})
public abstract class ThrownPotionMixin
extends ThrowableItemProjectile
implements ItemSupplier {
    private ThrownPotionMixin() {
        super(null, null);
    }

    @Redirect(method={"applySplash"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean setAttackerOnHit(LivingEntity entity, MobEffectInstance effectInstance, Entity effectSource) {
        Entity entity2 = this.m_19749_();
        if (entity2 instanceof Player) {
            Player player = (Player)entity2;
            entity.m_6598_(player);
        }
        return entity.m_147207_(effectInstance, effectSource);
    }

    @Redirect(method={"applySplash"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private <T extends Entity> List<T> removePlayerTarget(Level level, Class<T> entityClass, AABB area) {
        List targets = level.m_45976_(entityClass, area);
        Entity owner = this.m_19749_();
        if (!(owner instanceof Player)) {
            return targets;
        }
        Player player = (Player)owner;
        if (!targets.contains(player)) {
            return targets;
        }
        List<SelfSplashImmuneBonus> bonuses = SkillBonusHandler.getSkillBonuses(player, SelfSplashImmuneBonus.class);
        if (bonuses.isEmpty()) {
            return targets;
        }
        targets.removeIf(arg_0 -> ((Entity)owner).equals(arg_0));
        return targets;
    }
}

