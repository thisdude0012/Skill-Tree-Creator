/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  javax.annotation.Nonnull
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.Input
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.TickTask
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.AbstractArrow
 *  net.minecraft.world.entity.projectile.AbstractArrow$Pickup
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.entity.projectile.ThrownPotion
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.event.MovementInputUpdateEvent
 *  net.minecraftforge.client.event.RenderTooltipEvent$GatherComponents
 *  net.minecraftforge.common.Tags$Blocks
 *  net.minecraftforge.event.AnvilUpdateEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingEntityUseItemEvent$Finish
 *  net.minecraftforge.event.entity.living.LivingEntityUseItemEvent$Tick
 *  net.minecraftforge.event.entity.living.LivingExperienceDropEvent
 *  net.minecraftforge.event.entity.living.LivingFallEvent
 *  net.minecraftforge.event.entity.living.LivingHealEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.living.MobEffectEvent$Added
 *  net.minecraftforge.event.entity.living.MobEffectEvent$Applicable
 *  net.minecraftforge.event.entity.living.ShieldBlockEvent
 *  net.minecraftforge.event.entity.player.AttackEntityEvent
 *  net.minecraftforge.event.entity.player.CriticalHitEvent
 *  net.minecraftforge.event.entity.player.ItemFishedEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.LogicalSide
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  top.theillusivec4.curios.api.event.CurioEquipEvent
 */
package daripher.skilltree.skill.bonus;

import com.mojang.datafixers.util.Either;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.mixin.AbstractArrowAccessor;
import daripher.skilltree.mixin.MobEffectInstanceAccessor;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.event.AttackEventListener;
import daripher.skilltree.skill.bonus.event.BlockEventListener;
import daripher.skilltree.skill.bonus.event.DamageTakenEventListener;
import daripher.skilltree.skill.bonus.event.ItemUsedEventListener;
import daripher.skilltree.skill.bonus.event.KillEventListener;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.player.ArrowRetrievalBonus;
import daripher.skilltree.skill.bonus.player.BlockBreakSpeedBonus;
import daripher.skilltree.skill.bonus.player.CanPoisonAnyoneBonus;
import daripher.skilltree.skill.bonus.player.CantUseItemBonus;
import daripher.skilltree.skill.bonus.player.CritChanceBonus;
import daripher.skilltree.skill.bonus.player.CritDamageBonus;
import daripher.skilltree.skill.bonus.player.DamageAvoidanceBonus;
import daripher.skilltree.skill.bonus.player.DamageBonus;
import daripher.skilltree.skill.bonus.player.DamageConversionBonus;
import daripher.skilltree.skill.bonus.player.DamageTakenBonus;
import daripher.skilltree.skill.bonus.player.EffectDurationBonus;
import daripher.skilltree.skill.bonus.player.FreeEnchantmentBonus;
import daripher.skilltree.skill.bonus.player.GainedExperienceBonus;
import daripher.skilltree.skill.bonus.player.HealthReservationBonus;
import daripher.skilltree.skill.bonus.player.IncomingHealingBonus;
import daripher.skilltree.skill.bonus.player.ItemUsageSpeedBonus;
import daripher.skilltree.skill.bonus.player.ItemUseMovementSpeedBonus;
import daripher.skilltree.skill.bonus.player.JumpHeightBonus;
import daripher.skilltree.skill.bonus.player.ProjectileDuplicationBonus;
import daripher.skilltree.skill.bonus.player.ProjectileSpeedBonus;
import daripher.skilltree.skill.bonus.player.RepairEfficiencyBonus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

