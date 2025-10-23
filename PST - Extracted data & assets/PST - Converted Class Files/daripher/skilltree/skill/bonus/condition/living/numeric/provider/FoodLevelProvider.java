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
 *  net.minecraft.world.entity.player.Player
 */
package daripher.skilltree.skill.bonus.condition.living.numeric.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FoodLevelProvider
implements NumericValueProvider<FoodLevelProvider> {
    private boolean percentage;
    private boolean missing;

    public FoodLevelProvider(boolean percentage, boolean missing) {
        this.percentage = percentage;
        this.missing = missing;
    }

    @Override
    public float getValue(LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return 0.0f;
        }
        Player player = (Player)entity;
        float value = player.m_36324_().m_38702_();
        if (this.missing) {
            value = 20.0f - value;
        }
        if (this.percentage) {
            value /= 20.0f;
        }
        return value;
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        Object key = "%s.multiplier.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        String pointsKey = this.getDescriptionId() + ".point";
        if (divisor != 1.0f) {
            key = (String)key + ".plural";
            pointsKey = pointsKey + ".plural";
        }
        MutableComponent pointsDescription = Component.m_237115_((String)pointsKey);
        if (this.missing) {
            key = (String)key + ".missing";
        }
        if (divisor != 1.0f) {
            return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, this.formatNumber(divisor), pointsDescription});
        }
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, pointsDescription});
    }

    @Override
    public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip, float requiredValue) {
        Object key = "%s.condition.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        String pointsKey = this.getDescriptionId() + ".point";
        if (requiredValue != 1.0f) {
            pointsKey = pointsKey + ".plural";
        }
        MutableComponent pointsDescription = Component.m_237115_((String)pointsKey);
        if (logic == NumericValueCondition.Logic.EQUAL && this.percentage && requiredValue == 1.0f) {
            return Component.m_237110_((String)((String)key + ".full"), (Object[])new Object[]{bonusTooltip});
        }
        if (logic == NumericValueCondition.Logic.LESS && this.percentage && requiredValue == 1.0f) {
            return Component.m_237110_((String)((String)key + ".not_full"), (Object[])new Object[]{bonusTooltip});
        }
        if (this.missing) {
            key = (String)key + ".missing";
        }
        String valueDescription = this.formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("food_level", valueDescription);
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, logicDescription, pointsDescription});
    }

    @Override
    public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
        Object key = "%s.requirement".formatted(new Object[]{this.getDescriptionId()});
        String pointsKey = this.getDescriptionId() + ".point";
        if (requiredValue != 1.0f) {
            pointsKey = pointsKey + ".plural";
        }
        MutableComponent pointsDescription = Component.m_237115_((String)pointsKey);
        if (logic == NumericValueCondition.Logic.EQUAL && this.percentage && requiredValue == 1.0f) {
            return Component.m_237115_((String)((String)key + ".full"));
        }
        if (logic == NumericValueCondition.Logic.LESS && this.percentage && requiredValue == 1.0f) {
            return Component.m_237115_((String)((String)key + ".not_full"));
        }
        if (this.missing) {
            key = (String)key + ".missing";
        }
        String valueDescription = this.formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("food_level", valueDescription);
        return Component.m_237110_((String)key, (Object[])new Object[]{logicDescription, pointsDescription});
    }

    @Override
    public String formatNumber(float number) {
        if (this.percentage) {
            return NumericValueProvider.super.formatNumber(number * 100.0f) + "%";
        }
        return NumericValueProvider.super.formatNumber(number);
    }

    @Override
    public NumericValueProvider.Serializer getSerializer() {
        return (NumericValueProvider.Serializer)PSTNumericValueProviders.FOOD_LEVEL.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
        editor.addLabel(0, 0, "Missing", ChatFormatting.GREEN);
        editor.addLabel(55, 0, "Percentage", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addCheckBox(0, 0, this.missing).setResponder(v -> this.selectMissingMode(consumer, (boolean)v));
        editor.addCheckBox(55, 0, this.percentage).setResponder(v -> this.selectPercentageMode(consumer, (boolean)v));
        editor.increaseHeight(19);
    }

    private void selectMissingMode(Consumer<NumericValueProvider<?>> consumer, boolean missing) {
        this.setMissing(missing);
        consumer.accept(this);
    }

    private void selectPercentageMode(Consumer<NumericValueProvider<?>> consumer, boolean percentage) {
        this.setPercentage(percentage);
        consumer.accept(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FoodLevelProvider that = (FoodLevelProvider)o;
        return this.percentage == that.percentage && this.missing == that.missing;
    }

    public int hashCode() {
        return Objects.hash(this.percentage, this.missing);
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    public void setPercentage(boolean percentage) {
        this.percentage = percentage;
    }

    public static class Serializer
    implements NumericValueProvider.Serializer {
        @Override
        public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
            boolean percentage = json.get("percentage").getAsBoolean();
            boolean missing = json.get("missing").getAsBoolean();
            return new FoodLevelProvider(percentage, missing);
        }

        @Override
        public void serialize(JsonObject json, NumericValueProvider<?> provider) {
            if (!(provider instanceof FoodLevelProvider)) {
                throw new IllegalArgumentException();
            }
            FoodLevelProvider aProvider = (FoodLevelProvider)provider;
            json.addProperty("percentage", Boolean.valueOf(aProvider.percentage));
            json.addProperty("missing", Boolean.valueOf(aProvider.missing));
        }

        @Override
        public NumericValueProvider<?> deserialize(CompoundTag tag) {
            boolean percentage = tag.m_128471_("percentage");
            boolean missing = tag.m_128471_("missing");
            return new FoodLevelProvider(percentage, missing);
        }

        @Override
        public CompoundTag serialize(NumericValueProvider<?> provider) {
            if (!(provider instanceof FoodLevelProvider)) {
                throw new IllegalArgumentException();
            }
            FoodLevelProvider aProvider = (FoodLevelProvider)provider;
            CompoundTag tag = new CompoundTag();
            tag.m_128379_("percentage", aProvider.percentage);
            tag.m_128379_("missing", aProvider.missing);
            return tag;
        }

        @Override
        public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
            boolean percentage = buf.readBoolean();
            boolean missing = buf.readBoolean();
            return new FoodLevelProvider(percentage, missing);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
            if (!(provider instanceof FoodLevelProvider)) {
                throw new IllegalArgumentException();
            }
            FoodLevelProvider aProvider = (FoodLevelProvider)provider;
            buf.writeBoolean(aProvider.percentage);
            buf.writeBoolean(aProvider.missing);
        }

        @Override
        public NumericValueProvider<?> createDefaultInstance() {
            return new FoodLevelProvider(false, false);
        }
    }
}

