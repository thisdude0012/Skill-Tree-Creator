/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package daripher.skilltree.mixin.minecraft;

import daripher.skilltree.entity.player.PlayerExtension;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Player.class})
public abstract class PlayerMixin
extends LivingEntity
implements PlayerExtension {
    private int rainbowJewelInsertionSeed;

    protected PlayerMixin() {
        super(null, null);
    }

    @Inject(method={"readAdditionalSaveData"}, at={@At(value="TAIL")})
    private void readRainbowJewelInsertionSeed(CompoundTag tag, CallbackInfo callbackInfo) {
        this.rainbowJewelInsertionSeed = tag.m_128451_("RainbowJewelInsertionSeed");
    }

    @Inject(method={"addAdditionalSaveData"}, at={@At(value="TAIL")})
    private void writeRainbowJewelInsertionSeed(CompoundTag tag, CallbackInfo callbackInfo) {
        tag.m_128405_("RainbowJewelInsertionSeed", this.rainbowJewelInsertionSeed);
    }

    @Inject(method={"onEnchantmentPerformed"}, at={@At(value="HEAD")})
    private void restoreEnchantmentExperience(ItemStack itemStack, int enchantmentCost, CallbackInfo callbackInfo) {
        Player player = (Player)this;
        float freeEnchantmentChance = SkillBonusHandler.getFreeEnchantmentChance(player);
        if (player.m_217043_().m_188501_() < freeEnchantmentChance) {
            player.m_6749_(enchantmentCost);
        }
    }

    @Override
    public int getGemsRandomSeed() {
        return this.rainbowJewelInsertionSeed;
    }

    @Override
    public void updateGemsRandomSeed() {
        this.rainbowJewelInsertionSeed = this.f_19796_.m_188502_();
    }
}

