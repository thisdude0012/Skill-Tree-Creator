/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.enchantment.EnchantmentCategory
 */
package daripher.skilltree.skill.bonus.condition.enchantment;

import daripher.skilltree.init.PSTRegistries;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public interface EnchantmentCondition {
    public boolean met(EnchantmentCategory var1);

    default public String getDescriptionId() {
        ResourceLocation id = PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey((Object)this.getSerializer());
        Objects.requireNonNull(id);
        return "enchantment_condition.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
    }

    public Serializer getSerializer();

    public static interface Serializer
    extends daripher.skilltree.data.serializers.Serializer<EnchantmentCondition> {
        public EnchantmentCondition createDefaultInstance();
    }
}

