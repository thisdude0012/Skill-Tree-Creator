/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.SmithingMenu
 *  net.minecraft.world.item.crafting.SmithingRecipe
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package daripher.skilltree.mixin;

import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SmithingMenu.class})
public interface SmithingMenuAccessor {
    @Accessor
    @Nullable
    public SmithingRecipe getSelectedRecipe();
}

