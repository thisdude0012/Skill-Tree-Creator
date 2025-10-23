/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.item.Item
 *  net.minecraftforge.common.data.LanguageProvider
 */
package daripher.skilltree.data.generation.translation;

import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import java.util.Arrays;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class PSTTranslationProvider
extends LanguageProvider {
    public PSTTranslationProvider(DataGenerator dataGenerator, String modId, String locale) {
        super(dataGenerator.getPackOutput(), modId, locale);
    }

    protected void addTooltip(Item item, String tooltip) {
        this.add(item.m_5524_() + ".tooltip", tooltip);
    }

    protected void addWarning(Item item, String tooltip) {
        this.add(item.m_5524_() + ".warning", tooltip);
    }

    protected void add(Attribute attribute, String name) {
        this.add(attribute.m_22087_(), name);
    }

    protected void addCurioSlot(String slotName, String name) {
        this.add("curio.slot.%s".formatted(new Object[]{slotName}), name);
    }

    protected void addCurioSlot(String slotName, String type, String name) {
        this.add("curio.slot.%s.%s".formatted(new Object[]{slotName, type}), name);
    }

    protected void addSkill(String skillTree, int skillId, String name) {
        this.add("skill.skilltree.%s_%d.name".formatted(new Object[]{skillTree, skillId}), name);
    }

    protected void addSkills(String skillTree, int skillId1, int skillId2, int skillId3, String name) {
        this.addSkill(skillTree, skillId1, name);
        this.addSkill(skillTree, skillId2, name);
        this.addSkill(skillTree, skillId3, name);
    }

    protected void addMixture(String name, String potionType, MobEffect ... effects) {
        StringBuilder potionName = new StringBuilder("item.minecraft." + potionType + ".mixture");
        Arrays.stream(effects).map(MobEffect::m_19481_).map(id -> id.replaceAll("effect.", "")).forEach(id -> potionName.append(".").append((String)id));
        this.add(potionName.toString(), name);
    }

    protected void add(LivingCondition.Serializer condition, String value) {
        ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey((Object)condition);
        assert (id != null);
        String key = "living_condition.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
        this.add(key, value);
    }

    protected void add(LivingCondition.Serializer condition, String type, String value) {
        ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey((Object)condition);
        assert (id != null);
        String key = "living_condition.%s.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_(), type});
        this.add(key, value);
    }

    protected void add(SkillEventListener.Serializer condition, String value) {
        ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey((Object)condition);
        assert (id != null);
        String key = "event_listener.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
        this.add(key, value);
    }

    protected void add(SkillEventListener.Serializer condition, String type, String value) {
        ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey((Object)condition);
        assert (id != null);
        String key = "event_listener.%s.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_(), type});
        this.add(key, value);
    }

    protected void add(DamageCondition.Serializer condition, String type, String value) {
        ResourceLocation id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey((Object)condition);
        assert (id != null);
        String key = "damage_condition.%s.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_(), type});
        this.add(key, value);
    }

    protected void add(DamageCondition.Serializer condition, String value) {
        ResourceLocation id = PSTRegistries.DAMAGE_CONDITIONS.get().getKey((Object)condition);
        assert (id != null);
        String key = "damage_condition.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
        this.add(key, value);
    }

    protected void add(LivingMultiplier.Serializer multiplier, String value) {
        ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey((Object)multiplier);
        assert (id != null);
        String key = "skill_bonus_multiplier.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
        this.add(key, value);
    }

    protected void add(LivingMultiplier.Serializer multiplier, String type, String value) {
        ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey((Object)multiplier);
        assert (id != null);
        String key = "skill_bonus_multiplier.%s.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_(), type});
        this.add(key, value);
    }

    protected void add(NumericValueProvider.Serializer provider, String value) {
        ResourceLocation id = PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey((Object)provider);
        assert (id != null);
        String key = "value_provider.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
        this.add(key, value);
    }

    protected void add(NumericValueProvider.Serializer provider, String type, String value) {
        ResourceLocation id = PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey((Object)provider);
        assert (id != null);
        String key = "value_provider.%s.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_(), type});
        this.add(key, value);
    }

    protected void add(SkillBonus.Serializer serializer, String value) {
        ResourceLocation id = PSTRegistries.SKILL_BONUSES.get().getKey((Object)serializer);
        assert (id != null);
        String key = "skill_bonus.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
        this.add(key, value);
    }

    protected void add(SkillBonus.Serializer serializer, String type, String value) {
        ResourceLocation id = PSTRegistries.SKILL_BONUSES.get().getKey((Object)serializer);
        assert (id != null);
        String key = "skill_bonus.%s.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_(), type});
        this.add(key, value);
    }

    protected void add(ItemCondition.Serializer serializer, String type, String value) {
        ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey((Object)serializer);
        assert (id != null);
        String key = "item_condition.%s.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_(), type});
        this.add(key, value);
    }

    protected void add(ItemCondition.Serializer serializer, String value) {
        ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey((Object)serializer);
        assert (id != null);
        String key = "item_condition.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
        this.add(key, value);
    }

    protected void add(TagKey<Item> itemTag, String value) {
        ResourceLocation id = itemTag.f_203868_();
        String key = "item_tag.%s".formatted(new Object[]{id.toString()});
        this.add(key, value);
    }

    protected void add(TagKey<Item> itemTag, String type, String value) {
        ResourceLocation id = itemTag.f_203868_();
        String key = "item_tag.%s.%s".formatted(new Object[]{id.toString(), type});
        this.add(key, value);
    }

    protected void add(EnchantmentCondition.Serializer serializer, String value) {
        ResourceLocation id = PSTRegistries.ENCHANTMENT_CONDITIONS.get().getKey((Object)serializer);
        assert (id != null);
        String key = "enchantment_condition.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
        this.add(key, value);
    }

    protected void addGem(String type, String name, String ... qualities) {
        for (int i = 0; i < qualities.length; ++i) {
            this.add("item.skilltree.gem.skilltree." + type + "_" + i, qualities[i] + " " + name);
            this.add("item.apotheosis.gem.skilltree:" + type + "_" + i, name);
        }
    }

    protected void deathMessage(String damageType, String deathMessage) {
        this.add("death.attack." + damageType, deathMessage);
    }
}

