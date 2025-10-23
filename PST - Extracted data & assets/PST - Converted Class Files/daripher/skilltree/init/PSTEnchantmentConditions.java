/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.RegistryObject
 */
package daripher.skilltree.init;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.condition.enchantment.ArmorEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.NoneEnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.WeaponEnchantmentCondition;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTEnchantmentConditions {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation("skilltree", "enchantment_conditions");
    public static final DeferredRegister<EnchantmentCondition.Serializer> REGISTRY = DeferredRegister.create((ResourceLocation)REGISTRY_ID, (String)"skilltree");
    public static final RegistryObject<EnchantmentCondition.Serializer> NONE = REGISTRY.register("none", NoneEnchantmentCondition.Serializer::new);
    public static final RegistryObject<EnchantmentCondition.Serializer> ARMOR = REGISTRY.register("armor", ArmorEnchantmentCondition.Serializer::new);
    public static final RegistryObject<EnchantmentCondition.Serializer> WEAPON = REGISTRY.register("weapon", WeaponEnchantmentCondition.Serializer::new);

    public static List<EnchantmentCondition> conditionsList() {
        return PSTRegistries.ENCHANTMENT_CONDITIONS.get().getValues().stream().map(EnchantmentCondition.Serializer::createDefaultInstance).toList();
    }

    public static String getName(EnchantmentCondition condition) {
        ResourceLocation id = PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey((Object)condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).m_135815_());
    }
}

