/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraftforge.common.loot.IGlobalLootModifier
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries$Keys
 */
package daripher.skilltree.init;

import com.mojang.serialization.Codec;
import daripher.skilltree.loot.modifier.AddItemModifier;
import daripher.skilltree.loot.modifier.SkillBonusesModifier;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PSTLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create((ResourceKey)ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, (String)"skilltree");

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }

    static {
        REGISTRY.register("add_item", AddItemModifier.CODEC);
        REGISTRY.register("skill_bonuses", SkillBonusesModifier.CODEC);
    }
}

