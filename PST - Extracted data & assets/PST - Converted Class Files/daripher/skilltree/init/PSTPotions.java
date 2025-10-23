/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package daripher.skilltree.init;

import daripher.skilltree.init.PSTMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class PSTPotions {
    public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.POTIONS, (String)"skilltree");
    public static final RegistryObject<Potion> LIQUID_FIRE_1 = REGISTRY.register("liquid_fire_1", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance((MobEffect)PSTMobEffects.LIQUID_FIRE.get())}));
    public static final RegistryObject<Potion> LIQUID_FIRE_2 = REGISTRY.register("liquid_fire_2", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance((MobEffect)PSTMobEffects.LIQUID_FIRE.get(), 0, 1)}));
}

