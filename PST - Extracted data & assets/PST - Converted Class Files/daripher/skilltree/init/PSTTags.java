/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.item.Item
 */
package daripher.skilltree.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;

public class PSTTags {

    public static class Items {
        public static final TagKey<Item> RINGS = ItemTags.create((ResourceLocation)new ResourceLocation("curios", "ring"));
        public static final TagKey<Item> NECKLACES = ItemTags.create((ResourceLocation)new ResourceLocation("curios", "necklace"));
        public static final TagKey<Item> QUIVERS = ItemTags.create((ResourceLocation)new ResourceLocation("curios", "quiver"));
        public static final TagKey<Item> JEWELRY = ItemTags.create((ResourceLocation)new ResourceLocation("forge", "curios/jewelry"));
        public static final TagKey<Item> MELEE_WEAPON = ItemTags.create((ResourceLocation)new ResourceLocation("skilltree", "melee_weapon"));
        public static final TagKey<Item> RANGED_WEAPON = ItemTags.create((ResourceLocation)new ResourceLocation("skilltree", "ranged_weapon"));
        public static final TagKey<Item> LEATHER_ARMOR = ItemTags.create((ResourceLocation)new ResourceLocation("skilltree", "armors/leather"));
    }

    public static class DamageTypes {
        public static final TagKey<DamageType> IS_MAGIC = TagKey.m_203882_((ResourceKey)Registries.f_268580_, (ResourceLocation)new ResourceLocation("forge", "is_magic"));
    }
}

