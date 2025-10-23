/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.tags.ItemTagsProvider
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraftforge.common.Tags$Items
 *  net.minecraftforge.common.data.BlockTagsProvider
 *  net.minecraftforge.common.data.ExistingFileHelper
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package daripher.skilltree.data.generation;

import daripher.skilltree.init.PSTTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PSTItemTagsProvider
extends ItemTagsProvider {
    public static final ResourceLocation KNIVES = new ResourceLocation("forge", "tools/knives");

    public PSTItemTagsProvider(DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> provider, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper fileHelper) {
        super(dataGenerator.getPackOutput(), provider, blockTagsProvider.m_274426_(), "skilltree", fileHelper);
    }

    protected void m_6577_(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider provider) {
        this.m_206424_(Tags.Items.TOOLS).m_176841_(KNIVES);
        this.m_206424_(PSTTags.Items.MELEE_WEAPON).addTags(new TagKey[]{ItemTags.f_271388_, ItemTags.f_271207_, Tags.Items.TOOLS_TRIDENTS});
        this.m_206424_(PSTTags.Items.RANGED_WEAPON).addTags(new TagKey[]{Tags.Items.TOOLS_BOWS, Tags.Items.TOOLS_CROSSBOWS});
        this.m_206424_(PSTTags.Items.LEATHER_ARMOR).m_255179_((Object[])new Item[]{Items.f_42463_, Items.f_42408_, Items.f_42407_, Items.f_42463_});
    }
}

