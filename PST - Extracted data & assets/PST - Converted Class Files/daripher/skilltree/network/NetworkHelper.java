/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.chat.TextColor
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.network;

import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class NetworkHelper {
    public static void writePassiveSkill(FriendlyByteBuf buf, PassiveSkill skill) {
        buf.m_130070_(skill.getId().toString());
        buf.writeInt(skill.getSkillSize());
        buf.m_130070_(skill.getFrameTexture().toString());
        buf.m_130070_(skill.getIconTexture().toString());
        buf.m_130070_(skill.getTooltipFrameTexture().toString());
        buf.writeBoolean(skill.isStartingPoint());
        buf.writeFloat(skill.getPositionX());
        buf.writeFloat(skill.getPositionY());
        buf.m_130070_(skill.getTitle());
        buf.m_130070_(skill.getTitleColor());
        NetworkHelper.writeResourceLocations(buf, skill.getDirectConnections());
        NetworkHelper.writeNullableResourceLocation(buf, skill.getConnectedTreeId());
        NetworkHelper.writeSkillBonuses(buf, skill.getBonuses());
        NetworkHelper.writeSkillRequirements(buf, skill.getRequirements());
        NetworkHelper.writeResourceLocations(buf, skill.getLongConnections());
        NetworkHelper.writeResourceLocations(buf, skill.getOneWayConnections());
        NetworkHelper.writeTags(buf, skill.getTags());
        NetworkHelper.writeDescription(buf, skill.getDescription());
    }

    public static PassiveSkill readPassiveSkill(FriendlyByteBuf buf) {
        ResourceLocation id = new ResourceLocation(buf.m_130277_());
        int size = buf.readInt();
        ResourceLocation background = new ResourceLocation(buf.m_130277_());
        ResourceLocation icon = new ResourceLocation(buf.m_130277_());
        ResourceLocation border = new ResourceLocation(buf.m_130277_());
        boolean startingPoint = buf.readBoolean();
        PassiveSkill skill = new PassiveSkill(id, size, background, icon, border, startingPoint);
        skill.setPosition(buf.readFloat(), buf.readFloat());
        skill.setTitle(buf.m_130277_());
        skill.setTitleColor(buf.m_130277_());
        skill.getDirectConnections().addAll(NetworkHelper.readResourceLocations(buf));
        skill.setConnectedTree(NetworkHelper.readNullableResourceLocation(buf));
        skill.getBonuses().addAll(NetworkHelper.readSkillBonuses(buf));
        skill.getRequirements().addAll(NetworkHelper.readSkillRequirements(buf));
        skill.getLongConnections().addAll(NetworkHelper.readResourceLocations(buf));
        skill.getOneWayConnections().addAll(NetworkHelper.readResourceLocations(buf));
        skill.getTags().addAll(NetworkHelper.readTags(buf));
        skill.setDescription(NetworkHelper.readDescription(buf));
        return skill;
    }

    public static void writeAttribute(FriendlyByteBuf buf, Attribute attribute) {
        String attributeId = Objects.requireNonNull(ForgeRegistries.ATTRIBUTES.getKey((Object)attribute)).toString();
        buf.m_130070_(attributeId);
    }

    @Nullable
    public static Attribute readAttribute(FriendlyByteBuf buf) {
        String attributeId = buf.m_130277_();
        Attribute attribute = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeId));
        if (attribute == null) {
            SkillTreeMod.LOGGER.error("Attribute {} does not exist", (Object)attributeId);
        }
        return attribute;
    }

    public static void writeAttributeModifier(FriendlyByteBuf buf, AttributeModifier modifier) {
        buf.writeLong(modifier.m_22209_().getMostSignificantBits());
        buf.writeLong(modifier.m_22209_().getLeastSignificantBits());
        buf.m_130070_(modifier.m_22214_());
        buf.writeDouble(modifier.m_22218_());
        NetworkHelper.writeOperation(buf, modifier.m_22217_());
    }

    @Nonnull
    public static AttributeModifier readAttributeModifier(FriendlyByteBuf buf) {
        UUID id = new UUID(buf.readLong(), buf.readLong());
        String name = buf.m_130277_();
        double amount = buf.readDouble();
        AttributeModifier.Operation operation = NetworkHelper.readOperation(buf);
        return new AttributeModifier(id, name, amount, operation);
    }

    public static void writeNullableResourceLocation(FriendlyByteBuf buf, @Nullable ResourceLocation location) {
        buf.writeBoolean(location != null);
        if (location != null) {
            buf.m_130070_(location.toString());
        }
    }

    @Nullable
    public static ResourceLocation readNullableResourceLocation(FriendlyByteBuf buf) {
        return buf.readBoolean() ? new ResourceLocation(buf.m_130277_()) : null;
    }

    public static void writeResourceLocations(FriendlyByteBuf buf, List<ResourceLocation> locations) {
        buf.writeInt(locations.size());
        locations.forEach(location -> buf.m_130070_(location.toString()));
    }

    public static List<ResourceLocation> readResourceLocations(FriendlyByteBuf buf) {
        int count = buf.readInt();
        ArrayList<ResourceLocation> locations = new ArrayList<ResourceLocation>();
        for (int i = 0; i < count; ++i) {
            locations.add(new ResourceLocation(buf.m_130277_()));
        }
        return locations;
    }

    private static void writeTags(FriendlyByteBuf buf, List<String> tags) {
        buf.writeInt(tags.size());
        for (String tag : tags) {
            buf.m_130070_(tag);
        }
    }

    private static List<String> readTags(FriendlyByteBuf buf) {
        ArrayList<String> tags = new ArrayList<String>();
        int size = buf.readInt();
        for (int i = 0; i < size; ++i) {
            tags.add(buf.m_130277_());
        }
        return tags;
    }

    public static void writeSkillBonuses(FriendlyByteBuf buf, List<SkillBonus<?>> bonuses) {
        buf.writeInt(bonuses.size());
        bonuses.forEach(bonus -> NetworkHelper.writeSkillBonus(buf, bonus));
    }

    public static List<SkillBonus<?>> readSkillBonuses(FriendlyByteBuf buf) {
        int count = buf.readInt();
        ArrayList bonuses = new ArrayList();
        for (int i = 0; i < count; ++i) {
            bonuses.add(NetworkHelper.readSkillBonus(buf));
        }
        return bonuses;
    }

    public static void writeSkillRequirements(FriendlyByteBuf buf, List<SkillRequirement<?>> requirements) {
        buf.writeInt(requirements.size());
        requirements.forEach(requirement -> NetworkHelper.writeSkillRequirement(buf, requirement));
    }

    public static List<SkillRequirement<?>> readSkillRequirements(FriendlyByteBuf buf) {
        int count = buf.readInt();
        ArrayList requirements = new ArrayList();
        for (int i = 0; i < count; ++i) {
            requirements.add(NetworkHelper.readSkillRequirement(buf));
        }
        return requirements;
    }

    public static void writePassiveSkills(FriendlyByteBuf buf, Collection<PassiveSkill> skills) {
        buf.writeInt(skills.size());
        skills.forEach(skill -> NetworkHelper.writePassiveSkill(buf, skill));
    }

    public static List<PassiveSkill> readPassiveSkills(FriendlyByteBuf buf) {
        int count = buf.readInt();
        ArrayList<PassiveSkill> skills = new ArrayList<PassiveSkill>();
        for (int i = 0; i < count; ++i) {
            skills.add(NetworkHelper.readPassiveSkill(buf));
        }
        return skills;
    }

    public static void writeSkillBonus(FriendlyByteBuf buf, SkillBonus<?> bonus) {
        SkillBonus.Serializer serializer = bonus.getSerializer();
        ResourceLocation serializerId = PSTRegistries.SKILL_BONUSES.get().getKey((Object)serializer);
        Objects.requireNonNull(serializerId);
        buf.m_130070_(serializerId.toString());
        serializer.serialize(buf, bonus);
    }

    public static SkillBonus<?> readSkillBonus(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.m_130277_());
        SkillBonus.Serializer serializer = (SkillBonus.Serializer)PSTRegistries.SKILL_BONUSES.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return (SkillBonus)serializer.deserialize(buf);
    }

    public static void writeSkillRequirement(FriendlyByteBuf buf, SkillRequirement<?> requirement) {
        SkillRequirement.Serializer serializer = requirement.getSerializer();
        ResourceLocation serializerId = PSTRegistries.SKILL_REQUIREMENTS.get().getKey((Object)serializer);
        Objects.requireNonNull(serializerId);
        buf.m_130070_(serializerId.toString());
        serializer.serialize(buf, requirement);
    }

    public static SkillRequirement<?> readSkillRequirement(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.m_130277_());
        SkillRequirement.Serializer serializer = (SkillRequirement.Serializer)PSTRegistries.SKILL_REQUIREMENTS.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return (SkillRequirement)serializer.deserialize(buf);
    }

    public static void writeDescription(FriendlyByteBuf buf, @Nullable List<MutableComponent> description) {
        buf.writeBoolean(description != null);
        if (description == null) {
            return;
        }
        buf.writeInt(description.size());
        for (MutableComponent component : description) {
            NetworkHelper.writeChatComponent(buf, component);
        }
    }

    @Nullable
    public static List<MutableComponent> readDescription(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return null;
        }
        int size = buf.readInt();
        ArrayList<MutableComponent> description = new ArrayList<MutableComponent>();
        for (int i = 0; i < size; ++i) {
            description.add(NetworkHelper.readChatComponent(buf));
        }
        return description;
    }

    public static void writeChatComponent(FriendlyByteBuf buf, MutableComponent component) {
        buf.m_130070_(component.getString());
        Style style = component.m_7383_();
        buf.writeBoolean(style.m_131154_());
        buf.writeBoolean(style.m_131161_());
        buf.writeBoolean(style.m_131171_());
        buf.writeBoolean(style.m_131168_());
        buf.writeBoolean(style.m_131176_());
        TextColor textColor = style.m_131135_();
        buf.writeInt(textColor == null ? -1 : textColor.m_131265_());
    }

    public static MutableComponent readChatComponent(FriendlyByteBuf buf) {
        String text = buf.m_130277_();
        Style style = Style.f_131099_.m_131136_(Boolean.valueOf(buf.readBoolean())).m_131155_(Boolean.valueOf(buf.readBoolean())).m_131162_(Boolean.valueOf(buf.readBoolean())).m_178522_(Boolean.valueOf(buf.readBoolean())).m_178524_(Boolean.valueOf(buf.readBoolean()));
        int color = buf.readInt();
        if (color != -1) {
            style = style.m_178520_(color);
        }
        return Component.m_237113_((String)text).m_130948_(style);
    }

    public static void writePassiveSkillTrees(FriendlyByteBuf buf, Collection<PassiveSkillTree> skillTrees) {
        buf.writeInt(skillTrees.size());
        skillTrees.forEach(skillTree -> NetworkHelper.writePassiveSkillTree(buf, skillTree));
    }

    public static List<PassiveSkillTree> readPassiveSkillTrees(FriendlyByteBuf buf) {
        int count = buf.readInt();
        ArrayList<PassiveSkillTree> skillTrees = new ArrayList<PassiveSkillTree>();
        for (int i = 0; i < count; ++i) {
            skillTrees.add(NetworkHelper.readPassiveSkillTree(buf));
        }
        return skillTrees;
    }

    public static void writePassiveSkillTree(FriendlyByteBuf buf, PassiveSkillTree skillTree) {
        buf.m_130070_(skillTree.getId().toString());
        NetworkHelper.writeResourceLocations(buf, skillTree.getSkillIds());
        NetworkHelper.writeTagLimits(buf, skillTree.getSkillLimitations());
    }

    public static PassiveSkillTree readPassiveSkillTree(FriendlyByteBuf buf) {
        ResourceLocation id = new ResourceLocation(buf.m_130277_());
        PassiveSkillTree skillTree = new PassiveSkillTree(id);
        NetworkHelper.readResourceLocations(buf).forEach(skillTree.getSkillIds()::add);
        NetworkHelper.readTagLimits(buf).forEach(skillTree.getSkillLimitations()::put);
        return skillTree;
    }

    private static void writeTagLimits(FriendlyByteBuf buf, Map<String, Integer> limits) {
        buf.writeInt(limits.size());
        for (Map.Entry<String, Integer> entry : limits.entrySet()) {
            buf.m_130070_(entry.getKey());
            buf.writeInt(entry.getValue().intValue());
        }
    }

    private static Map<String, Integer> readTagLimits(FriendlyByteBuf buf) {
        HashMap<String, Integer> limits = new HashMap<String, Integer>();
        int size = buf.readInt();
        for (int i = 0; i < size; ++i) {
            limits.put(buf.m_130277_(), buf.readInt());
        }
        return limits;
    }

    public static void writeLivingMultiplier(FriendlyByteBuf buf, @Nonnull LivingMultiplier multiplier) {
        LivingMultiplier.Serializer serializer = multiplier.getSerializer();
        ResourceLocation serializerId = PSTRegistries.LIVING_MULTIPLIERS.get().getKey((Object)serializer);
        buf.m_130070_(Objects.requireNonNull(serializerId).toString());
        serializer.serialize(buf, multiplier);
    }

    @Nonnull
    public static LivingMultiplier readLivingMultiplier(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.m_130277_());
        LivingMultiplier.Serializer serializer = (LivingMultiplier.Serializer)PSTRegistries.LIVING_MULTIPLIERS.get().getValue(serializerId);
        return (LivingMultiplier)Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeLivingCondition(FriendlyByteBuf buf, @Nonnull LivingCondition condition) {
        LivingCondition.Serializer serializer = condition.getSerializer();
        ResourceLocation serializerId = PSTRegistries.LIVING_CONDITIONS.get().getKey((Object)serializer);
        buf.m_130070_(Objects.requireNonNull(serializerId).toString());
        serializer.serialize(buf, condition);
    }

    @Nonnull
    public static LivingCondition readLivingCondition(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.m_130277_());
        LivingCondition.Serializer serializer = (LivingCondition.Serializer)PSTRegistries.LIVING_CONDITIONS.get().getValue(serializerId);
        return (LivingCondition)Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeDamageCondition(FriendlyByteBuf buf, @Nonnull DamageCondition condition) {
        DamageCondition.Serializer serializer = condition.getSerializer();
        ResourceLocation serializerId = PSTRegistries.DAMAGE_CONDITIONS.get().getKey((Object)serializer);
        Objects.requireNonNull(serializerId);
        buf.m_130070_(serializerId.toString());
        serializer.serialize(buf, condition);
    }

    @Nonnull
    public static DamageCondition readDamageCondition(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.m_130277_());
        DamageCondition.Serializer serializer = (DamageCondition.Serializer)PSTRegistries.DAMAGE_CONDITIONS.get().getValue(serializerId);
        return (DamageCondition)Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeItemCondition(FriendlyByteBuf buf, @Nonnull ItemCondition condition) {
        ItemCondition.Serializer serializer = condition.getSerializer();
        ResourceLocation serializerId = PSTRegistries.ITEM_CONDITIONS.get().getKey((Object)serializer);
        buf.m_130070_(Objects.requireNonNull(serializerId).toString());
        serializer.serialize(buf, condition);
    }

    @Nonnull
    public static ItemCondition readItemCondition(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.m_130277_());
        ItemCondition.Serializer serializer = (ItemCondition.Serializer)PSTRegistries.ITEM_CONDITIONS.get().getValue(serializerId);
        return (ItemCondition)Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeEventListener(FriendlyByteBuf buf, @Nonnull SkillEventListener condition) {
        SkillEventListener.Serializer serializer = condition.getSerializer();
        ResourceLocation serializerId = PSTRegistries.EVENT_LISTENERS.get().getKey((Object)serializer);
        buf.m_130070_(Objects.requireNonNull(serializerId).toString());
        serializer.serialize(buf, condition);
    }

    @Nonnull
    public static SkillEventListener readEventListener(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.m_130277_());
        SkillEventListener.Serializer serializer = (SkillEventListener.Serializer)PSTRegistries.EVENT_LISTENERS.get().getValue(serializerId);
        return (SkillEventListener)Objects.requireNonNull(serializer).deserialize(buf);
    }

    public static void writeEffect(FriendlyByteBuf buf, MobEffect effect) {
        ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey((Object)effect);
        buf.m_130070_(Objects.requireNonNull(effectId).toString());
    }

    @Nullable
    public static MobEffect readEffect(FriendlyByteBuf buf) {
        ResourceLocation effectId = new ResourceLocation(buf.m_130277_());
        return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(effectId);
    }

    public static <T extends Enum<T>> void writeEnum(FriendlyByteBuf buf, T anEnum) {
        buf.writeInt(anEnum.ordinal());
    }

    @Nullable
    public static <T extends Enum<T>> T readEnum(FriendlyByteBuf buf, Class<T> type) {
        return (T)((Enum[])type.getEnumConstants())[buf.readInt()];
    }

    public static void writeOperation(FriendlyByteBuf buf, AttributeModifier.Operation operation) {
        buf.writeInt(operation.m_22235_());
    }

    @NotNull
    public static AttributeModifier.Operation readOperation(FriendlyByteBuf buf) {
        return AttributeModifier.Operation.m_22236_((int)buf.readInt());
    }

    public static void writeEffectInstance(FriendlyByteBuf buf, MobEffectInstance effect) {
        NetworkHelper.writeEffect(buf, effect.m_19544_());
        buf.writeInt(effect.m_19557_());
        buf.writeInt(effect.m_19564_());
    }

    @NotNull
    public static MobEffectInstance readEffectInstance(FriendlyByteBuf buf) {
        MobEffect effect = NetworkHelper.readEffect(buf);
        Objects.requireNonNull(effect);
        return new MobEffectInstance(effect, buf.readInt(), buf.readInt());
    }

    public static void writeValueProvider(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
        NumericValueProvider.Serializer serializer = provider.getSerializer();
        ResourceLocation serializerId = PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey((Object)serializer);
        Objects.requireNonNull(serializerId);
        buf.m_130070_(serializerId.toString());
        serializer.serialize(buf, provider);
    }

    public static NumericValueProvider<?> readValueProvider(FriendlyByteBuf buf) {
        ResourceLocation serializerId = new ResourceLocation(buf.m_130277_());
        NumericValueProvider.Serializer serializer = (NumericValueProvider.Serializer)PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getValue(serializerId);
        Objects.requireNonNull(serializer);
        return (NumericValueProvider)serializer.deserialize(buf);
    }
}

