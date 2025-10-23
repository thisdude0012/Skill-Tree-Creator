/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 *  net.minecraftforge.common.data.GlobalLootModifierProvider
 *  net.minecraftforge.common.loot.IGlobalLootModifier
 */
package daripher.skilltree.data.generation;

import daripher.skilltree.loot.modifier.SkillBonusesModifier;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;

public class PSTGlobalLootModifierProvider
extends GlobalLootModifierProvider {
    public PSTGlobalLootModifierProvider(DataGenerator generator) {
        super(generator.getPackOutput(), "skilltree");
    }

    protected void start() {
        this.add("skill_bonuses", (IGlobalLootModifier)new SkillBonusesModifier(new LootItemCondition[0]));
    }
}

