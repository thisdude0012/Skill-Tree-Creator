/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.recipebook.RecipeCollection
 *  net.minecraft.world.item.crafting.Recipe
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package daripher.skilltree.mixin;

import java.util.Set;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RecipeCollection.class})
public interface RecipeCollectionAccessor {
    @Accessor
    public Set<Recipe<?>> getCraftable();
}

