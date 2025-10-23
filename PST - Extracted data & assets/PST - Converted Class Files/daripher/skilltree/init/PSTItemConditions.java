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
import daripher.skilltree.skill.bonus.condition.item.EnchantedCondition;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.item.FoodCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemIdCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemTagCondition;
import daripher.skilltree.skill.bonus.condition.item.NoneItemCondition;
import daripher.skilltree.skill.bonus.condition.item.PotionCondition;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTItemConditions {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation("skilltree", "item_conditions");
    public static final DeferredRegister<ItemCondition.Serializer> REGISTRY = DeferredRegister.create((ResourceLocation)REGISTRY_ID, (String)"skilltree");
    public static final RegistryObject<ItemCondition.Serializer> NONE = REGISTRY.register("none", NoneItemCondition.Serializer::new);
    public static final RegistryObject<ItemCondition.Serializer> POTIONS = REGISTRY.register("potion", PotionCondition.Serializer::new);
    public static final RegistryObject<ItemCondition.Serializer> FOOD = REGISTRY.register("food", FoodCondition.Serializer::new);
    public static final RegistryObject<ItemCondition.Serializer> ITEM_ID = REGISTRY.register("item_id", ItemIdCondition.Serializer::new);
    public static final RegistryObject<ItemCondition.Serializer> ENCHANTED = REGISTRY.register("enchanted", EnchantedCondition.Serializer::new);
    public static final RegistryObject<ItemCondition.Serializer> TAG = REGISTRY.register("tag", ItemTagCondition.Serializer::new);
    public static final RegistryObject<ItemCondition.Serializer> EQUIPMENT_TYPE = REGISTRY.register("equipment_type", EquipmentCondition.Serializer::new);

    public static List<ItemCondition> conditionsList() {
        return PSTRegistries.ITEM_CONDITIONS.get().getValues().stream().map(ItemCondition.Serializer::createDefaultInstance).toList();
    }

    public static String getName(ItemCondition condition) {
        ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey((Object)condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).m_135815_());
    }
}

