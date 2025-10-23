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
 *  net.minecraft.util.StringUtil
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
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class InflictIgniteBonus
implements EventListenerBonus<InflictIgniteBonus> {
    private float chance;
    private int duration;
    private SkillEventListener eventListener;

    public InflictIgniteBonus(float chance, int duration, SkillEventListener eventListener) {
        this.chance = chance;
        this.duration = duration;
        this.eventListener = eventListener;
    }

    public InflictIgniteBonus(float chance, int duration) {
        this(chance, duration, new AttackEventListener());
    }

    @Override
    public void applyEffect(LivingEntity target) {
        if (target.m_217043_().m_188501_() < this.chance) {
            target.m_20254_(this.duration);
        }
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.INFLICT_IGNITE.get();
    }

    public InflictIgniteBonus copy() {
        return new InflictIgniteBonus(this.chance, this.duration, this.eventListener);
    }

    @Override
    public InflictIgniteBonus multiply(double multiplier) {
        this.chance *= (float)multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof InflictIgniteBonus)) {
            return false;
        }
        InflictIgniteBonus otherBonus = (InflictIgniteBonus)other;
        if (otherBonus.duration != this.duration) {
            return false;
        }
        return Objects.equals(otherBonus.eventListener, this.eventListener);
    }

    @Override
    public SkillBonus<EventListenerBonus<InflictIgniteBonus>> merge(SkillBonus<?> other) {
        if (!(other instanceof InflictIgniteBonus)) {
            throw new IllegalArgumentException();
        }
        InflictIgniteBonus otherBonus = (InflictIgniteBonus)other;
        return new InflictIgniteBonus(otherBonus.chance + this.chance, this.duration, this.eventListener);
    }

    @Override
    public MutableComponent getTooltip() {
        String durationDescription = StringUtil.m_14404_((int)(this.duration * 20));
        String targetDescription = this.eventListener.getTarget().name().toLowerCase();
        String bonusDescription = this.getDescriptionId() + "." + targetDescription;
        if (this.chance < 1.0f) {
            bonusDescription = bonusDescription + ".chance";
        }
        MutableComponent tooltip = Component.m_237110_((String)bonusDescription, (Object[])new Object[]{durationDescription});
        if (this.chance < 1.0f) {
            tooltip = TooltipHelper.getSkillBonusTooltip((Component)tooltip, (double)this.chance, AttributeModifier.Operation.MULTIPLY_BASE);
        }
        tooltip = this.eventListener.getTooltip((Component)tooltip);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.chance > 0.0f ^ this.eventListener.getTarget() == SkillBonus.Target.PLAYER;
    }

    @Override
    public SkillEventListener getEventListener() {
        return this.eventListener;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<EventListenerBonus<InflictIgniteBonus>> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.addLabel(110, 0, "Duration", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, this.chance).setNumericResponder(value -> this.selectChance(consumer, (Double)value));
        editor.addNumericTextField(110, 0, 90, 14, this.duration).setNumericResponder(value -> this.selectDuration(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.eventListener).setResponder(eventListener -> this.selectEventListener(editor, consumer, (SkillEventListener)eventListener)).setMenuInitFunc(() -> this.addEventListenerWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictIgniteBonus>> consumer) {
        this.eventListener.addEditorWidgets(editor, eventListener -> {
            this.setEventListener((SkillEventListener)eventListener);
            consumer.accept(this.copy());
        });
    }

    private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictIgniteBonus>> consumer, SkillEventListener eventListener) {
        this.setEventListener(eventListener);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectDuration(Consumer<EventListenerBonus<InflictIgniteBonus>> consumer, Double value) {
        this.setDuration(value.intValue());
        consumer.accept(this.copy());
    }

    private void selectChance(Consumer<EventListenerBonus<InflictIgniteBonus>> consumer, Double value) {
        this.setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setEventListener(SkillEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public InflictIgniteBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            int duration = SerializationHelper.getElement(json, "duration").getAsInt();
            InflictIgniteBonus bonus = new InflictIgniteBonus(chance, duration);
            bonus.eventListener = SerializationHelper.deserializeEventListener(json);
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictIgniteBonus)) {
                throw new IllegalArgumentException();
            }
            InflictIgniteBonus aBonus = (InflictIgniteBonus)bonus;
            json.addProperty("chance", (Number)Float.valueOf(aBonus.chance));
            json.addProperty("duration", (Number)aBonus.duration);
            SerializationHelper.serializeEventListener(json, aBonus.eventListener);
        }

        @Override
        public InflictIgniteBonus deserialize(CompoundTag tag) {
            float chance = tag.m_128457_("chance");
            int duration = tag.m_128451_("duration");
            InflictIgniteBonus bonus = new InflictIgniteBonus(chance, duration);
            bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictIgniteBonus)) {
                throw new IllegalArgumentException();
            }
            InflictIgniteBonus aBonus = (InflictIgniteBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("chance", aBonus.chance);
            tag.m_128405_("duration", aBonus.duration);
            SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
            return tag;
        }

        @Override
        public InflictIgniteBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            int duration = buf.readInt();
            InflictIgniteBonus bonus = new InflictIgniteBonus(amount, duration);
            bonus.eventListener = NetworkHelper.readEventListener(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictIgniteBonus)) {
                throw new IllegalArgumentException();
            }
            InflictIgniteBonus aBonus = (InflictIgniteBonus)bonus;
            buf.writeFloat(aBonus.chance);
            buf.writeInt(aBonus.duration);
            NetworkHelper.writeEventListener(buf, aBonus.eventListener);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new InflictIgniteBonus(0.05f, 5);
        }
    }
}

