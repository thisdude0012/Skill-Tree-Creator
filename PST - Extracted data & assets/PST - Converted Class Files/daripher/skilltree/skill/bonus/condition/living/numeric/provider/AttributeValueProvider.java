/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeMap
 *  net.minecraft.world.entity.ai.attributes.Attributes
 */
package daripher.skilltree.skill.bonus.condition.living.numeric.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttributeValueProvider
implements NumericValueProvider<AttributeValueProvider> {
    private Attribute attribute;

    public AttributeValueProvider(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public float getValue(LivingEntity entity) {
        AttributeMap attributes = entity.m_21204_();
        return attributes.m_22171_(this.attribute) ? (float)attributes.m_22181_(this.attribute) : 0.0f;
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        Object key = "%s.multiplier.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        MutableComponent attributeDescription = Component.m_237115_((String)this.attribute.m_22087_());
        if (divisor != 1.0f) {
            key = (String)key + ".plural";
            return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, this.formatNumber(divisor), attributeDescription});
        }
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, attributeDescription});
    }

    @Override
    public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip, float requiredValue) {
        String key = "%s.condition.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        MutableComponent attributeDescription = Component.m_237115_((String)this.attribute.m_22087_());
        String valueDescription = this.formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("attribute_value", valueDescription);
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, attributeDescription, logicDescription});
    }

    @Override
    public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
        String key = "%s.requirement".formatted(new Object[]{this.getDescriptionId()});
        MutableComponent attributeDescription = Component.m_237115_((String)this.attribute.m_22087_());
        String valueDescription = this.formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("attribute_value", valueDescription);
        return Component.m_237110_((String)key, (Object[])new Object[]{attributeDescription, logicDescription});
    }

    @Override
    public NumericValueProvider.Serializer getSerializer() {
        return (NumericValueProvider.Serializer)PSTNumericValueProviders.ATTRIBUTE_VALUE.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
        editor.addLabel(0, 0, "Attribute", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.attribute).setResponder(attribute -> this.selectAttribute(consumer, (Attribute)attribute));
        editor.increaseHeight(19);
    }

    private void selectAttribute(Consumer<NumericValueProvider<?>> consumer, Attribute attribute) {
        this.setAttribute(attribute);
        consumer.accept(this);
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public static class Serializer
    implements NumericValueProvider.Serializer {
        @Override
        public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
            Attribute attribute = SerializationHelper.deserializeAttribute(json);
            return new AttributeValueProvider(attribute);
        }

        @Override
        public void serialize(JsonObject json, NumericValueProvider<?> provider) {
            if (!(provider instanceof AttributeValueProvider)) {
                throw new IllegalArgumentException();
            }
            AttributeValueProvider aProvider = (AttributeValueProvider)provider;
            SerializationHelper.serializeAttribute(json, aProvider.attribute);
        }

        @Override
        public NumericValueProvider<?> deserialize(CompoundTag tag) {
            Attribute attribute = SerializationHelper.deserializeAttribute(tag);
            return new AttributeValueProvider(attribute);
        }

        @Override
        public CompoundTag serialize(NumericValueProvider<?> provider) {
            if (!(provider instanceof AttributeValueProvider)) {
                throw new IllegalArgumentException();
            }
            AttributeValueProvider aProvider = (AttributeValueProvider)provider;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeAttribute(tag, aProvider.attribute);
            return tag;
        }

        @Override
        public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
            Attribute attribute = NetworkHelper.readAttribute(buf);
            return new AttributeValueProvider(attribute);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
            if (!(provider instanceof AttributeValueProvider)) {
                throw new IllegalArgumentException();
            }
            AttributeValueProvider aProvider = (AttributeValueProvider)provider;
            NetworkHelper.writeAttribute(buf, aProvider.attribute);
        }

        @Override
        public NumericValueProvider<?> createDefaultInstance() {
            return new AttributeValueProvider(Attributes.f_22276_);
        }
    }
}

