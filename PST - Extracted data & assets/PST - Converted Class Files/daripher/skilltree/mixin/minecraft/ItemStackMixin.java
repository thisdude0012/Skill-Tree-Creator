/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.ItemDurabilityLossAvoidanceBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ItemStack.class})
public class ItemStackMixin {
    @Inject(method={"hurt"}, at={@At(value="HEAD")}, cancellable=true)
    public void preventDurabilityLoss(int amount, RandomSource random, @Nullable ServerPlayer user, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (user == null) {
            return;
        }
        ItemStack itemStack = (ItemStack)this;
        float chance = SkillBonusHandler.getSkillBonuses((Player)user, ItemDurabilityLossAvoidanceBonus.class).stream().map(bonus -> Float.valueOf(bonus.getChance((Player)user, itemStack))).reduce(Float::sum).orElse(Float.valueOf(0.0f)).floatValue();
        if (random.m_188501_() < chance) {
            callbackInfo.setReturnValue((Object)false);
        }
    }
}

