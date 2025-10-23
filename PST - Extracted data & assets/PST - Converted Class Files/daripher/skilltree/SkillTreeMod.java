/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package daripher.skilltree;

import daripher.skilltree.compat.attributeslib.AttributesLibCompatibility;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.init.PSTAttributes;
import daripher.skilltree.init.PSTCreativeTabs;
import daripher.skilltree.init.PSTDamageConditions;
import daripher.skilltree.init.PSTEnchantmentConditions;
import daripher.skilltree.init.PSTEventListeners;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.init.PSTItems;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.init.PSTLootModifiers;
import daripher.skilltree.init.PSTMobEffects;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.init.PSTPotions;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.init.PSTSkillRequirements;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value="skilltree")
public class SkillTreeMod {
    public static final String MOD_ID = "skilltree";
    public static final Logger LOGGER = LogManager.getLogger((String)"skilltree");

    public SkillTreeMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PSTItems.REGISTRY.register(eventBus);
        PSTAttributes.REGISTRY.register(eventBus);
        PSTMobEffects.REGISTRY.register(eventBus);
        PSTCreativeTabs.REGISTRY.register(eventBus);
        PSTSkillBonuses.REGISTRY.register(eventBus);
        PSTLivingConditions.REGISTRY.register(eventBus);
        PSTLivingMultipliers.REGISTRY.register(eventBus);
        PSTDamageConditions.REGISTRY.register(eventBus);
        PSTItemConditions.REGISTRY.register(eventBus);
        PSTEnchantmentConditions.REGISTRY.register(eventBus);
        PSTEventListeners.REGISTRY.register(eventBus);
        PSTLootModifiers.REGISTRY.register(eventBus);
        PSTNumericValueProviders.REGISTRY.register(eventBus);
        PSTPotions.REGISTRY.register(eventBus);
        PSTSkillRequirements.REGISTRY.register(eventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, (IConfigSpec)ServerConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)ClientConfig.SPEC);
        this.addCompatibilities();
    }

    protected void addCompatibilities() {
        if (ModList.get().isLoaded("attributeslib")) {
            AttributesLibCompatibility.INSTANCE.register();
        }
    }
}

