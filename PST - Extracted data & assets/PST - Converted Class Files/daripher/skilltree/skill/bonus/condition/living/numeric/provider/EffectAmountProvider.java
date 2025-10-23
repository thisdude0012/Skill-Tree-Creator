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
 *  net.minecraft.world.effect.MobEffectCategory
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.LivingEntity
 */
package daripher.skilltree.skill.bonus.condition.living.numeric.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.effect.EffectType;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class EffectAmountProvider
implements NumericValueProvider<EffectAmountProvider> {
    private EffectType effectType;

    public EffectAmountProvider(EffectType effectType) {
        this.effectType = effectType;
    }

    @Override
    public float getValue(LivingEntity entity) {
        List effects = entity.m_21220_().stream().map(MobEffectInstance::m_19544_).toList();
        return switch (this.effectType) {
            default -> throw new IncompatibleClassChangeError();
            case EffectType.ANY -> effects.size();
            case EffectType.NEUTRAL -> effects.stream().filter(e -> e.m_19483_() == MobEffectCategory.NEUTRAL).count();
            case EffectType.HARMFUL -> effects.stream().filter(e -> e.m_19483_() == MobEffectCategory.HARMFUL).count();
            case EffectType.BENEFICIAL -> effects.stream().filter(e -> e.m_19483_() == MobEffectCategory.BENEFICIAL).count();
        };
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        Object key = "%s.multiplier.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        Object effectTypeKey = this.effectType.getDescriptionId();
        if (divisor != 1.0f) {
            effectTypeKey = (String)effectTypeKey + ".plural";
            key = (String)key + ".plural";
            MutableComponent effectDescription = Component.m_237115_((String)effectTypeKey);
            return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, this.formatNumber(divisor), effectDescription});
        }
        MutableComponent effectDescription = Component.m_237115_((String)effectTypeKey);
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, effectDescription});
    }

    @Override
    public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip, float requiredValue) {
        String key = "%s.condition.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        Object effectTypeKey = this.effectType.getDescriptionId();
        if ((requiredValue != 0.0f || logic != NumericValueCondition.Logic.MORE) && requiredValue != 1.0f) {
            effectTypeKey = (String)effectTypeKey + ".plural";
        }
        MutableComponent effectDescription = Component.m_237115_((String)effectTypeKey);
        if (requiredValue == 0.0f && logic == NumericValueCondition.Logic.EQUAL) {
            return Component.m_237110_((String)(key + ".none"), (Object[])new Object[]{bonusTooltip, effectDescription});
        }
        if (requiredValue == 0.0f && logic == NumericValueCondition.Logic.MORE) {
            return Component.m_237110_((String)(key + ".any"), (Object[])new Object[]{bonusTooltip, effectDescription});
        }
        String valueDescription = this.formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("effect_amount", valueDescription);
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, logicDescription, effectDescription});
    }

    @Override
    public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
        String key = "%s.requirement".formatted(new Object[]{this.getDescriptionId()});
        Object effectTypeKey = this.effectType.getDescriptionId();
        if ((requiredValue != 0.0f || logic != NumericValueCondition.Logic.MORE) && requiredValue != 1.0f) {
            effectTypeKey = (String)effectTypeKey + ".plural";
        }
        MutableComponent effectDescription = Component.m_237115_((String)effectTypeKey);
        if (requiredValue == 0.0f && logic == NumericValueCondition.Logic.EQUAL) {
            return Component.m_237110_((String)(key + ".none"), (Object[])new Object[]{effectDescription});
        }
        if (requiredValue == 0.0f && logic == NumericValueCondition.Logic.MORE) {
            return Component.m_237110_((String)(key + ".any"), (Object[])new Object[]{effectDescription});
        }
        String valueDescription = this.formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("effect_amount", valueDescription);
        return Component.m_237110_((String)key, (Object[])new Object[]{logicDescription, effectDescription});
    }

    @Override
    public NumericValueProvider.Serializer getSerializer() {
        return (NumericValueProvider.Serializer)PSTNumericValueProviders.EFFECT_AMOUNT.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
        editor.addLabel(0, 0, "Effect Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.effectType).setElementNameGetter(effectType -> Component.m_237113_((String)effectType.name())).setResponder(effectType -> this.selectEffectType(consumer, (EffectType)((Object)effectType)));
        editor.increaseHeight(19);
    }

    private void selectEffectType(Consumer<NumericValueProvider<?>> consumer, EffectType type) {
        this.setEffectType(type);
        consumer.accept(this);
    }

    public void setEffectType(EffectType type) {
        this.effectType = type;
    }

    public static class Serializer
    implements NumericValueProvider.Serializer {
        @Override
        public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
            EffectType type = EffectType.fromName(json.get("effect_type").getAsString());
            return new EffectAmountProvider(type);
        }

        @Override
        public void serialize(JsonObject json, NumericValueProvider<?> provider) {
            if (!(provider instanceof EffectAmountProvider)) {
                throw new IllegalArgumentException();
            }
            EffectAmountProvider aProvider = (EffectAmountProvider)provider;
            json.addProperty("effect_type", aProvider.effectType.getName());
        }

        @Override
        public NumericValueProvider<?> deserialize(CompoundTag tag) {
            EffectType type = EffectType.fromName(tag.m_128461_("effect_type"));
            return new EffectAmountProvider(type);
        }

        @Override
        public CompoundTag serialize(NumericValueProvider<?> provider) {
            if (!(provider instanceof EffectAmountProvider)) {
                throw new IllegalArgumentException();
            }
            EffectAmountProvider aProvider = (EffectAmountProvider)provider;
            CompoundTag tag = new CompoundTag();
            tag.m_128359_("effect_type", aProvider.effectType.getName());
            return tag;
        }

        @Override
        public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
            EffectType type = EffectType.values()[buf.readInt()];
            return new EffectAmountProvider(type);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
            if (!(provider instanceof EffectAmountProvider)) {
                throw new IllegalArgumentException();
            }
            EffectAmountProvider aProvider = (EffectAmountProvider)provider;
            buf.writeInt(aProvider.effectType.ordinal());
        }

        @Override
        public NumericValueProvider<?> createDefaultInstance() {
            return new EffectAmountProvider(EffectType.ANY);
        }
    }
}

