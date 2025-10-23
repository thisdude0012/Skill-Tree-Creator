/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.loot.LootTableProvider
 *  net.minecraft.data.loot.LootTableProvider$SubProviderEntry
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.level.storage.loot.ValidationContext
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.data.generation.loot;

import daripher.skilltree.data.generation.loot.PSTBlockLoot;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

public class PSTLootTablesProvider
extends LootTableProvider {
    public static final Set<ResourceLocation> REQUIRED_TABLES = Set.of();

    public PSTLootTablesProvider(DataGenerator generator) {
        super(generator.getPackOutput(), REQUIRED_TABLES, List.of((Object)new LootTableProvider.SubProviderEntry(PSTBlockLoot::new, LootContextParamSets.f_81421_)));
    }

    protected void validate(@NotNull Map<ResourceLocation, LootTable> map, @NotNull ValidationContext ctx) {
    }
}

