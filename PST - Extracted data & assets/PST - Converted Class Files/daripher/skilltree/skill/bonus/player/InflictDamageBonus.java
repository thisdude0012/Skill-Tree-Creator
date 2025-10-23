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
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 */
package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTDamageConditions;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.MagicDamageCondition;
import daripher.skilltree.skill.bonus.event.BlockEventListener;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class InflictDamageBonus
implements EventListenerBonus<InflictDamageBonus> {
    private float chance;
    private float damage;
    private SkillEventListener eventListener;
    private DamageCondition damageType;

    public InflictDamageBonus(float chance, float damage, SkillEventListener eventListener, DamageCondition damageType) {
        this.chance = chance;
        this.damage = damage;
        this.eventListener = eventListener;
        this.damageType = damageType;
    }

    public InflictDamageBonus(float chance, float damage) {
        this(chance, damage, new BlockEventListener(), new MagicDamageCondition());
    }

    @Override
    public void applyEffect(LivingEntity target) {
        target.m_6469_(target.m_9236_().m_269111_().m_269425_(), this.damage);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.INFLICT_DAMAGE.get();
    }

    public InflictDamageBonus copy() {
        return new InflictDamageBonus(this.chance, this.damage, this.eventListener, this.damageType);
    }

    @Override
    public InflictDamageBonus multiply(double multiplier) {
        if (this.chance < 1.0f) {
            this.chance *= (float)multiplier;
        } else {
            this.damage *= (float)multiplier;
        }
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof InflictDamageBonus)) {
            return false;
        }
        InflictDamageBonus otherBonus = (InflictDamageBonus)other;
        if (otherBonus.chance < 1.0f && this.chance < 1.0f && otherBonus.damage != this.damage) {
            return false;
        }
        if (!Objects.equals(otherBonus.eventListener, this.eventListener)) {
            return false;
        }
        return Objects.equals(this.damageType, otherBonus.damageType);
    }

    @Override
    public SkillBonus<EventListenerBonus<InflictDamageBonus>> merge(SkillBonus<?> other) {
        if (!(other instanceof InflictDamageBonus)) {
            throw new IllegalArgumentException();
        }
        InflictDamageBonus otherBonus = (InflictDamageBonus)other;
        if (otherBonus.chance < 1.0f && this.chance < 1.0f) {
            return new InflictDamageBonus(otherBonus.chance + this.chance, this.damage, this.eventListener, this.damageType);
        }
        return new InflictDamageBonus(this.chance, otherBonus.damage + this.damage, this.eventListener, this.damageType);
    }

    @Override
    public MutableComponent getTooltip() {
        String targetDescription = this.eventListener.getTarget().getName();
        String key = this.getDescriptionId() + "." + targetDescription;
        String damageDescription = TooltipHelper.formatNumber(this.damage);
        MutableComponent damageTypeDescription = this.damageType.getTooltip();
        if (this.chance < 1.0f) {
            key = key + ".chance";
        }
        MutableComponent tooltip = Component.m_237110_((String)key, (Object[])new Object[]{damageDescription, damageTypeDescription});
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
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<EventListenerBonus<InflictDamageBonus>> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.addLabel(110, 0, "Damage", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, this.chance).setNumericResponder(value -> this.selectChance(consumer, (Double)value));
        editor.addNumericTextField(110, 0, 90, 14, this.damage).setNumericResponder(value -> this.selectDamage(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Damage Type", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        List damageTypes = PSTDamageConditions.conditionsList().stream().filter(DamageCondition::canCreateDamageSource).toList();
        editor.addSelectionMenu(0, 0, 200, damageTypes).setValue(this.damageType).setElementNameGetter(c -> Component.m_237115_((String)PSTDamageConditions.getName(c))).setResponder(damageType -> this.selectDamageType(editor, consumer, (DamageCondition)damageType));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.eventListener).setResponder(eventListener -> this.selectEventListener(editor, consumer, (SkillEventListener)eventListener)).setMenuInitFunc(() -> this.addEventListenerWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictDamageBonus>> consumer) {
        this.eventListener.addEditorWidgets(editor, eventListener -> {
            this.setEventListener((SkillEventListener)eventListener);
            consumer.accept(this.copy());
        });
    }

    private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictDamageBonus>> consumer, SkillEventListener eventListener) {
        this.setEventListener(eventListener);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectDamageType(SkillTreeEditor editor, Consumer<EventListenerBonus<InflictDamageBonus>> consumer, DamageCondition damageType) {
        this.setDamageType(damageType);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectDamage(Consumer<EventListenerBonus<InflictDamageBonus>> consumer, Double value) {
        this.setDamage(value.intValue());
        consumer.accept(this.copy());
    }

    private void selectChance(Consumer<EventListenerBonus<InflictDamageBonus>> consumer, Double value) {
        this.setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setEventListener(SkillEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setDamageType(DamageCondition damageType) {
        this.damageType = damageType;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public InflictDamageBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = json.get("chance").getAsFloat();
            float damage = json.get("damage").getAsInt();
            InflictDamageBonus bonus = new InflictDamageBonus(chance, damage);
            bonus.eventListener = SerializationHelper.deserializeEventListener(json);
            if (json.has("damage_type")) {
                bonus.damageType = SerializationHelper.deserializeDamageCondition(json, "damage_type");
            }
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictDamageBonus)) {
                throw new IllegalArgumentException();
            }
            InflictDamageBonus aBonus = (InflictDamageBonus)bonus;
            json.addProperty("chance", (Number)Float.valueOf(aBonus.chance));
            json.addProperty("damage", (Number)Float.valueOf(aBonus.damage));
            SerializationHelper.serializeEventListener(json, aBonus.eventListener);
            SerializationHelper.serializeDamageCondition(json, aBonus.damageType, "damage_type");
        }

        @Override
        public InflictDamageBonus deserialize(CompoundTag tag) {
            float chance = tag.m_128457_("chance");
            float damage = tag.m_128457_("damage");
            InflictDamageBonus bonus = new InflictDamageBonus(chance, damage);
            bonus.eventListener = SerializationHelper.deserializeEventListener(tag);
            if (tag.m_128441_("damage_type")) {
                bonus.damageType = SerializationHelper.deserializeDamageCondition(tag, "damage_type");
            }
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictDamageBonus)) {
                throw new IllegalArgumentException();
            }
            InflictDamageBonus aBonus = (InflictDamageBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("chance", aBonus.chance);
            tag.m_128350_("damage", aBonus.damage);
            SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
            SerializationHelper.serializeDamageCondition(tag, aBonus.damageType, "damage_type");
            return tag;
        }

        @Override
        public InflictDamageBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            float damage = buf.readFloat();
            InflictDamageBonus bonus = new InflictDamageBonus(amount, damage);
            bonus.eventListener = NetworkHelper.readEventListener(buf);
            bonus.damageType = NetworkHelper.readDamageCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof InflictDamageBonus)) {
                throw new IllegalArgumentException();
            }
            InflictDamageBonus aBonus = (InflictDamageBonus)bonus;
            buf.writeFloat(aBonus.chance);
            buf.writeFloat(aBonus.damage);
            NetworkHelper.writeEventListener(buf, aBonus.eventListener);
            NetworkHelper.writeDamageCondition(buf, aBonus.damageType);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new InflictDamageBonus(0.05f, 5.0f);
        }
    }
}

