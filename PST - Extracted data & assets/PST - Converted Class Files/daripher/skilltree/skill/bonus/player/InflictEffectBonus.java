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
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectCategory
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 */
package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.AttackEventListener;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.event.TickingEventListener;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class InflictEffectBonus
implements EventListenerBonus<InflictEffectBonus> {
    private MobEffectInstance effectInstance;
    private SkillEventListener eventListener;
    private float chance;
    private int maxStacks;

    public InflictEffectBonus(float chance, MobEffectInstance effectInstance, SkillEventListener eventListener, int maxStacks) {
        this.chance = chance;
        this.effectInstance = effectInstance;
        this.eventListener = eventListener;
        this.maxStacks = maxStacks;
    }

    public InflictEffectBonus(float chance, MobEffectInstance effectInstance, int maxStacks) {
        this(chance, effectInstance, new AttackEventListener(), maxStacks);
    }

    @Override
    public void applyEffect(LivingEntity target) {
        RandomSource random = target.m_217043_();
        if (!(random.m_188501_() < this.chance)) {
            return;
        }
        MobEffectInstance effectInstanceCopy = new MobEffectInstance(this.effectInstance);
        MobEffect effect = this.effectInstance.m_19544_();
        if (this.maxStacks > 1) {
            effectInstanceCopy = this.getStackedEffectInstance(target, effect, effectInstanceCopy);
        }
        target.m_7292_(effectInstanceCopy);
    }

    private MobEffectInstance getStackedEffectInstance(LivingEntity target, MobEffect effect, MobEffectInstance effectInstanceCopy) {
        MobEffectInstance activeEffectInstance = target.m_21124_(effect);
        if (activeEffectInstance == null) {
            return effectInstanceCopy;
        }
        int amplifier = activeEffectInstance.m_19564_();
        if (amplifier >= this.maxStacks - 1) {
            return effectInstanceCopy;
        }
        int duration = this.effectInstance.m_19557_();
        effectInstanceCopy = new MobEffectInstance(effect, duration, amplifier + 1);
        return effectInstanceCopy;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.INFLICT_EFFECT.get();
    }

    public InflictEffectBonus copy() {
        return new InflictEffectBonus(this.chance, this.effectInstance, this.eventListener, this.maxStacks);
    }

    @Override
    public InflictEffectBonus multiply(double multiplier) {
        if (this.chance < 1.0f) {
            this.chance *= (float)multiplier;
        } else {
            int newDuration = (int)((double)this.effectInstance.m_19557_() * multiplier);
            this.effectInstance = new MobEffectInstance(this.effectInstance.m_19544_(), newDuration, this.effectInstance.m_19564_());
            return new InflictEffectBonus(this.chance, this.effectInstance, this.eventListener, this.maxStacks);
        }
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof InflictEffectBonus)) {
            return false;
        }
        InflictEffectBonus otherBonus = (InflictEffectBonus)other;
        if (!Objects.equals(otherBonus.effectInstance.m_19544_(), this.effectInstance.m_19544_())) {
            return false;
        }
        return Objects.equals(otherBonus.eventListener, this.eventListener);
    }

    @Override
    public SkillBonus<EventListenerBonus<InflictEffectBonus>> merge(SkillBonus<?> other) {
        if (!(other instanceof InflictEffectBonus)) {
            throw new IllegalArgumentException();
        }
        InflictEffectBonus otherBonus = (InflictEffectBonus)other;
        if (this.chance < 1.0f) {
            return new InflictEffectBonus(otherBonus.chance + this.chance, this.effectInstance, this.eventListener, this.maxStacks);
        }
        int newDuration = this.effectInstance.m_19557_() + otherBonus.effectInstance.m_19557_();
        this.effectInstance = new MobEffectInstance(this.effectInstance.m_19544_(), newDuration, this.effectInstance.m_19564_());
        return new InflictEffectBonus(this.chance, this.effectInstance, this.eventListener, this.maxStacks);
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent tooltip;
        boolean showDuration;
        Component effectDescription = TooltipHelper.getEffectTooltip(this.effectInstance);
        int duration = this.effectInstance.m_19557_();
        SkillBonus.Target target = this.eventListener.getTarget();
        String targetDescription = target.getName();
        String bonusDescription = this.getDescriptionId() + "." + targetDescription;
        if (this.chance < 1.0f) {
            bonusDescription = bonusDescription + ".chance";
        }
        boolean isInstantEffect = duration == 0;
        boolean bl = showDuration = !isInstantEffect && (!(this.getEventListener() instanceof TickingEventListener) || duration > 20);
        if (showDuration) {
            Component durationDescription = this.getDurationDescription();
            tooltip = Component.m_237110_((String)bonusDescription, (Object[])new Object[]{effectDescription, durationDescription});
        } else {
            tooltip = Component.m_237110_((String)bonusDescription, (Object[])new Object[]{effectDescription, ""});
        }
        if (this.chance < 1.0f) {
            tooltip = TooltipHelper.getSkillBonusTooltip((Component)tooltip, (double)this.chance, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        tooltip = this.eventListener.getTooltip((Component)tooltip);
        if (this.maxStacks > 1) {
            tooltip = Component.m_237110_((String)(this.getDescriptionId() + ".stacks"), (Object[])new Object[]{tooltip, this.maxStacks});
        }
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    private Component getDurationDescription() {
        boolean measureInSeconds = this.effectInstance.m_19557_() < 1200;
        String measurement = measureInSeconds ? "seconds" : "minutes";
        float duration = measureInSeconds ? (float)this.effectInstance.m_19557_() / 20.0f : (float)this.effectInstance.m_19557_() / 1200.0f;
        String formattedDuration = TooltipHelper.formatNumber(duration);
        return Component.m_237110_((String)(this.getDescriptionId() + "." + measurement), (Object[])new Object[]{formattedDuration});
    }

    @Override
    public void gatherInfo(Consumer<MutableComponent> consumer) {
        TooltipHelper.consumeTranslated(this.effectInstance.m_19576_() + ".info", consumer);
    }

    @Override
    public boolean isPositive() {
        return this.chance > 0.0f ^ this.eventListener.getTarget() == SkillBonus.Target.PLAYER ^ this.effectInstance.m_19544_().m_19483_() != MobEffectCategory.HARMFUL;
    }

    @Override
    public SkillEventListener getEventListener() {
        return this.eventListener;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<EventListenerBonus<InflictEffectBonus>> consumer) {
        editor.addLabel(0, 0, "Effect", ChatFormatting.GOLD);
        editor.addLabel(150, 0, "Chance", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 145, this.effectInstance.m_19544_()).setResponder(effect -> this.selectEffect(consumer, (MobEffect)effect));
        editor.addNumericTextField(150, 0, 50, 14, this.chance).setNumericResponder(value -> this.selectChance(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Duration", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Amplifier", ChatFormatting.GOLD);
        editor.addLabel(110, 0, "Stacks", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.effectInstance.m_19557_()).setNumericFilter(value -> value >= -1.0).setNumericResponder(value -> this.selectDuration(consumer, (Double)value));
        editor.addNumericTextField(55, 0, 50, 14, this.effectInstance.m_19564_()).setNumericFilter(value -> value >= 0.0).setNumericResponder(value -> this.selectAmplifier(consumer, (Double)value));
        editor.addNumericTextField(110, 0, 50, 14, this.maxStacks).setNumericFilter(value -> value >= 1.0).setNumericResponder(value -> this.selectMaxStacks(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.eventListener).setResponder(eventListener -> this.selectEventListener(editor, consumer, (SkillEventListener)eventListener)).setMenuInitFunc(() -> this.addEventListenerWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictEffectBonus>> consumer) {
        this.eventListener.addEditorWidgets(editor, eventListener -> {
            this.setEventListener((SkillEventListener)eventListener);
            consumer.accept(this.copy());
        });
    }

    private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictEffectBonus>> consumer, SkillEventListener eventListener) {
        this.setEventListener(eventListener);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectAmplifier(Consumer<EventListenerBonus<InflictEffectBonus>> consumer, Double value) {
        this.setAmplifier(value.intValue());
        consumer.accept(this.copy());
    }

    private void selectMaxStacks(Consumer<EventListenerBonus<InflictEffectBonus>> consumer, Double value) {
        this.setMaxStacks(value.intValue());
        consumer.accept(this.copy());
    }

    private void selectDuration(Consumer<EventListenerBonus<InflictEffectBonus>> consumer, Double value) {
        this.setDuration(value.intValue());
        consumer.accept(this.copy());
    }

    private void selectChance(Consumer<EventListenerBonus<InflictEffectBonus>> consumer, Double value) {
        this.setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    private void selectEffect(Consumer<EventListenerBonus<InflictEffectBonus>> consumer, MobEffect effect) {
        this.setEffectInstance(effect);
        consumer.accept(this);
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setEffectInstance(MobEffect effectInstance) {
        this.effectInstance = new MobEffectInstance(effectInstance, this.effectInstance.m_19557_(), this.effectInstance.m_19564_());
    }

    public void setDuration(int duration) {
        this.effectInstance = new MobEffectInstance(this.effectInstance.m_19544_(), duration, this.effectInstance.m_19564_());
    }

    public void setAmplifier(int amplifier) {
        this.effectInstance = new MobEffectInstance(this.effectInstance.m_19544_(), this.effectInstance.m_19557_(), amplifier);
    }

    public void setMaxStacks(int maxStacks) {
        this.maxStacks = maxStacks;
    }

    public void setEventListener(SkillEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public InflictEffectBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            MobEffectInstance effect = SerializationHelper.deserializeEffectInstance(json);
            int maxStacks = json.has("max_stacks") ? json.get("max_stacks").getAsInt() : 0;
            InflictEffectBonus bonus = new InflictEffectBonus(chance, effect, maxStacks);
            bonus.eventListener = SerializationHelper.deserializeEventListener(json);
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictEffectBonus)) {
                throw new IllegalArgumentException();
            }
            InflictEffectBonus aBonus = (InflictEffectBonus)bonus;
            json.addProperty("chance", (Number)Float.valueOf(aBonus.chance));
            json.addProperty("max_stacks", (Number)aBonus.maxStacks);
            SerializationHelper.serializeEffectInstance(json, aBonus.effectInstance);
            SerializationHelper.serializeEventListener(json, aBonus.eventListener);
        }

        @Override
        public InflictEffectBonus deserialize(CompoundTag tag) {
            float chance = tag.m_128457_("chance");
            MobEffectInstance effect = SerializationHelper.deserializeEffectInstance(tag);
            int maxStacks = tag.m_128451_("max_stacks");
            InflictEffectBonus bonus = new InflictEffectBonus(chance, effect, maxStacks);
            bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictEffectBonus)) {
                throw new IllegalArgumentException();
            }
            InflictEffectBonus aBonus = (InflictEffectBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("chance", aBonus.chance);
            tag.m_128405_("max_stacks", aBonus.maxStacks);
            SerializationHelper.serializeEffectInstance(tag, aBonus.effectInstance);
            SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
            return tag;
        }

        @Override
        public InflictEffectBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            int maxStacks = buf.readInt();
            MobEffectInstance effect = NetworkHelper.readEffectInstance(buf);
            InflictEffectBonus bonus = new InflictEffectBonus(amount, effect, maxStacks);
            bonus.eventListener = NetworkHelper.readEventListener(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictEffectBonus)) {
                throw new IllegalArgumentException();
            }
            InflictEffectBonus aBonus = (InflictEffectBonus)bonus;
            buf.writeFloat(aBonus.chance);
            buf.writeInt(aBonus.maxStacks);
            NetworkHelper.writeEffectInstance(buf, aBonus.effectInstance);
            NetworkHelper.writeEventListener(buf, aBonus.eventListener);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new InflictEffectBonus(0.05f, new MobEffectInstance(MobEffects.f_19614_, 100), 1);
        }
    }
}

