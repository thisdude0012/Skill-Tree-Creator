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
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NumericValueMultiplier;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTLivingMultipliers {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation("skilltree", "skill_bonus_multipliers");
    public static final DeferredRegister<LivingMultiplier.Serializer> REGISTRY = DeferredRegister.create((ResourceLocation)REGISTRY_ID, (String)"skilltree");
    public static final RegistryObject<LivingMultiplier.Serializer> NONE = REGISTRY.register("none", NoneLivingMultiplier.Serializer::new);
    public static final RegistryObject<LivingMultiplier.Serializer> NUMERIC_VALUE = REGISTRY.register("numeric_value", NumericValueMultiplier.Serializer::new);

    public static List<LivingMultiplier> multiplierList() {
        return PSTRegistries.LIVING_MULTIPLIERS.get().getValues().stream().map(LivingMultiplier.Serializer::createDefaultInstance).toList();
    }

    public static String getName(LivingMultiplier condition) {
        ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey((Object)condition.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).m_135815_());
    }
}

