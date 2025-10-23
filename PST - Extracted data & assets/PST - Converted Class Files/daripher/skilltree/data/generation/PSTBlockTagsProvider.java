/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.DataGenerator
 *  net.minecraftforge.common.data.BlockTagsProvider
 *  net.minecraftforge.common.data.ExistingFileHelper
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.data.generation;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class PSTBlockTagsProvider
extends BlockTagsProvider {
    public PSTBlockTagsProvider(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper fileHelper) {
        super(dataGenerator.getPackOutput(), provider, "skilltree", fileHelper);
    }

    protected void m_6577_(@NotNull HolderLookup.Provider provider) {
    }
}

