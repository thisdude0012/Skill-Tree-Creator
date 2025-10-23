/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package daripher.skilltree.init;

import daripher.skilltree.item.AmnesiaScrollItem;
import daripher.skilltree.item.WisdomScrollItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class PSTItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"skilltree");
    public static final RegistryObject<Item> WISDOM_SCROLL = REGISTRY.register("wisdom_scroll", WisdomScrollItem::new);
    public static final RegistryObject<Item> AMNESIA_SCROLL = REGISTRY.register("amnesia_scroll", AmnesiaScrollItem::new);
}

