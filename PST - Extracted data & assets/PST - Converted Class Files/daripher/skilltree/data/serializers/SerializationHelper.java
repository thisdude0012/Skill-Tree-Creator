/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.data.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.serializers.Serializer;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.NoneDamageCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.NoneItemCondition;
import daripher.skilltree.skill.bonus.condition.item.PotionCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class SerializationHelper {
    @NotNull
    public static Attribute deserializeAttribute(JsonObject json) {
        ResourceLocation attributeId = new ResourceLocation(json.get("attribute").getAsString());
        Attribute attribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) {
            throw new RuntimeException("Attribute " + attributeId + " doesn't exist!");
        }
        return attribute;
    }

    public static void serializeAttribute(JsonObject json, Attribute attribute) {
        ResourceLocation attributeId = ForgeRegistries.ATTRIBUTES.getKey((Object)attribute);
        Objects.requireNonNull(attributeId);
        json.addProperty("attribute", attributeId.toString());
    }

    @NotNull
    public static AttributeModifier deserializeAttributeModifier(JsonObject json) {
        UUID id = UUID.fromString(json.get("id").getAsString());
        String name = json.get("name").getAsString();
        double amount = json.get("amount").getAsDouble();
        AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(json);
        return new AttributeModifier(id, name, amount, operation);
    }

    public static void serializeAttributeModifier(JsonObject json, AttributeModifier modifier) {
        json.addProperty("id", modifier.m_22209_().toString());
        json.addProperty("name", modifier.m_22214_());
        json.addProperty("amount", (Number)modifier.m_22218_());
        SerializationHelper.serializeOperation(json, modifier.m_22217_());
    }

    @NotNull
    public static AttributeModifier.Operation deserializeOperation(JsonObject json) {
        return AttributeModifier.Operation.m_22236_((int)json.get("operation").getAsInt());
    }

    public static void serializeOperation(JsonObject json, AttributeModifier.Operation operation) {
        json.addProperty("operation", (Number)operation.m_22235_());
    }

    @Nonnull
    public static LivingMultiplier deserializeLivingMultiplier(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneLivingMultiplier.INSTANCE;
        }
        JsonObject multiplierJson = json.getAsJsonObject(name);
        ResourceLocation serializerId = new ResourceLocation(multiplierJson.get("type").getAsString());
        LivingMultiplier.Serializer serializer = (LivingMultiplier.Serializer)PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
        String errorMessage = "Unknown living multiplier: " + serializerId;
        return SerializationHelper.deserializeObject(serializer, multiplierJson, errorMessage);
    }

    public static void serializeLivingMultiplier(JsonObject json, @Nonnull LivingMultiplier multiplier, String name) {
        JsonObject multiplierJson = new JsonObject();
        LivingMultiplier.Serializer serializer = multiplier.getSerializer();
        serializer.serialize(multiplierJson, multiplier);
        ResourceLocation serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey((Object)serializer);
        Objects.requireNonNull(serializerId);
        multiplierJson.addProperty("type", serializerId.toString());
        json.add(name, (JsonElement)multiplierJson);
    }

    @Nonnull
    public static LivingCondition deserializeLivingCondition(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneLivingCondition.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        ResourceLocation serializerId = new ResourceLocation(conditionJson.get("type").getAsString());
        LivingCondition.Serializer serializer = (LivingCondition.Serializer)PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
        String errorMessage = "Unknown living condition: " + serializerId;
        return SerializationHelper.deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeLivingCondition(JsonObject json, @Nonnull LivingCondition condition, String name) {
        JsonObject conditionJson = new JsonObject();
        LivingCondition.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey((Object)serializer);
        Objects.requireNonNull(serializerId);
        conditionJson.addProperty("type", serializerId.toString());
        json.add(name, (JsonElement)conditionJson);
    }

    @Nonnull
    public static DamageCondition deserializeDamageCondition(JsonObject json) {
        return SerializationHelper.deserializeDamageCondition(json, "damage_condition");
    }

    @Nonnull
    public static DamageCondition deserializeDamageCondition(JsonObject json, String name) {
        if (!json.has(name)) {
            return NoneDamageCondition.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        ResourceLocation serializerId = new ResourceLocation(conditionJson.get("type").getAsString());
        DamageCondition.Serializer serializer = (DamageCondition.Serializer)PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
        String errorMessage = "Unknown damage condition: " + serializerId;
        return SerializationHelper.deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeDamageCondition(JsonObject json, @Nonnull DamageCondition condition) {
        SerializationHelper.serializeDamageCondition(json, condition, "damage_condition");
    }

    public static void serializeDamageCondition(JsonObject json, @Nonnull DamageCondition condition, String name) {
        JsonObject conditionJson = new JsonObject();
        DamageCondition.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        ResourceLocation serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey((Object)serializer);
        conditionJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add(name, (JsonElement)conditionJson);
    }

    @Nonnull
    public static ItemCondition deserializeItemCondition(JsonObject json) {
        String name = "item_condition";
        if (!json.has(name)) {
            return NoneItemCondition.INSTANCE;
        }
        JsonObject conditionJson = json.getAsJsonObject(name);
        ResourceLocation serializerId = new ResourceLocation(conditionJson.get("type").getAsString());
        ItemCondition.Serializer serializer = (ItemCondition.Serializer)PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
        String errorMessage = "Unknown item condition: " + serializerId;
        return SerializationHelper.deserializeObject(serializer, conditionJson, errorMessage);
    }

    public static void serializeItemCondition(JsonObject json, @Nonnull ItemCondition condition) {
        JsonObject conditionJson = new JsonObject();
        ItemCondition.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey((Object)serializer);
        conditionJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add("item_condition", (JsonElement)conditionJson);
    }

    @Nonnull
    public static SkillEventListener deserializeEventListener(JsonObject json) {
        JsonObject eventJson = json.getAsJsonObject("event_listener");
        ResourceLocation serializerId = new ResourceLocation(eventJson.get("type").getAsString());
        SkillEventListener.Serializer serializer = (SkillEventListener.Serializer)PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
        String errorMessage = "Unknown event listener: " + serializerId;
        return SerializationHelper.deserializeObject(serializer, eventJson, errorMessage);
    }

    public static void serializeEventListener(JsonObject json, @Nonnull SkillEventListener condition) {
        JsonObject conditionJson = new JsonObject();
        SkillEventListener.Serializer serializer = condition.getSerializer();
        serializer.serialize(conditionJson, condition);
        ResourceLocation serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey((Object)serializer);
        conditionJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add("event_listener", (JsonElement)conditionJson);
    }

    @Nullable
    public static MobEffect deserializeEffect(JsonObject json) {
        if (!json.has("effect")) {
            return null;
        }
        ResourceLocation effectId = new ResourceLocation(json.get("effect").getAsString());
        return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(effectId);
    }

    public static void serializeEffect(JsonObject json, MobEffect effect) {
        ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey((Object)effect);
        json.addProperty("effect", Objects.requireNonNull(effectId).toString());
    }

    @Nullable
    public static PotionCondition.Type deserializePotionType(JsonObject json) {
        return PotionCondition.Type.byName(json.get("potion_type").getAsString());
    }

    public static void serializePotionType(JsonObject json, PotionCondition.Type type) {
        json.addProperty("potion_type", type.getName());
    }

    public static MobEffectInstance deserializeEffectInstance(JsonObject json) {
        MobEffect effect = SerializationHelper.deserializeEffect(json);
        int duration = json.get("duration").getAsInt();
        int amplifier = json.get("amplifier").getAsInt();
        return new MobEffectInstance(Objects.requireNonNull(effect), duration, amplifier);
    }

    public static void serializeEffectInstance(JsonObject json, MobEffectInstance effect) {
        SerializationHelper.serializeEffect(json, effect.m_19544_());
        json.addProperty("duration", (Number)effect.m_19557_());
        json.addProperty("amplifier", (Number)effect.m_19564_());
    }

    public static NumericValueProvider<?> deserializeValueProvider(JsonObject json) {
        JsonObject providerJson = json.getAsJsonObject("value_provider");
        String type = providerJson.get("type").getAsString();
        ResourceLocation serializerId = new ResourceLocation(type);
        NumericValueProvider.Serializer serializer = (NumericValueProvider.Serializer)PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getValue(serializerId);
        String errorMessage = "Unknown value provider: " + serializerId;
        return (NumericValueProvider)SerializationHelper.deserializeObject(serializer, providerJson, errorMessage);
    }

    public static void serializeValueProvider(JsonObject json, NumericValueProvider<?> provider) {
        ResourceLocation serializerId = PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey((Object)provider.getSerializer());
        JsonObject bonusJson = new JsonObject();
        provider.getSerializer().serialize(bonusJson, provider);
        bonusJson.addProperty("type", Objects.requireNonNull(serializerId).toString());
        json.add("value_provider", (JsonElement)bonusJson);
    }

    @Nullable
    public static Attribute deserializeAttribute(CompoundTag tag) {
        ResourceLocation attributeId = new ResourceLocation(tag.m_128461_("attribute"));
        Attribute attribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) {
            SkillTreeMod.LOGGER.error("Attribute {} doesn't exist!", (Object)attributeId);
        }
        return attribute;
    }

    public static void serializeAttribute(CompoundTag tag, Attribute attribute) {
        ResourceLocation attributeId = ForgeRegistries.ATTRIBUTES.getKey((Object)attribute);
        Objects.requireNonNull(attributeId);
        tag.m_128359_("attribute", attributeId.toString());
    }

    @NotNull
    public static AttributeModifier deserializeAttributeModifier(CompoundTag tag) {
        UUID modifierId = UUID.fromString(tag.m_128461_("id"));
        String name = tag.m_128461_("name");
        double amount = tag.m_128459_("amount");
        AttributeModifier.Operation operation = SerializationHelper.deserializeOperation(tag);
        return new AttributeModifier(modifierId, name, amount, operation);
    }

    public static void serializeAttributeModifier(CompoundTag tag, AttributeModifier modifier) {
        tag.m_128359_("id", modifier.m_22209_().toString());
        tag.m_128359_("name", modifier.m_22214_());
        tag.m_128347_("amount", modifier.m_22218_());
        SerializationHelper.serializeOperation(tag, modifier.m_22217_());
    }

    @NotNull
    public static AttributeModifier.Operation deserializeOperation(CompoundTag tag) {
        return AttributeModifier.Operation.m_22236_((int)tag.m_128451_("operation"));
    }

    public static void serializeOperation(CompoundTag tag, AttributeModifier.Operation operation) {
        tag.m_128405_("operation", operation.m_22235_());
    }

    @Nonnull
    public static LivingMultiplier deserializeLivingMultiplier(CompoundTag tag, String name) {
        if (!tag.m_128441_(name)) {
            return NoneLivingMultiplier.INSTANCE;
        }
        CompoundTag multiplierTag = tag.m_128469_(name);
        ResourceLocation serializerId = new ResourceLocation(multiplierTag.m_128461_("type"));
        LivingMultiplier.Serializer serializer = (LivingMultiplier.Serializer)PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
        return (LivingMultiplier)Objects.requireNonNull(serializer).deserialize(multiplierTag);
    }

    public static void serializeLivingMultiplier(CompoundTag tag, @Nonnull LivingMultiplier multiplier, String name) {
        LivingMultiplier.Serializer serializer = multiplier.getSerializer();
        CompoundTag multiplierTag = serializer.serialize(multiplier);
        ResourceLocation serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey((Object)serializer);
        multiplierTag.m_128359_("type", Objects.requireNonNull(serializerId).toString());
        tag.m_128365_(name, (Tag)multiplierTag);
    }

    @Nonnull
    public static LivingCondition deserializeLivingCondition(CompoundTag tag, String name) {
        CompoundTag conditionTag = tag.m_128469_(name);
        ResourceLocation serializerId = new ResourceLocation(conditionTag.m_128461_("type"));
        LivingCondition.Serializer serializer = (LivingCondition.Serializer)PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
        return (LivingCondition)Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeLivingCondition(CompoundTag tag, @Nonnull LivingCondition condition, String name) {
        LivingCondition.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey((Object)serializer);
        Objects.requireNonNull(serializerId);
        conditionTag.m_128359_("type", serializerId.toString());
        tag.m_128365_(name, (Tag)conditionTag);
    }

    @Nonnull
    public static DamageCondition deserializeDamageCondition(CompoundTag tag) {
        return SerializationHelper.deserializeDamageCondition(tag, "damage_condition");
    }

    @Nonnull
    public static DamageCondition deserializeDamageCondition(CompoundTag tag, String name) {
        CompoundTag conditionTag = tag.m_128469_(name);
        ResourceLocation serializerId = new ResourceLocation(conditionTag.m_128461_("type"));
        DamageCondition.Serializer serializer = (DamageCondition.Serializer)PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
        return (DamageCondition)Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeDamageCondition(CompoundTag tag, @Nonnull DamageCondition condition) {
        SerializationHelper.serializeDamageCondition(tag, condition, "damage_condition");
    }

    public static void serializeDamageCondition(CompoundTag tag, @Nonnull DamageCondition condition, String name) {
        DamageCondition.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        ResourceLocation serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey((Object)serializer);
        conditionTag.m_128359_("type", Objects.requireNonNull(serializerId).toString());
        tag.m_128365_(name, (Tag)conditionTag);
    }

    @Nonnull
    public static ItemCondition deserializeItemCondition(CompoundTag tag) {
        CompoundTag conditionTag = tag.m_128469_("item_condition");
        ResourceLocation serializerId = new ResourceLocation(conditionTag.m_128461_("type"));
        ItemCondition.Serializer serializer = (ItemCondition.Serializer)PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
        return (ItemCondition)Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeItemCondition(CompoundTag tag, @Nonnull ItemCondition condition) {
        ItemCondition.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey((Object)serializer);
        conditionTag.m_128359_("type", Objects.requireNonNull(serializerId).toString());
        tag.m_128365_("item_condition", (Tag)conditionTag);
    }

    @Nonnull
    public static SkillEventListener deserializeEventListener(CompoundTag tag) {
        CompoundTag conditionTag = tag.m_128469_("event_listener");
        ResourceLocation serializerId = new ResourceLocation(conditionTag.m_128461_("type"));
        SkillEventListener.Serializer serializer = (SkillEventListener.Serializer)PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
        return (SkillEventListener)Objects.requireNonNull(serializer).deserialize(conditionTag);
    }

    public static void serializeEventListener(CompoundTag tag, @Nonnull SkillEventListener condition) {
        SkillEventListener.Serializer serializer = condition.getSerializer();
        CompoundTag conditionTag = serializer.serialize(condition);
        ResourceLocation serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey((Object)serializer);
        conditionTag.m_128359_("type", Objects.requireNonNull(serializerId).toString());
        tag.m_128365_("event_listener", (Tag)conditionTag);
    }

    @Nullable
    public static MobEffect deserializeEffect(CompoundTag tag) {
        if (!tag.m_128441_("effect")) {
            return null;
        }
        ResourceLocation effectId = new ResourceLocation(tag.m_128461_("effect"));
        return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(effectId);
    }

    public static void serializeEffect(CompoundTag tag, MobEffect effect) {
        ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey((Object)effect);
        tag.m_128359_("effect", Objects.requireNonNull(effectId).toString());
    }

    public static PotionCondition.Type deserializePotionType(CompoundTag tag) {
        return PotionCondition.Type.byName(tag.m_128461_("potion_type"));
    }

    public static void serializePotionType(CompoundTag tag, PotionCondition.Type type) {
        tag.m_128359_("category", type.getName());
    }

    public static MobEffectInstance deserializeEffectInstance(CompoundTag tag) {
        MobEffect effect = Objects.requireNonNull(SerializationHelper.deserializeEffect(tag));
        int duration = tag.m_128451_("duration");
        int amplifier = tag.m_128451_("amplifier");
        return new MobEffectInstance(effect, duration, amplifier);
    }

    public static void serializeEffectInstance(CompoundTag tag, MobEffectInstance effect) {
        SerializationHelper.serializeEffect(tag, effect.m_19544_());
        tag.m_128405_("duration", effect.m_19557_());
        tag.m_128405_("amplifier", effect.m_19564_());
    }

    public static NumericValueProvider<?> deserializeValueProvider(CompoundTag tag) {
        CompoundTag providerTag = tag.m_128469_("value_provider");
        String type = providerTag.m_128461_("type");
        ResourceLocation serializerId = new ResourceLocation(type);
        NumericValueProvider.Serializer serializer = (NumericValueProvider.Serializer)PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getValue(serializerId);
        return (NumericValueProvider)Objects.requireNonNull(serializer).deserialize(providerTag);
    }

    public static void serializeValueProvider(CompoundTag tag, NumericValueProvider<?> provider) {
        ResourceLocation serializerId = PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey((Object)provider.getSerializer());
        CompoundTag providerTag = provider.getSerializer().serialize(provider);
        providerTag.m_128359_("type", Objects.requireNonNull(serializerId).toString());
        tag.m_128365_("value_provider", (Tag)providerTag);
    }

    private static <T> T deserializeObject(Serializer<T> serializer, JsonObject jsonObject, String errorMessage) {
        return Objects.requireNonNull(serializer, errorMessage).deserialize(jsonObject);
    }

    public static JsonElement getElement(JsonObject json, String name) {
        JsonElement element = json.get(name);
        return Objects.requireNonNull(element, "Element not found: " + name);
    }
}

