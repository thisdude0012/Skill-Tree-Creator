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
 *  net.minecraft.world.entity.ai.attributes.Attributes
 */
package daripher.skilltree.skill.bonus.multiplier;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTLivingMultipliers;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.AttributeValueProvider;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class NumericValueMultiplier
implements LivingMultiplier {
    private NumericValueProvider<?> valueProvider;
    private float divisor;

    public NumericValueMultiplier(NumericValueProvider<?> valueProvider, float divisor) {
        this.valueProvider = valueProvider;
        this.divisor = divisor;
    }

    @Override
    public float getValue(LivingEntity entity) {
        return (int)(this.valueProvider.getValue(entity) / this.divisor);
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        return this.valueProvider.getMultiplierTooltip(target, this.divisor, (Component)bonusTooltip);
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingMultiplier> consumer) {
        editor.addLabel(0, 0, "Value Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.valueProvider).setResponder(provider -> {
            this.selectValueProvider(consumer, (NumericValueProvider<?>)provider);
            this.addValueProviderWidgets(editor, consumer);
            editor.rebuildWidgets();
        }).setMenuInitFunc(() -> this.addValueProviderWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Divisor", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.divisor).setNumericFilter(value -> value > 0.0).setNumericResponder(value -> this.selectDivisor(consumer, (Double)value));
        editor.increaseHeight(19);
    }

    private void addValueProviderWidgets(SkillTreeEditor editor, Consumer<LivingMultiplier> consumer) {
        this.valueProvider.addEditorWidgets(editor, (NumericValueProvider<?> provider) -> this.selectValueProvider(consumer, (NumericValueProvider<?>)provider));
    }

    private void selectDivisor(Consumer<LivingMultiplier> consumer, Double value) {
        this.setDivisor(value.floatValue());
        consumer.accept(this);
    }

    private void selectValueProvider(Consumer<LivingMultiplier> consumer, NumericValueProvider<?> valueProvider) {
        this.setValueProvider(valueProvider);
        consumer.accept(this);
    }

    @Override
    public LivingMultiplier.Serializer getSerializer() {
        return (LivingMultiplier.Serializer)PSTLivingMultipliers.NUMERIC_VALUE.get();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NumericValueMultiplier that = (NumericValueMultiplier)o;
        if (Float.compare(this.divisor, that.divisor) != 0) {
            return false;
        }
        return Objects.equals(this.valueProvider, that.valueProvider);
    }

    public int hashCode() {
        return Objects.hash(this.valueProvider, Float.valueOf(this.divisor));
    }

    public void setValueProvider(NumericValueProvider<?> valueProvider) {
        this.valueProvider = valueProvider;
    }

    public void setDivisor(float divisor) {
        this.divisor = divisor;
    }

    public static class Serializer
    implements LivingMultiplier.Serializer {
        @Override
        public LivingMultiplier deserialize(JsonObject json) throws JsonParseException {
            NumericValueProvider<?> valueProvider = SerializationHelper.deserializeValueProvider(json);
            float divisor = !json.has("divisor") ? 1.0f : json.get("divisor").getAsFloat();
            return new NumericValueMultiplier(valueProvider, divisor);
        }

        @Override
        public void serialize(JsonObject json, LivingMultiplier multiplier) {
            if (!(multiplier instanceof NumericValueMultiplier)) {
                throw new IllegalArgumentException();
            }
            NumericValueMultiplier aMultiplier = (NumericValueMultiplier)multiplier;
            SerializationHelper.serializeValueProvider(json, aMultiplier.valueProvider);
            json.addProperty("divisor", (Number)Float.valueOf(aMultiplier.divisor));
        }

        @Override
        public LivingMultiplier deserialize(CompoundTag tag) {
            NumericValueProvider<?> valueProvider = SerializationHelper.deserializeValueProvider(tag);
            float divisor = !tag.m_128441_("divisor") ? 1.0f : tag.m_128457_("divisor");
            return new NumericValueMultiplier(valueProvider, divisor);
        }

        @Override
        public CompoundTag serialize(LivingMultiplier multiplier) {
            if (!(multiplier instanceof NumericValueMultiplier)) {
                throw new IllegalArgumentException();
            }
            NumericValueMultiplier aMultiplier = (NumericValueMultiplier)multiplier;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeValueProvider(tag, aMultiplier.valueProvider);
            tag.m_128350_("divisor", aMultiplier.divisor);
            return tag;
        }

        @Override
        public LivingMultiplier deserialize(FriendlyByteBuf buf) {
            NumericValueProvider<?> valueProvider = NetworkHelper.readValueProvider(buf);
            float divisor = buf.readFloat();
            return new NumericValueMultiplier(valueProvider, divisor);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingMultiplier multiplier) {
            if (!(multiplier instanceof NumericValueMultiplier)) {
                throw new IllegalArgumentException();
            }
            NumericValueMultiplier aMultiplier = (NumericValueMultiplier)multiplier;
            NetworkHelper.writeValueProvider(buf, aMultiplier.valueProvider);
            buf.writeFloat(aMultiplier.divisor);
        }

        @Override
        public LivingMultiplier createDefaultInstance() {
            return new NumericValueMultiplier(new AttributeValueProvider(Attributes.f_22276_), 5.0f);
        }
    }
}

