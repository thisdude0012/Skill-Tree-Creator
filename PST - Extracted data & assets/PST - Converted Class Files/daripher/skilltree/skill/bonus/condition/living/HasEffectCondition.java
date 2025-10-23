/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nonnull
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.LivingEntity
 */
package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public final class HasEffectCondition
implements LivingCondition {
    private MobEffect effect;
    private int amplifier;

    public HasEffectCondition(@Nonnull MobEffect effect) {
        this(effect, 0);
    }

    public HasEffectCondition(@Nonnull MobEffect effect, int amplifier) {
        this.effect = effect;
        this.amplifier = amplifier;
    }

    @Override
    public boolean isConditionMet(LivingEntity living) {
        if (this.amplifier == 0) {
            return living.m_21023_(this.effect);
        }
        MobEffectInstance effect = living.m_21124_(this.effect);
        return effect != null && effect.m_19564_() >= this.amplifier;
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        String key = this.getDescriptionId();
        MutableComponent targetDescription = Component.m_237115_((String)"%s.target.%s".formatted(new Object[]{key, target.getName()}));
        Component effectDescription = this.effect.m_19482_();
        if (this.amplifier == 0) {
            return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, targetDescription, effectDescription});
        }
        MutableComponent amplifierDescription = Component.m_237115_((String)("potion.potency." + this.amplifier));
        effectDescription = Component.m_237110_((String)"potion.withAmplifier", (Object[])new Object[]{effectDescription, amplifierDescription});
        return Component.m_237110_((String)(key + ".amplifier"), (Object[])new Object[]{bonusTooltip, targetDescription, effectDescription});
    }

    @Override
    public LivingCondition.Serializer getSerializer() {
        return (LivingCondition.Serializer)PSTLivingConditions.HAS_EFFECT.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
        editor.addLabel(0, 0, "Effect", ChatFormatting.GREEN);
        editor.addLabel(150, 0, "Level", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 145, this.effect).setResponder(effect -> this.selectEffect(consumer, (MobEffect)effect));
        editor.addNumericTextField(150, 0, 50, 14, this.amplifier).setNumericFilter(value -> value >= 0.0 && value == (double)value.intValue()).setNumericResponder(value -> this.selectAmplifier(consumer, (Double)value));
        editor.increaseHeight(19);
    }

    private void selectAmplifier(Consumer<LivingCondition> consumer, Double value) {
        this.setAmplifier(value.intValue());
        consumer.accept(this);
    }

    private void selectEffect(Consumer<LivingCondition> consumer, MobEffect effect) {
        this.setEffect(effect);
        consumer.accept(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HasEffectCondition that = (HasEffectCondition)o;
        return this.amplifier == that.amplifier && Objects.equals(this.effect, that.effect);
    }

    public int hashCode() {
        return Objects.hash(this.effect, this.amplifier);
    }

    public void setEffect(MobEffect effect) {
        this.effect = effect;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public static class Serializer
    implements LivingCondition.Serializer {
        @Override
        public LivingCondition deserialize(JsonObject json) throws JsonParseException {
            MobEffect effect = SerializationHelper.deserializeEffect(json);
            int amplifier = !json.has("amplifier") ? 0 : json.get("amplifier").getAsInt();
            Objects.requireNonNull(effect);
            return new HasEffectCondition(effect, amplifier);
        }

        @Override
        public void serialize(JsonObject json, LivingCondition condition) {
            if (!(condition instanceof HasEffectCondition)) {
                throw new IllegalArgumentException();
            }
            HasEffectCondition aCondition = (HasEffectCondition)condition;
            SerializationHelper.serializeEffect(json, aCondition.effect);
            json.addProperty("amplifier", (Number)aCondition.amplifier);
        }

        @Override
        public LivingCondition deserialize(CompoundTag tag) {
            MobEffect effect = SerializationHelper.deserializeEffect(tag);
            int amplifier = !tag.m_128441_("amplifier") ? 0 : tag.m_128451_("amplifier");
            Objects.requireNonNull(effect);
            return new HasEffectCondition(effect, amplifier);
        }

        @Override
        public CompoundTag serialize(LivingCondition condition) {
            if (!(condition instanceof HasEffectCondition)) {
                throw new IllegalArgumentException();
            }
            HasEffectCondition aCondition = (HasEffectCondition)condition;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeEffect(tag, aCondition.effect);
            tag.m_128405_("amplifier", aCondition.amplifier);
            return tag;
        }

        @Override
        public LivingCondition deserialize(FriendlyByteBuf buf) {
            MobEffect effect = NetworkHelper.readEffect(buf);
            Objects.requireNonNull(effect);
            return new HasEffectCondition(effect, buf.readInt());
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
            if (!(condition instanceof HasEffectCondition)) {
                throw new IllegalArgumentException();
            }
            HasEffectCondition aCondition = (HasEffectCondition)condition;
            NetworkHelper.writeEffect(buf, aCondition.effect);
            buf.writeInt(aCondition.amplifier);
        }

        @Override
        public LivingCondition createDefaultInstance() {
            return new HasEffectCondition(MobEffects.f_19614_);
        }
    }
}

