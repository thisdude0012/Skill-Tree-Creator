/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 */
package daripher.skilltree.entity;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface EquippedEntity {
    public boolean hasItemEquipped(ItemStack var1);

    default public boolean hasItemEquipped(ItemEntity entity) {
        return this.hasItemEquipped(entity.m_32055_());
    }
}

