/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$Output
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.RegistryObject
 */
package daripher.skilltree.init;

import daripher.skilltree.init.PSTItems;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create((ResourceKey)Registries.f_279569_, (String)"skilltree");
    public static final MutableComponent TAB_TITLE = Component.m_237115_((String)"itemGroup.skilltree");
    public static final Supplier<ItemStack> TAB_ICON_STACK = () -> new ItemStack((ItemLike)PSTItems.AMNESIA_SCROLL.get());

    private static void collectModItems(CreativeModeTab.Output output) {
        PSTItems.REGISTRY.getEntries().stream().map(RegistryObject::get).forEach(arg_0 -> ((CreativeModeTab.Output)output).m_246326_(arg_0));
    }

    static {
        REGISTRY.register("skilltree", () -> CreativeModeTab.builder().m_257941_((Component)TAB_TITLE).m_257737_(TAB_ICON_STACK).m_257501_((params, output) -> PSTCreativeTabs.collectModItems(output)).m_257652_());
    }
}

