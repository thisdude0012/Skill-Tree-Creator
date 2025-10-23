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
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.AttributeValueProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.DistanceToTargetProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.EffectAmountProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.EnchantmentAmountProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.EnchantmentLevelsProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.EquipmentDurabilityProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.FoodLevelProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.HealthLevelProvider;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTNumericValueProviders {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation("skilltree", "numeric_value_providers");
    public static final DeferredRegister<NumericValueProvider.Serializer> REGISTRY = DeferredRegister.create((ResourceLocation)REGISTRY_ID, (String)"skilltree");
    public static final RegistryObject<NumericValueProvider.Serializer> ATTRIBUTE_VALUE = REGISTRY.register("attribute_value", AttributeValueProvider.Serializer::new);
    public static final RegistryObject<NumericValueProvider.Serializer> EFFECT_AMOUNT = REGISTRY.register("effect_amount", EffectAmountProvider.Serializer::new);
    public static final RegistryObject<NumericValueProvider.Serializer> FOOD_LEVEL = REGISTRY.register("food_level", FoodLevelProvider.Serializer::new);
    public static final RegistryObject<NumericValueProvider.Serializer> HEALTH_LEVEL = REGISTRY.register("health_level", HealthLevelProvider.Serializer::new);
    public static final RegistryObject<NumericValueProvider.Serializer> EQUIPMENT_DURABILITY = REGISTRY.register("equipment_durability", EquipmentDurabilityProvider.Serializer::new);
    public static final RegistryObject<NumericValueProvider.Serializer> ENCHANTMENT_AMOUNT = REGISTRY.register("enchantment_amount", EnchantmentAmountProvider.Serializer::new);
    public static final RegistryObject<NumericValueProvider.Serializer> ENCHANTMENT_LEVELS = REGISTRY.register("enchantment_levels", EnchantmentLevelsProvider.Serializer::new);
    public static final RegistryObject<NumericValueProvider.Serializer> DISTANCE_TO_TARGET = REGISTRY.register("distance_to_target", DistanceToTargetProvider.Serializer::new);

    public static List<NumericValueProvider> providerList() {
        return PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getValues().stream().map(NumericValueProvider.Serializer::createDefaultInstance).map(NumericValueProvider.class::cast).toList();
    }

    public static String getName(NumericValueProvider<?> provider) {
        ResourceLocation id = PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey((Object)provider.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).m_135815_());
    }
}

