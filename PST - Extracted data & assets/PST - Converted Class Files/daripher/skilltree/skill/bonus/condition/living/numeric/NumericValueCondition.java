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
 */
package daripher.skilltree.skill.bonus.condition.living.numeric;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.HealthLevelProvider;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public class NumericValueCondition
implements LivingCondition {
    private NumericValueProvider<?> valueProvider;
    private float requiredValue;
    private Logic logic;

    public NumericValueCondition(NumericValueProvider<?> valueProvider, float requiredValue, Logic logic) {
        this.valueProvider = valueProvider;
        this.requiredValue = requiredValue;
        this.logic = logic;
    }

    @Override
    public boolean isConditionMet(LivingEntity living) {
        float value = this.valueProvider.getValue(living);
        return switch (this.logic) {
            default -> throw new IncompatibleClassChangeError();
            case Logic.EQUAL -> {
                if (value == this.requiredValue) {
                    yield true;
                }
                yield false;
            }
            case Logic.MORE -> {
                if (value > this.requiredValue) {
                    yield true;
                }
                yield false;
            }
            case Logic.LESS -> {
                if (value < this.requiredValue) {
                    yield true;
                }
                yield false;
            }
            case Logic.AT_LEAST -> {
                if (value >= this.requiredValue) {
                    yield true;
                }
                yield false;
            }
            case Logic.AT_MOST -> value <= this.requiredValue;
        };
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        return this.valueProvider.getConditionTooltip(target, this.logic, (Component)bonusTooltip, this.requiredValue);
    }

    @Override
    public LivingCondition.Serializer getSerializer() {
        return (LivingCondition.Serializer)PSTLivingConditions.NUMERIC_VALUE.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
        editor.addLabel(0, 0, "Value Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.valueProvider).setResponder(provider -> this.selectValueProvider(editor, consumer, (NumericValueProvider<?>)provider)).setMenuInitFunc(() -> this.addValueProviderWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Logic", ChatFormatting.GREEN);
        editor.addLabel(100, 0, "Required Value", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 90, this.logic).setElementNameGetter(logic -> Component.m_237113_((String)logic.name())).setResponder(logic -> this.selectLogic(consumer, (Logic)((Object)logic)));
        editor.addNumericTextField(100, 0, 50, 14, this.requiredValue).setNumericResponder(value -> this.selectRequiredValue(consumer, (Double)value));
        editor.increaseHeight(19);
    }

    private void addValueProviderWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
        this.valueProvider.addEditorWidgets(editor, (NumericValueProvider<?> provider) -> this.selectValueProvider(editor, consumer, (NumericValueProvider<?>)provider));
    }

    private void selectRequiredValue(Consumer<LivingCondition> consumer, Double value) {
        this.setRequiredValue(value.floatValue());
        consumer.accept(this);
    }

    private void selectValueProvider(SkillTreeEditor editor, Consumer<LivingCondition> consumer, NumericValueProvider<?> provider) {
        this.setValueProvider(provider);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    private void selectLogic(Consumer<LivingCondition> consumer, Logic logic) {
        this.setLogic(logic);
        consumer.accept(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NumericValueCondition that = (NumericValueCondition)o;
        if (Float.compare(this.requiredValue, that.requiredValue) != 0) {
            return false;
        }
        if (!Objects.equals(this.valueProvider, that.valueProvider)) {
            return false;
        }
        return this.logic == that.logic;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.valueProvider, Float.valueOf(this.requiredValue), this.logic});
    }

    public void setValueProvider(NumericValueProvider<?> provider) {
        this.valueProvider = provider;
    }

    public void setRequiredValue(float requiredValue) {
        this.requiredValue = requiredValue;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public NumericValueProvider<?> getValueProvider() {
        return this.valueProvider;
    }

    public Logic getLogic() {
        return this.logic;
    }

    public float getRequiredValue() {
        return this.requiredValue;
    }

    public static enum Logic {
        MORE,
        LESS,
        EQUAL,
        AT_LEAST,
        AT_MOST;


        public String getName() {
            return this.name().toLowerCase();
        }

        public Component getTooltip(String subtype, Object ... args) {
            String conditionDescriptionId = ((LivingCondition.Serializer)PSTLivingConditions.NUMERIC_VALUE.get()).createDefaultInstance().getDescriptionId();
            String key = conditionDescriptionId + "." + this.getName();
            return TooltipHelper.getOptionalTooltip(key, subtype, args);
        }
    }

    public static class Serializer
    implements LivingCondition.Serializer {
        @Override
        public LivingCondition deserialize(JsonObject json) throws JsonParseException {
            NumericValueProvider<?> valueProvider = SerializationHelper.deserializeValueProvider(json);
            float requiredValue = json.get("required_value").getAsFloat();
            Logic logic = Logic.valueOf(json.get("logic").getAsString());
            return new NumericValueCondition(valueProvider, requiredValue, logic);
        }

        @Override
        public void serialize(JsonObject json, LivingCondition condition) {
            if (!(condition instanceof NumericValueCondition)) {
                throw new IllegalArgumentException();
            }
            NumericValueCondition aCondition = (NumericValueCondition)condition;
            SerializationHelper.serializeValueProvider(json, aCondition.valueProvider);
            json.addProperty("required_value", (Number)Float.valueOf(aCondition.requiredValue));
            json.addProperty("logic", aCondition.logic.name());
        }

        @Override
        public LivingCondition deserialize(CompoundTag tag) {
            NumericValueProvider<?> valueProvider = SerializationHelper.deserializeValueProvider(tag);
            float requiredValue = tag.m_128457_("required_value");
            Logic logic = Logic.valueOf(tag.m_128461_("logic"));
            return new NumericValueCondition(valueProvider, requiredValue, logic);
        }

        @Override
        public CompoundTag serialize(LivingCondition condition) {
            if (!(condition instanceof NumericValueCondition)) {
                throw new IllegalArgumentException();
            }
            NumericValueCondition aCondition = (NumericValueCondition)condition;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeValueProvider(tag, aCondition.valueProvider);
            tag.m_128350_("required_value", aCondition.requiredValue);
            tag.m_128359_("logic", aCondition.logic.name());
            return tag;
        }

        @Override
        public LivingCondition deserialize(FriendlyByteBuf buf) {
            NumericValueProvider<?> valueProvider = NetworkHelper.readValueProvider(buf);
            float requiredValue = buf.readFloat();
            Logic logic = Logic.values()[buf.readInt()];
            return new NumericValueCondition(valueProvider, requiredValue, logic);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
            if (!(condition instanceof NumericValueCondition)) {
                throw new IllegalArgumentException();
            }
            NumericValueCondition aCondition = (NumericValueCondition)condition;
            NetworkHelper.writeValueProvider(buf, aCondition.valueProvider);
            buf.writeFloat(aCondition.requiredValue);
            buf.writeInt(aCondition.logic.ordinal());
        }

        @Override
        public LivingCondition createDefaultInstance() {
            return new NumericValueCondition(new HealthLevelProvider(true, false), 1.0f, Logic.EQUAL);
        }
    }
}

