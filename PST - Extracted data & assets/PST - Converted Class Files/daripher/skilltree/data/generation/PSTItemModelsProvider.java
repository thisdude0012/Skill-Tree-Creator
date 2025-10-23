/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.DataGenerator
 *  net.minecraftforge.client.model.generators.ItemModelProvider
 *  net.minecraftforge.common.data.ExistingFileHelper
 *  net.minecraftforge.registries.RegistryObject
 */
package daripher.skilltree.data.generation;

import daripher.skilltree.init.PSTItems;
import java.util.Collection;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class PSTItemModelsProvider
extends ItemModelProvider {
    public PSTItemModelsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
        super(dataGenerator.getPackOutput(), "skilltree", existingFileHelper);
    }

    protected void registerModels() {
        Collection items = PSTItems.REGISTRY.getEntries();
        items.stream().map(RegistryObject::get).forEach(arg_0 -> ((PSTItemModelsProvider)this).basicItem(arg_0));
    }
}

