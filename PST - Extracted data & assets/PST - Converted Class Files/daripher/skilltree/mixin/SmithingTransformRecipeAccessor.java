/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.SmithingTransformRecipe
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package daripher.skilltree.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={SmithingTransformRecipe.class})
public interface SmithingTransformRecipeAccessor {
    @Accessor
    public Ingredient getTemplate();

    @Accessor
    public Ingredient getBase();

    @Accessor
    public Ingredient getAddition();

    @Accessor
    public ItemStack getResult();
}

