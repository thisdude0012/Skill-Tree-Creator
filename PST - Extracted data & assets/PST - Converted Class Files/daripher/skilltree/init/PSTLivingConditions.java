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
import daripher.skilltree.skill.bonus.condition.living.AllArmorCondition;
import daripher.skilltree.skill.bonus.condition.living.BurningCondition;
import daripher.skilltree.skill.bonus.condition.living.CrouchingCondition;
import daripher.skilltree.skill.bonus.condition.living.DualWieldingCondition;
import daripher.skilltree.skill.bonus.condition.living.FishingCondition;
import daripher.skilltree.skill.bonus.condition.living.HasEffectCondition;
import daripher.skilltree.skill.bonus.condition.living.HasItemEquippedCondition;
import daripher.skilltree.skill.bonus.condition.living.HasItemInHandCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.condition.living.UnarmedCondition;
import daripher.skilltree.skill.bonus.condition.living.UnderwaterCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTLivingConditions {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation("skilltree", "living_conditions");
    public static final DeferredRegister<LivingCondition.Serializer> REGISTRY = DeferredRegister.create((ResourceLocation)REGISTRY_ID, (String)"skilltree");
    public static final RegistryObject<LivingCondition.Serializer> NONE = REGISTRY.register("none", NoneLivingCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> HAS_ITEM_EQUIPPED = REGISTRY.register("has_item_equipped", HasItemEquippedCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> HAS_EFFECT = REGISTRY.register("has_effect", HasEffectCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> BURNING = REGISTRY.register("burning", BurningCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> FISHING = REGISTRY.register("fishing", FishingCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> UNDERWATER = REGISTRY.register("underwater", UnderwaterCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> DUAL_WIELDING = REGISTRY.register("dual_wielding", DualWieldingCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> HAS_ITEM_IN_HAND = REGISTRY.register("has_item_in_hand", HasItemInHandCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> CROUCHING = REGISTRY.register("crouching", CrouchingCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> UNARMED = REGISTRY.register("unarmed", UnarmedCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> NUMERIC_VALUE = REGISTRY.register("numeric_value", NumericValueCondition.Serializer::new);
    public static final RegistryObject<LivingCondition.Serializer> ALL_ARMOR = REGISTRY.register("all_armor", AllArmorCondition.Serializer::new);

    public static List<LivingCondition> conditionsList() {
        return PSTRegistries.LIVING_CONDITIONS.get().getValues().stream().map(LivingCondition.Serializer::createDefaultInstance).toList();
    }

    public static String getName(LivingCondition condition) {
        ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey((Object)condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).m_135815_());
    }
}

