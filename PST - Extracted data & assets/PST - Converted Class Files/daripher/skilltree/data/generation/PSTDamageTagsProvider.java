/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.tags.DamageTypeTagsProvider
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraftforge.common.data.ExistingFileHelper
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package daripher.skilltree.data.generation;

import daripher.skilltree.init.PSTDamageTypes;
import daripher.skilltree.init.PSTTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PSTDamageTagsProvider
extends DamageTypeTagsProvider {
    public PSTDamageTagsProvider(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper fileHelper) {
        super(dataGenerator.getPackOutput(), provider, "skilltree", fileHelper);
    }

    protected void m_6577_(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider provider) {
        this.add(PSTTags.DamageTypes.IS_MAGIC, DamageTypes.f_268515_, DamageTypes.f_268530_, PSTDamageTypes.POISON);
    }

    @SafeVarargs
    private void add(TagKey<DamageType> damageTag, ResourceKey<DamageType> ... types) {
        for (ResourceKey<DamageType> type : types) {
            this.m_206424_(damageTag).m_255204_(type);
        }
    }
}