@Mod.EventBusSubscriber(modid="skilltree")
public class SkillBonusHandler {
    @SubscribeEvent
    public static void applyBreakSpeedMultiplier(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        float multiplier = 1.0f;
        event.setNewSpeed(event.getNewSpeed() * (multiplier += SkillBonusHandler.getSkillBonuses(player, BlockBreakSpeedBonus.class).stream().map(b -> Float.valueOf(b.getMultiplier(player))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue()));
    }

    @SubscribeEvent
    public static void applyFallReductionMultiplier(LivingFallEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        float multiplier = SkillBonusHandler.getJumpHeightMultiplier(player);
        if (multiplier <= 1.0f) {
            return;
        }
        event.setDistance(event.getDistance() / multiplier);
    }

    @SubscribeEvent
    public static void applyRepairEfficiency(AnvilUpdateEvent event) {
        int materialsUsed;
        int durabilityPerMaterial;
        ItemStack stack;
        Player player = event.getPlayer();
        float efficiency = SkillBonusHandler.getRepairEfficiency(player, stack = event.getLeft());
        if (efficiency == 1.0f) {
            return;
        }
        if (!stack.m_41763_() || !stack.m_41768_()) {
            return;
        }
        ItemStack material = event.getRight();
        if (!stack.m_41720_().m_6832_(stack, material)) {
            return;
        }
        ItemStack result = stack.m_41777_();
        int durabilityRestored = durabilityPerMaterial = (int)((float)(result.m_41776_() * 12) * (1.0f + efficiency) / 100.0f);
        int cost = 0;
        for (materialsUsed = 0; durabilityRestored > 0 && materialsUsed < material.m_41613_(); ++materialsUsed) {
            result.m_41721_(result.m_41773_() - durabilityRestored);
            ++cost;
            durabilityRestored = Math.min(result.m_41773_(), durabilityPerMaterial);
        }
        if (event.getName() != null && !StringUtils.isBlank((CharSequence)event.getName())) {
            if (!event.getName().equals(stack.m_41786_().getString())) {
                ++cost;
                result.m_41714_((Component)Component.m_237113_((String)event.getName()));
            }
        } else if (stack.m_41788_()) {
            ++cost;
            result.m_41787_();
        }
        event.setMaterialCost(materialsUsed);
        event.setCost(cost);
        event.setOutput(result);
    }

    private static float getRepairEfficiency(Player player, ItemStack stack) {
        float efficiency = 1.0f;
        for (RepairEfficiencyBonus bonus : SkillBonusHandler.getSkillBonuses(player, RepairEfficiencyBonus.class)) {
            if (!bonus.getItemCondition().met(stack)) continue;
            efficiency += bonus.getMultiplier();
        }
        return efficiency;
    }

    @SubscribeEvent
    public static void tickSkillBonuses(TickEvent.PlayerTickEvent event) {
        if (event.player.m_21224_()) {
            return;
        }
        Player player = event.player;
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        if (event.phase == TickEvent.Phase.END) {
            return;
        }
        SkillBonusHandler.getSkillBonuses((Player)player2, TickingSkillBonus.class).forEach(bonus -> bonus.tick(player2));
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void applyFlatDamageBonus(LivingHurtEvent event) {
        Player attacker = SkillBonusHandler.getPlayerAttacker(event);
        if (attacker == null) {
            return;
        }
        LivingEntity target = event.getEntity();
        SkillBonusHandler.setLastTarget(attacker, target);
        float bonus = SkillBonusHandler.getDamageBonus(attacker, event.getSource(), target, AttributeModifier.Operation.ADDITION);
        event.setAmount(event.getAmount() + bonus);
    }

    private static void setLastTarget(Player attacker, LivingEntity target) {
        CompoundTag dataTag = attacker.getPersistentData();
        dataTag.m_128405_("LastAttackTarget", target.m_19879_());
    }

    @SubscribeEvent
    public static void applyBaseDamageMultipliers(LivingHurtEvent event) {
        Player attacker = SkillBonusHandler.getPlayerAttacker(event);
        if (attacker == null) {
            return;
        }
        float bonus = SkillBonusHandler.getDamageBonus(attacker, event.getSource(), event.getEntity(), AttributeModifier.Operation.MULTIPLY_BASE);
        event.setAmount(event.getAmount() * (1.0f + bonus));
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public static void applyTotalDamageMultipliers(LivingHurtEvent event) {
        Player attacker = SkillBonusHandler.getPlayerAttacker(event);
        if (attacker == null) {
            return;
        }
        float bonus = SkillBonusHandler.getDamageBonus(attacker, event.getSource(), event.getEntity(), AttributeModifier.Operation.MULTIPLY_TOTAL);
        event.setAmount(event.getAmount() * (1.0f + bonus));
    }

    @Nullable
    private static Player getPlayerAttacker(LivingHurtEvent event) {
        Player attacker = null;
        Entity entity = event.getSource().m_7639_();
        if (entity instanceof Player) {
            Player player;
            attacker = player = (Player)entity;
        } else {
            entity = event.getSource().m_7640_();
            if (entity instanceof Player) {
                Player player;
                attacker = player = (Player)entity;
            }
        }
        return attacker;
    }

    private static float getDamageBonus(Player player, DamageSource damageSource, LivingEntity target, AttributeModifier.Operation operation) {
        float amount = 0.0f;
        for (DamageBonus bonus : SkillBonusHandler.getSkillBonuses(player, DamageBonus.class)) {
            amount += bonus.getDamageBonus(operation, damageSource, player, target);
        }
        return amount;
    }

    @SubscribeEvent
    public static void applyCritBonuses(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        Entity entity = event.getTarget();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity target = (LivingEntity)entity;
        DamageSource damageSource = player2.m_9236_().m_269111_().m_269075_((Player)player2);
        float critChance = SkillBonusHandler.getCritChance(player2, damageSource, (LivingEntity)event.getEntity());
        if (player2.m_217043_().m_188501_() >= critChance) {
            return;
        }
        float critMultiplier = event.getDamageModifier();
        critMultiplier += SkillBonusHandler.getCritDamageMultiplier(player2, damageSource, target);
        if (!event.isVanillaCritical()) {
            critMultiplier += 0.5f;
            event.setResult(Event.Result.ALLOW);
        }
        event.setDamageModifier(critMultiplier);
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public static void applyCritBonuses(LivingHurtEvent event) {
        if (event.getSource().m_7640_() instanceof Player) {
            return;
        }
        Entity entity = event.getSource().m_7639_();
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)entity;
        float critChance = SkillBonusHandler.getCritChance(player, event.getSource(), event.getEntity());
        if (player.m_217043_().m_188501_() >= critChance) {
            return;
        }
        float critMultiplier = 1.5f;
        event.setAmount(event.getAmount() * (critMultiplier += SkillBonusHandler.getCritDamageMultiplier(player, event.getSource(), event.getEntity())));
    }

    private static float getCritDamageMultiplier(ServerPlayer player, DamageSource source, LivingEntity target) {
        float multiplier = 0.0f;
        for (CritDamageBonus bonus : SkillBonusHandler.getSkillBonuses((Player)player, CritDamageBonus.class)) {
            multiplier += bonus.getDamageBonus(source, (Player)player, target);
        }
        return multiplier;
    }

    private static float getCritChance(ServerPlayer player, DamageSource source, LivingEntity target) {
        float critChance = 0.0f;
        for (CritChanceBonus bonus : SkillBonusHandler.getSkillBonuses((Player)player, CritChanceBonus.class)) {
            critChance += bonus.getChanceBonus(source, (Player)player, target);
        }
        return critChance;
    }

    @SubscribeEvent
    public static void applyIncomingHealingBonus(LivingHealEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        float multiplier = 1.0f;
        for (IncomingHealingBonus bonus : SkillBonusHandler.getSkillBonuses(player, IncomingHealingBonus.class)) {
            multiplier += bonus.getHealingMultiplier(player);
        }
        event.setAmount(event.getAmount() * multiplier);
    }

    @SubscribeEvent
    public static void applyExperienceFromMobsBonus(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player == null) {
            return;
        }
        float multiplier = 1.0f;
        event.setDroppedExperience((int)((float)event.getDroppedExperience() * (multiplier += SkillBonusHandler.getExperienceMultiplier(player, GainedExperienceBonus.ExperienceSource.MOBS))));
    }

    @SubscribeEvent
    public static void applyExperienceFromOreBonus(BlockEvent.BreakEvent event) {
        if (!event.getState().m_204336_(Tags.Blocks.ORES)) {
            return;
        }
        float multiplier = 1.0f;
        event.setExpToDrop((int)((float)event.getExpToDrop() * (multiplier += SkillBonusHandler.getExperienceMultiplier(event.getPlayer(), GainedExperienceBonus.ExperienceSource.ORE))));
    }

    @SubscribeEvent
    public static void applyFishingExperienceBonus(ItemFishedEvent event) {
        Player player = event.getEntity();
        float multiplier = SkillBonusHandler.getExperienceMultiplier(player, GainedExperienceBonus.ExperienceSource.FISHING);
        if (multiplier == 0.0f) {
            return;
        }
        int exp = (int)((float)(player.m_217043_().m_188503_(6) + 1) * multiplier);
        if (exp == 0) {
            return;
        }
        ExperienceOrb expOrb = new ExperienceOrb(player.m_9236_(), player.m_20185_(), player.m_20186_() + 0.5, player.m_20189_() + 0.5, exp);
        player.m_9236_().m_7967_((Entity)expOrb);
    }

    private static float getExperienceMultiplier(Player player, GainedExperienceBonus.ExperienceSource source) {
        float multiplier = 0.0f;
        for (GainedExperienceBonus bonus : SkillBonusHandler.getSkillBonuses(player, GainedExperienceBonus.class)) {
            if (bonus.getSource() != source) continue;
            multiplier += bonus.getMultiplier();
        }
        return multiplier;
    }

    @SubscribeEvent
    public static void applyEventListenerEffect(LivingHurtEvent event) {
        Object copy;
        SkillEventListener listener;
        Object object;
        Player player;
        Entity sourceEntity = event.getSource().m_7639_();
        if (sourceEntity instanceof Player) {
            player = (Player)sourceEntity;
            object = SkillBonusHandler.getSkillBonuses(player, EventListenerBonus.class).iterator();
            while (object.hasNext()) {
                EventListenerBonus bonus = (EventListenerBonus)object.next();
                SkillEventListener skillEventListener = bonus.getEventListener();
                if (!(skillEventListener instanceof AttackEventListener)) continue;
                listener = (AttackEventListener)skillEventListener;
                copy = bonus.copy();
                ((AttackEventListener)listener).onEvent(player, event.getEntity(), event.getSource(), (EventListenerBonus)copy);
            }
        }
        if ((object = event.getEntity()) instanceof Player) {
            player = (Player)object;
            for (EventListenerBonus bonus : SkillBonusHandler.getSkillBonuses(player, EventListenerBonus.class)) {
                copy = bonus.getEventListener();
                if (!(copy instanceof DamageTakenEventListener)) continue;
                listener = (DamageTakenEventListener)copy;
                copy = bonus.copy();
                LivingEntity attacker = sourceEntity instanceof LivingEntity ? (LivingEntity)sourceEntity : null;
                ((DamageTakenEventListener)listener).onEvent(player, attacker, event.getSource(), (EventListenerBonus)copy);
            }
        }
    }

    @SubscribeEvent
    public static void applyEventListenerEffect(ShieldBlockEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        for (EventListenerBonus bonus : SkillBonusHandler.getSkillBonuses(player, EventListenerBonus.class)) {
            SkillEventListener skillEventListener = bonus.getEventListener();
            if (!(skillEventListener instanceof BlockEventListener)) continue;
            BlockEventListener listener = (BlockEventListener)skillEventListener;
            SkillBonus copy = bonus.copy();
            DamageSource source = event.getDamageSource();
            Entity sourceEntity = source.m_7639_();
            LivingEntity attacker = sourceEntity instanceof LivingEntity ? (LivingEntity)sourceEntity : null;
            listener.onEvent(player, attacker, source, (EventListenerBonus)copy);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void applyEventListenerEffect(LivingEntityUseItemEvent.Finish event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        for (EventListenerBonus bonus : SkillBonusHandler.getSkillBonuses(player, EventListenerBonus.class)) {
            SkillEventListener skillEventListener = bonus.getEventListener();
            if (!(skillEventListener instanceof ItemUsedEventListener)) continue;
            ItemUsedEventListener listener = (ItemUsedEventListener)skillEventListener;
            SkillBonus copy = bonus.copy();
            listener.onEvent(player, event.getItem(), (EventListenerBonus)copy);
        }
    }

    @SubscribeEvent
    public static void applyEventListenerEffect(LivingDeathEvent event) {
        Entity entity = event.getSource().m_7639_();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        for (EventListenerBonus bonus : SkillBonusHandler.getSkillBonuses(player, EventListenerBonus.class)) {
            SkillEventListener skillEventListener = bonus.getEventListener();
            if (!(skillEventListener instanceof KillEventListener)) continue;
            KillEventListener listener = (KillEventListener)skillEventListener;
            SkillBonus copy = bonus.copy();
            DamageSource source = event.getSource();
            listener.onEvent(player, (LivingEntity)player, source, (EventListenerBonus)copy);
        }
    }

    @SubscribeEvent
    public static void applyArrowRetrievalBonus(LivingHurtEvent event) {
        Entity entity = event.getSource().m_7640_();
        if (!(entity instanceof AbstractArrow)) {
            return;
        }
        AbstractArrow arrow = (AbstractArrow)entity;
        Entity entity2 = event.getSource().m_7639_();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player player = (Player)entity2;
        AbstractArrowAccessor arrowAccessor = (AbstractArrowAccessor)arrow;
        ItemStack arrowStack = arrowAccessor.invokeGetPickupItem();
        if (arrowStack == null) {
            return;
        }
        float retrievalChance = 0.0f;
        for (ArrowRetrievalBonus bonus : SkillBonusHandler.getSkillBonuses(player, ArrowRetrievalBonus.class)) {
            retrievalChance += bonus.getChance();
        }
        if (player.m_217043_().m_188501_() >= retrievalChance) {
            return;
        }
        LivingEntity target = event.getEntity();
        CompoundTag targetData = target.getPersistentData();
        ListTag stuckArrowsTag = targetData.m_128437_("StuckArrows", (int)new CompoundTag().m_7060_());
        stuckArrowsTag.add((Object)arrowStack.m_41739_(new CompoundTag()));
        targetData.m_128365_("StuckArrows", (Tag)stuckArrowsTag);
    }

    @SubscribeEvent
    public static void retrieveArrows(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        ListTag arrowsTag = entity.getPersistentData().m_128437_("StuckArrows", (int)new CompoundTag().m_7060_());
        if (arrowsTag.isEmpty()) {
            return;
        }
        for (Tag tag : arrowsTag) {
            ItemStack arrowStack = ItemStack.m_41712_((CompoundTag)((CompoundTag)tag));
            entity.m_19983_(arrowStack);
        }
    }

    @SubscribeEvent
    public static void applyHealthReservationEffect(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.side == LogicalSide.CLIENT) {
            return;
        }
        float reservation = SkillBonusHandler.getHealthReservation(event.player);
        if (reservation == 0.0f) {
            return;
        }
        if (event.player.m_21223_() / event.player.m_21233_() > 1.0f - reservation) {
            event.player.m_21153_(event.player.m_21233_() * (1.0f - reservation));
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void applyHealthReservationEffect(LivingHealEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        float reservation = SkillBonusHandler.getHealthReservation(player);
        if (reservation == 0.0f) {
            return;
        }
        float healthAfterHealing = player.m_21223_() + event.getAmount();
        if (healthAfterHealing / player.m_21233_() > 1.0f - reservation) {
            event.setCanceled(true);
        }
    }

    private static float getHealthReservation(Player player) {
        float reservation = 0.0f;
        for (HealthReservationBonus bonus : SkillBonusHandler.getSkillBonuses(player, HealthReservationBonus.class)) {
            reservation += bonus.getAmount(player);
        }
        return reservation;
    }

    @SubscribeEvent
    public static void applyCantUseItemBonus(AttackEntityEvent event) {
        for (CantUseItemBonus bonus : SkillBonusHandler.getSkillBonuses(event.getEntity(), CantUseItemBonus.class)) {
            if (!bonus.getItemCondition().met(event.getEntity().m_21205_())) continue;
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent
    public static void applyCantUseItemBonus(PlayerInteractEvent event) {
        for (CantUseItemBonus bonus : SkillBonusHandler.getSkillBonuses(event.getEntity(), CantUseItemBonus.class)) {
            if (!bonus.getItemCondition().met(event.getItemStack())) continue;
            event.setCancellationResult(InteractionResult.FAIL);
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            return;
        }
    }

    @SubscribeEvent
    public static void applyCantUseItemBonus(CurioEquipEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        for (CantUseItemBonus bonus : SkillBonusHandler.getSkillBonuses(player, CantUseItemBonus.class)) {
            if (!bonus.getItemCondition().met(event.getStack())) continue;
            event.setResult(Event.Result.DENY);
            return;
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void addCantUseItemTooltip(RenderTooltipEvent.GatherComponents event) {
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        if (player == null) {
            return;
        }
        for (CantUseItemBonus bonus : SkillBonusHandler.getSkillBonuses((Player)player, CantUseItemBonus.class)) {
            if (!bonus.getItemCondition().met(event.getItemStack())) continue;
            MutableComponent tooltip = Component.m_237115_((String)"item.cant_use.info").m_130940_(ChatFormatting.RED);
            event.getTooltipElements().add(Either.left((Object)tooltip));
            return;
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST, receiveCanceled=true)
    public static void inflictPoisonForcefully(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance().m_19544_() != MobEffects.f_19614_) {
            return;
        }
        LivingEntity livingEntity = event.getEntity().m_21232_();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        if (SkillBonusHandler.getSkillBonuses(player, CanPoisonAnyoneBonus.class).isEmpty()) {
            return;
        }
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void applyDamageTakenBonuses(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        DamageSource damageSource = event.getSource();
        Entity entity = damageSource.m_7639_();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity attacker = (LivingEntity)entity;
        float damageTaken = event.getAmount();
        float addition = SkillBonusHandler.getDamageTaken(player, attacker, damageSource, AttributeModifier.Operation.ADDITION);
        damageTaken += addition;
        float multiplier = SkillBonusHandler.getDamageTaken(player, attacker, damageSource, AttributeModifier.Operation.MULTIPLY_BASE);
        damageTaken *= 1.0f + multiplier;
        float multiplierTotal = SkillBonusHandler.getDamageTaken(player, attacker, damageSource, AttributeModifier.Operation.MULTIPLY_TOTAL);
        event.setAmount(damageTaken *= 1.0f + multiplierTotal);
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void applyDamageAvoidanceBonuses(LivingAttackEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        DamageSource damageSource = event.getSource();
        Entity entity = damageSource.m_7639_();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity attacker = (LivingEntity)entity;
        float avoidance = SkillBonusHandler.getSkillBonuses(player, DamageAvoidanceBonus.class).stream().map(b -> Float.valueOf(b.getAvoidanceChance(damageSource, player, attacker))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue();
        if (player.m_217043_().m_188501_() < avoidance) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void applyDamageConversionBonuses(LivingHurtEvent event) {
        DamageSource originalDamageSource = event.getSource();
        Entity entity = originalDamageSource.m_7639_();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (SkillBonusHandler.getDamageConversionBonuses(player, originalDamageSource).findAny().isEmpty()) {
            return;
        }
        LivingEntity target = event.getEntity();
        float originalDamageAmount = event.getAmount();
        SkillBonusHandler.getDamageConversionMap(event, player, originalDamageSource).forEach((damageCondition, amount) -> {
            DamageSource damageSource = damageCondition.createDamageSource(player);
            SkillBonusHandler.forcefullyInflictDamage(damageSource, amount.floatValue() * originalDamageAmount, (Entity)target);
        });
        float convertedDamage = SkillBonusHandler.getConvertedDamagePercentage(player, originalDamageSource, target);
        event.setAmount(originalDamageAmount * (1.0f - convertedDamage));
    }

    @SubscribeEvent
    public static void applyEffectDurationBonuses(MobEffectEvent.Added event) {
        LivingEntity livingEntity;
        Projectile projectile;
        Entity entity;
        Player source = null;
        Entity entity2 = event.getEffectSource();
        if (entity2 instanceof Player) {
            Player player;
            source = player = (Player)entity2;
        }
        if ((entity = event.getEffectSource()) instanceof Projectile && (entity = (projectile = (Projectile)entity).m_19749_()) instanceof Player) {
            Player player;
            source = player = (Player)entity;
        }
        Player playerSource = source;
        float durationMultiplier = 1.0f;
        if (source != null) {
            durationMultiplier += SkillBonusHandler.getSkillBonuses(playerSource, EffectDurationBonus.class).stream().filter(b -> b.getTarget() == SkillBonus.Target.ENEMY).map(b -> Float.valueOf(b.getDuration(playerSource, event.getEntity()))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue();
        }
        if ((livingEntity = event.getEntity()) instanceof Player) {
            Player player = (Player)livingEntity;
            durationMultiplier += SkillBonusHandler.getSkillBonuses(player, EffectDurationBonus.class).stream().filter(b -> b.getTarget() == SkillBonus.Target.PLAYER).map(b -> Float.valueOf(b.getDuration(playerSource, (LivingEntity)player))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue();
        }
        if (durationMultiplier == 1.0f) {
            return;
        }
        MobEffectInstance effectInstance = event.getEffectInstance();
        int newDuration = (int)((float)effectInstance.m_19557_() * durationMultiplier);
        ((MobEffectInstanceAccessor)effectInstance).setDuration(newDuration);
    }

    @SubscribeEvent
    public static void applyProjectileDuplicationBonuses(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Projectile)) {
            return;
        }
        Projectile projectile = (Projectile)entity;
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        Entity entity2 = projectile.m_19749_();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player player = (Player)entity2;
        CompoundTag projectileTag = projectile.getPersistentData();
        if (projectileTag.m_128471_("duplicated")) {
            return;
        }
        float duplicationChance = SkillBonusHandler.getPlayerBonuses(player, ProjectileDuplicationBonus.class).stream().map(b -> Float.valueOf(b.getChance(player))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue();
        if (duplicationChance == 0.0f) {
            return;
        }
        projectileTag.m_128379_("duplicated", true);
        int projectileAmount = (int)duplicationChance;
        duplicationChance -= (float)projectileAmount;
        RandomSource random = player.m_217043_();
        if (random.m_188501_() < duplicationChance) {
            ++projectileAmount;
        }
        SkillBonusHandler.fireDuplicateProjectiles(projectile, level2, player, projectileAmount);
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void forcefullyInflictDuplicatedProjectileDamage(LivingAttackEvent event) {
        Projectile projectile;
        DamageSource damageSource = event.getSource();
        Entity entity = damageSource.m_7640_();
        if (!(entity instanceof Projectile) || !((projectile = (Projectile)entity).m_19749_() instanceof Player)) {
            return;
        }
        CompoundTag projectileTag = projectile.getPersistentData();
        if (!projectileTag.m_128471_("duplicated")) {
            return;
        }
        LivingEntity target = event.getEntity();
        target.f_19802_ = 0;
        target.m_20331_(false);
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void applyProjectileSpeedBonus(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Projectile)) {
            return;
        }
        Projectile projectile = (Projectile)entity;
        if (!(event.getLevel() instanceof ServerLevel)) {
            return;
        }
        Entity entity2 = projectile.m_19749_();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player player = (Player)entity2;
        CompoundTag projectileTag = projectile.getPersistentData();
        if (projectileTag.m_128471_("speed_applied")) {
            return;
        }
        float speedBonus = 1.0f;
        if ((speedBonus += SkillBonusHandler.getPlayerBonuses(player, ProjectileSpeedBonus.class).stream().map(b -> Float.valueOf(b.getMultiplier(player))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue()) == 1.0f) {
            return;
        }
        projectileTag.m_128379_("speed_applied", true);
        Vec3 speedBonusVec = new Vec3((double)speedBonus, (double)speedBonus, (double)speedBonus);
        Vec3 projectileMovement = projectile.m_20184_();
        projectile.m_20256_(projectileMovement.m_82559_(speedBonusVec));
    }

    @SubscribeEvent
    public static void applyItemUsageSpeed(LivingEntityUseItemEvent.Tick event) {
        int mod;
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        float additionalSpeed = SkillBonusHandler.getSkillBonuses(player, ItemUsageSpeedBonus.class).stream().map(bonus -> Float.valueOf(bonus.getMultiplier(player, event.getItem()))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue();
        if (additionalSpeed == 0.0f) {
            return;
        }
        int useTimeOffset = -1;
        if (additionalSpeed < 0.0f) {
            useTimeOffset = 1;
            additionalSpeed *= -1.0f;
        }
        while (additionalSpeed > 1.0f) {
            event.setDuration(event.getDuration() + useTimeOffset);
            additionalSpeed -= 1.0f;
        }
        if (additionalSpeed > 0.5f) {
            if (event.getEntity().f_19797_ % 2 == 0) {
                event.setDuration(event.getDuration() + useTimeOffset);
            }
            additionalSpeed -= 0.5f;
        }
        if (event.getEntity().f_19797_ % (mod = (int)Math.floor(1.0f / Math.min(1.0f, additionalSpeed))) == 0) {
            event.setDuration(event.getDuration() + useTimeOffset);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    @SubscribeEvent
    public static void applyItemUseMovementSpeedBonus(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        Input input = event.getInput();
        if (player.m_6117_() && !player.m_20159_()) {
            float defaultPenalty = 0.8f;
            float penaltyReduction = SkillBonusHandler.getSkillBonuses(player, ItemUseMovementSpeedBonus.class).stream().map(bonus -> Float.valueOf(bonus.getMultiplier(player, player.m_21211_()))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue();
            defaultPenalty += defaultPenalty * penaltyReduction;
            float reductionFactor = 1.0f - defaultPenalty;
            input.f_108566_ *= reductionFactor;
            input.f_108567_ *= reductionFactor;
            input.f_108566_ *= 5.0f;
            input.f_108567_ *= 5.0f;
        }
    }

    private static void fireDuplicateProjectiles(Projectile projectile, ServerLevel level, Player player, int projectileAmount) {
        float spreadAngle = 5.0f;
        for (int i = 0; i < projectileAmount; ++i) {
            int side = i % 2 == 0 ? 1 : -1;
            int projectileNumber = i / 2 + 1;
            float angleOffset = (float)(projectileNumber * side) * spreadAngle;
            SkillBonusHandler.duplicateProjectileWithOffset(projectile, player, level, angleOffset);
        }
    }

    private static void duplicateProjectileWithOffset(Projectile projectile, Player player, ServerLevel level, float angleOffset) {
        EntityType projectileType = projectile.m_6095_();
        Projectile duplicate = (Projectile)projectileType.m_20615_((Level)level);
        if (duplicate == null) {
            return;
        }
        Vec3 movementVector = projectile.m_20184_();
        Vec3 rotatedDirection = SkillBonusHandler.rotateVector(movementVector, angleOffset);
        Vec3 originalPos = projectile.m_20182_();
        Vec3 duplicatePos = originalPos.m_82549_(rotatedDirection.m_82541_());
        duplicate.m_6034_(duplicatePos.f_82479_, duplicatePos.f_82480_, duplicatePos.f_82481_);
        duplicate.m_20256_(rotatedDirection);
        duplicate.m_5602_((Entity)player);
        CompoundTag projectileTag = duplicate.getPersistentData();
        projectileTag.m_128379_("duplicated", true);
        if (duplicate instanceof AbstractArrow) {
            AbstractArrow arrow = (AbstractArrow)duplicate;
            arrow.f_36705_ = AbstractArrow.Pickup.DISALLOWED;
            float velocity = (float)movementVector.m_82553_();
            arrow.m_36745_((LivingEntity)player, velocity);
        } else if (projectile instanceof ThrownPotion) {
            ThrownPotion originalPotion = (ThrownPotion)projectile;
            if (duplicate instanceof ThrownPotion) {
                ThrownPotion potion = (ThrownPotion)duplicate;
                potion.m_37446_(originalPotion.m_7846_());
            }
        }
        level.m_7967_((Entity)duplicate);
    }

    private static Vec3 rotateVector(Vec3 vector, double angleDegrees) {
        double angleRadians = Math.toRadians(angleDegrees);
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        double x = vector.f_82479_ * cos - vector.f_82481_ * sin;
        double z = vector.f_82479_ * sin + vector.f_82481_ * cos;
        return new Vec3(x, vector.f_82480_, z);
    }

    private static float getConvertedDamagePercentage(Player player, DamageSource originalDamageSource, LivingEntity target) {
        return SkillBonusHandler.getDamageConversionBonuses(player, originalDamageSource).map(b -> Float.valueOf(b.getConversionRate(originalDamageSource, player, target))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue();
    }

    @NotNull
    private static Map<DamageCondition, Float> getDamageConversionMap(LivingHurtEvent event, Player player, DamageSource originalDamageSource) {
        HashMap<DamageCondition, Float> conversions = new HashMap<DamageCondition, Float>();
        SkillBonusHandler.getDamageConversionBonuses(player, originalDamageSource).forEach(bonus -> {
            DamageCondition resultDamageSource = bonus.getResultDamageCondition();
            conversions.put(resultDamageSource, Float.valueOf(conversions.getOrDefault(resultDamageSource, Float.valueOf(0.0f)).floatValue() + bonus.getConversionRate(originalDamageSource, player, event.getEntity())));
        });
        return conversions;
    }

    @NotNull
    private static Stream<DamageConversionBonus> getDamageConversionBonuses(Player player, DamageSource damageSource) {
        return SkillBonusHandler.getSkillBonuses(player, DamageConversionBonus.class).stream().filter(b -> b.getOriginalDamageCondition().met(damageSource)).filter(b -> !b.getResultDamageCondition().met(damageSource));
    }

    public static void forcefullyInflictDamage(DamageSource source, float amount, Entity entity) {
        MinecraftServer server = entity.m_20194_();
        if (server == null) {
            return;
        }
        server.m_6937_((Runnable)new TickTask(server.m_129921_() + 1, () -> {
            entity.f_19802_ = 0;
            entity.m_6469_(source, amount);
        }));
    }

    private static float getDamageTaken(Player player, LivingEntity attacker, DamageSource damageSource, AttributeModifier.Operation operation) {
        List<DamageTakenBonus> damageTakenBonuses = SkillBonusHandler.getSkillBonuses(player, DamageTakenBonus.class);
        return damageTakenBonuses.stream().map(b -> Float.valueOf(b.getDamageBonus(operation, damageSource, player, attacker))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue();
    }

    public static float getJumpHeightMultiplier(Player player) {
        float multiplier = 1.0f;
        for (JumpHeightBonus bonus : SkillBonusHandler.getSkillBonuses(player, JumpHeightBonus.class)) {
            multiplier += bonus.getJumpHeightMultiplier(player);
        }
        return multiplier;
    }

    public static float getFreeEnchantmentChance(@Nonnull Player player) {
        float chance = 0.0f;
        for (FreeEnchantmentBonus bonus : SkillBonusHandler.getSkillBonuses(player, FreeEnchantmentBonus.class)) {
            chance += bonus.getChance();
        }
        return chance;
    }

    public static <T> List<T> getSkillBonuses(@Nonnull Player player, Class<T> type) {
        if (!PlayerSkillsProvider.hasSkills(player)) {
            return List.of();
        }
        ArrayList<T> bonuses = new ArrayList<T>();
        bonuses.addAll(SkillBonusHandler.getPlayerBonuses(player, type));
        bonuses.addAll(SkillBonusHandler.getEffectBonuses(player, type));
        return SkillBonusHandler.mergeSkillBonuses(bonuses);
    }

    @NotNull
    private static <T> List<T> mergeSkillBonuses(List<T> bonuses) {
        ArrayList<SkillBonus> mergedBonuses = new ArrayList<SkillBonus>();
        for (T bonus : bonuses) {
            SkillBonus skillBonus = (SkillBonus)bonus;
            Optional<SkillBonus> mergeTarget = mergedBonuses.stream().map(SkillBonus.class::cast).filter(skillBonus::canMerge).findAny();
            if (mergeTarget.isPresent()) {
                mergedBonuses.remove(mergeTarget.get());
                mergedBonuses.add(mergeTarget.get().copy().merge(skillBonus));
                continue;
            }
            mergedBonuses.add(skillBonus);
        }
        return mergedBonuses;
    }

    private static <T> List<T> getPlayerBonuses(Player player, Class<T> type) {
        ArrayList<T> list = new ArrayList<T>();
        for (PassiveSkill skill : PlayerSkillsProvider.get(player).getPlayerSkills()) {
            List<SkillBonus<?>> bonuses = skill.getBonuses();
            for (SkillBonus<?> skillBonus : bonuses) {
                if (!type.isInstance(skillBonus)) continue;
                list.add(type.cast(skillBonus));
            }
        }
        return list;
    }

    private static <T> List<T> getEffectBonuses(Player player, Class<T> type) {
        ArrayList<T> bonuses = new ArrayList<T>();
        for (MobEffectInstance e : player.m_21220_()) {
            SkillBonusEffect skillEffect;
            SkillBonus<?> bonus;
            MobEffect mobEffect = e.m_19544_();
            if (!(mobEffect instanceof SkillBonusEffect) || !type.isInstance(bonus = (skillEffect = (SkillBonusEffect)mobEffect).getBonus().copy())) continue;
            bonus = bonus.copy().multiply(e.m_19564_());
            bonuses.add(type.cast(bonus));
        }
        return bonuses;
    }
}

