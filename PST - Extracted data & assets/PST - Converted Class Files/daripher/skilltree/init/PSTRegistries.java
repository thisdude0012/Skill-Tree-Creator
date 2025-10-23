/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.NewRegistryEvent
 *  net.minecraftforge.registries.RegistryBuilder
 */
package daripher.skilltree.init;

import daripher.skilltree.init.PSTDamageConditions;
import daripher.skilltree.init.PSTEnchantmentConditions;
import daripher.skilltree.init.PSTEventListeners;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid="skilltree", bus=Mod.EventBusSubscriber.Bus.MOD)
public class PSTRegistries {
    public static final Supplier<IForgeRegistry<SkillBonus.Serializer>> SKILL_BONUSES = PSTSkillBonuses.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<LivingMultiplier.Serializer>> LIVING_MULTIPLIERS = PSTLivingMultipliers.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<LivingCondition.Serializer>> LIVING_CONDITIONS = PSTLivingConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<DamageCondition.Serializer>> DAMAGE_CONDITIONS = PSTDamageConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<ItemCondition.Serializer>> ITEM_CONDITIONS = PSTItemConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<EnchantmentCondition.Serializer>> ENCHANTMENT_CONDITIONS = PSTEnchantmentConditions.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<SkillEventListener.Serializer>> EVENT_LISTENERS = PSTEventListeners.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<NumericValueProvider.Serializer>> NUMERIC_VALUE_PROVIDERS = PSTNumericValueProviders.REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<SkillRequirement.Serializer>> SKILL_REQUIREMENTS = PSTSkillRequirements.REGISTRY.makeRegistry(RegistryBuilder::new);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        PSTRegistries.createRegistry(event, PSTSkillBonuses.REGISTRY_ID);
        PSTRegistries.createRegistry(event, PSTLivingMultipliers.REGISTRY_ID);
        PSTRegistries.createRegistry(event, PSTLivingConditions.REGISTRY_ID);
        PSTRegistries.createRegistry(event, PSTDamageConditions.REGISTRY_ID);
        PSTRegistries.createRegistry(event, PSTItemConditions.REGISTRY_ID);
        PSTRegistries.createRegistry(event, PSTEnchantmentConditions.REGISTRY_ID);
        PSTRegistries.createRegistry(event, PSTEventListeners.REGISTRY_ID);
        PSTRegistries.createRegistry(event, PSTNumericValueProviders.REGISTRY_ID);
        PSTRegistries.createRegistry(event, PSTSkillRequirements.REGISTRY_ID);
    }

    private static <T> void createRegistry(NewRegistryEvent event, ResourceLocation id) {
        event.create(new RegistryBuilder().setName(id));
    }
}

