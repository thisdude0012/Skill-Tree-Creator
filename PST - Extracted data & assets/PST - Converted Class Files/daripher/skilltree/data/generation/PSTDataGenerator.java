/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraftforge.common.data.ExistingFileHelper
 *  net.minecraftforge.data.event.GatherDataEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package daripher.skilltree.data.generation;

import daripher.skilltree.data.generation.PSTBlockTagsProvider;
import daripher.skilltree.data.generation.PSTDamageTagsProvider;
import daripher.skilltree.data.generation.PSTGlobalLootModifierProvider;
import daripher.skilltree.data.generation.PSTItemModelsProvider;
import daripher.skilltree.data.generation.PSTItemTagsProvider;
import daripher.skilltree.data.generation.loot.PSTLootTablesProvider;
import daripher.skilltree.data.generation.translation.PSTEnglishTranslationProvider;
import daripher.skilltree.data.generation.translation.PSTRussianTranslationProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="skilltree", bus=Mod.EventBusSubscriber.Bus.MOD)
public class PSTDataGenerator {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator dataGenerator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture lookupProvider = event.getLookupProvider();
        boolean includeServer = event.includeServer();
        PSTBlockTagsProvider blockTagsProvider = new PSTBlockTagsProvider(dataGenerator, lookupProvider, existingFileHelper);
        dataGenerator.addProvider(includeServer, (DataProvider)blockTagsProvider);
        dataGenerator.addProvider(includeServer, (DataProvider)new PSTItemTagsProvider(dataGenerator, lookupProvider, blockTagsProvider, existingFileHelper));
        dataGenerator.addProvider(includeServer, (DataProvider)new PSTLootTablesProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, (DataProvider)new PSTGlobalLootModifierProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, (DataProvider)new PSTDamageTagsProvider(dataGenerator, lookupProvider, existingFileHelper));
        boolean includeClient = event.includeClient();
        dataGenerator.addProvider(includeClient, (DataProvider)new PSTEnglishTranslationProvider(dataGenerator));
        dataGenerator.addProvider(includeClient, (DataProvider)new PSTRussianTranslationProvider(dataGenerator));
        dataGenerator.addProvider(includeClient, (DataProvider)new PSTItemModelsProvider(dataGenerator, existingFileHelper));
    }
}

