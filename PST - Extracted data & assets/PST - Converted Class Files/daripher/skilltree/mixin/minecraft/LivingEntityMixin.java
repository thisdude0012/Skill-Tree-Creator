/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package daripher.skilltree.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import daripher.skilltree.entity.EquippedEntity;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntity.class})
public abstract class LivingEntityMixin
implements EquippedEntity {
    private final List<ItemStack> equippedItems = new ArrayList<ItemStack>();

    @Inject(method={"dropAllDeathLoot"}, at={@At(value="HEAD")})
    private void storeEquipmentBeforeDeath(DamageSource damageSource, CallbackInfo callbackInfo) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemInSlot = this.m_6844_(slot);
            if (itemInSlot.m_41619_()) continue;
            this.equippedItems.add(itemInSlot);
        }
    }

    @ModifyReturnValue(method={"getJumpPower"}, at={@At(value="RETURN")})
    private float applyJumpHeightBonus(float original) {
        boolean isPlayer = this instanceof Player;
        if (!isPlayer) {
            return original;
        }
        Player player = (Player)this;
        return original * SkillBonusHandler.getJumpHeightMultiplier(player);
    }

    @Override
    public boolean hasItemEquipped(ItemStack stack) {
        return this.equippedItems.stream().anyMatch(equipped -> ItemStack.m_41728_((ItemStack)stack, (ItemStack)equipped));
    }

    @Shadow
    public abstract ItemStack m_6844_(EquipmentSlot var1);
}

