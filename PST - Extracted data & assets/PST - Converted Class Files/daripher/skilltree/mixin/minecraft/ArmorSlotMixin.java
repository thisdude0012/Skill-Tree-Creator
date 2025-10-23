/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.CantUseItemBonus;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets={"net.minecraft.world.inventory.InventoryMenu$1"})
public abstract class ArmorSlotMixin
extends Slot {
    public ArmorSlotMixin() {
        super(null, 0, 0, 0);
    }

    @Inject(method={"mayPlace", "m_5857_"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void preventItemUsage(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
        Container container = this.f_40218_;
        if (!(container instanceof Inventory)) {
            return;
        }
        Inventory inventory = (Inventory)container;
        for (CantUseItemBonus bonus : SkillBonusHandler.getSkillBonuses(inventory.f_35978_, CantUseItemBonus.class)) {
            if (!bonus.getItemCondition().met(stack)) continue;
            callbackInfo.setReturnValue((Object)false);
            return;
        }
    }
}

