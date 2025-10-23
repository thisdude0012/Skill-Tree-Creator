/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.alchemy.PotionUtils
 *  net.minecraft.world.item.alchemy.Potions
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.common.brewing.BrewingRecipeRegistry
 *  net.minecraftforge.common.crafting.StrictNBTIngredient
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.init;

import daripher.skilltree.init.PSTPotions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid="skilltree", bus=Mod.EventBusSubscriber.Bus.MOD)
public class PSTBrewingRecipes {
    @SubscribeEvent
    public static void addRecipes(FMLCommonSetupEvent event) {
        PSTBrewingRecipes.addRecipe(Potions.f_43610_, Items.f_42592_, (Potion)PSTPotions.LIQUID_FIRE_1.get());
        PSTBrewingRecipes.addSplashRecipe((Potion)PSTPotions.LIQUID_FIRE_1.get());
        PSTBrewingRecipes.addLingeringRecipe((Potion)PSTPotions.LIQUID_FIRE_1.get());
        PSTBrewingRecipes.addRecipe((Potion)PSTPotions.LIQUID_FIRE_1.get(), Items.f_42525_, (Potion)PSTPotions.LIQUID_FIRE_2.get());
        PSTBrewingRecipes.addSplashRecipe((Potion)PSTPotions.LIQUID_FIRE_2.get());
        PSTBrewingRecipes.addLingeringRecipe((Potion)PSTPotions.LIQUID_FIRE_2.get());
    }

    private static void addRecipe(Potion inputPotion, Item ingredient, @NotNull Potion outputPotion) {
        Item[] potionItems;
        for (Item potionItem : potionItems = new Item[]{Items.f_42589_, Items.f_42736_, Items.f_42739_}) {
            PSTBrewingRecipes.addRecipe(inputPotion, potionItem, ingredient, outputPotion, potionItem);
        }
    }

    private static void addLingeringRecipe(@NotNull Potion potion) {
        PSTBrewingRecipes.addRecipe(potion, Items.f_42736_, Items.f_42735_, potion, Items.f_42739_);
    }

    private static void addSplashRecipe(@NotNull Potion potion) {
        PSTBrewingRecipes.addRecipe(potion, Items.f_42589_, Items.f_42403_, potion, Items.f_42736_);
    }

    private static void addRecipe(Potion inputPotion, Item inputItem, Item ingredient, @NotNull Potion outputPotion, Item outputItem) {
        ItemStack input = PSTBrewingRecipes.getPotionStack(inputItem, inputPotion);
        ItemStack output = PSTBrewingRecipes.getPotionStack(outputItem, outputPotion);
        BrewingRecipeRegistry.addRecipe((Ingredient)StrictNBTIngredient.of((ItemStack)input), (Ingredient)Ingredient.m_43929_((ItemLike[])new ItemLike[]{ingredient}), (ItemStack)output);
    }

    @NotNull
    private static ItemStack getPotionStack(Item potionItem, @NotNull Potion outputPotion) {
        ItemStack output = new ItemStack((ItemLike)potionItem);
        PotionUtils.m_43549_((ItemStack)output, (Potion)outputPotion);
        return output;
    }
}

