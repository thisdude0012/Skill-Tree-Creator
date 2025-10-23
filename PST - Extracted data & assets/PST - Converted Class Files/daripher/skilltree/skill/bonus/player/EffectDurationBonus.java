/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.player.Player
 */
package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.effect.EffectType;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class EffectDurationBonus
implements SkillBonus<EffectDurationBonus> {
    private EffectType effectType;
    private float duration;
    @Nonnull
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
    private SkillBonus.Target target;
    @Nonnull
    private LivingMultiplier enemyMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingCondition enemyCondition = NoneLivingCondition.INSTANCE;

    public EffectDurationBonus(EffectType effectType, float duration, SkillBonus.Target target) {
        this.effectType = effectType;
        this.duration = duration;
        this.target = target;
    }

    public float getDuration(@Nullable Player effectSource, LivingEntity entity) {
        if (this.target == SkillBonus.Target.PLAYER) {
            if (!this.playerCondition.isConditionMet(entity)) {
                return 0.0f;
            }
            return this.duration * this.playerMultiplier.getValue(entity);
        }
        if (!this.enemyCondition.isConditionMet(entity)) {
            return 0.0f;
        }
        float duration = this.duration;
        if (effectSource != null && !this.playerCondition.isConditionMet((LivingEntity)effectSource)) {
            return 0.0f;
        }
        return duration * this.playerMultiplier.getValue(entity) * this.enemyMultiplier.getValue(entity);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.EFFECT_DURATION.get();
    }

    public EffectDurationBonus copy() {
        EffectDurationBonus bonus = new EffectDurationBonus(this.effectType, this.duration, this.target);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.playerCondition = this.playerCondition;
        bonus.enemyCondition = this.enemyCondition;
        bonus.enemyMultiplier = this.enemyMultiplier;
        return bonus;
    }

    @Override
    public EffectDurationBonus multiply(double multiplier) {
        this.duration = (float)((double)this.duration * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof EffectDurationBonus)) {
            return false;
        }
        EffectDurationBonus otherBonus = (EffectDurationBonus)other;
        if (otherBonus.playerCondition != this.playerCondition) {
            return false;
        }
        if (otherBonus.playerMultiplier != this.playerMultiplier) {
            return false;
        }
        if (otherBonus.target != this.target) {
            return false;
        }
        if (otherBonus.enemyCondition != this.enemyCondition) {
            return false;
        }
        if (otherBonus.enemyMultiplier != this.enemyMultiplier) {
            return false;
        }
        return otherBonus.effectType == this.effectType;
    }

    @Override
    public SkillBonus<EffectDurationBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof EffectDurationBonus)) {
            throw new IllegalArgumentException();
        }
        EffectDurationBonus otherBonus = (EffectDurationBonus)other;
        EffectDurationBonus mergedBonus = new EffectDurationBonus(this.effectType, this.duration + otherBonus.duration, this.target);
        mergedBonus.playerCondition = this.playerCondition;
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.enemyCondition = this.enemyCondition;
        mergedBonus.enemyMultiplier = this.enemyMultiplier;
        return mergedBonus;
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent effectTypeDescription = Component.m_237115_((String)(this.effectType.getDescriptionId() + ".plural"));
        String key = this.getDescriptionId() + "." + this.target.getName();
        MutableComponent tooltip = Component.m_237110_((String)key, (Object[])new Object[]{effectTypeDescription});
        tooltip = TooltipHelper.getSkillBonusTooltip((Component)tooltip, (double)this.duration, AttributeModifier.Operation.MULTIPLY_BASE);
        tooltip = this.playerMultiplier.getTooltip(tooltip, this.target);
        tooltip = this.playerCondition.getTooltip(tooltip, this.target);
        tooltip = this.enemyMultiplier.getTooltip(tooltip, this.target);
        tooltip = this.enemyCondition.getTooltip(tooltip, this.target);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.duration > 0.0f ^ this.target == SkillBonus.Target.PLAYER ^ this.effectType != EffectType.HARMFUL;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int index, Consumer<EffectDurationBonus> consumer) {
        editor.addLabel(0, 0, "Effect Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.effectType).setElementNameGetter(effectType -> Component.m_237113_((String)effectType.name())).setResponder(effectType -> this.selectEffectType(consumer, (EffectType)((Object)effectType)));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Duration", ChatFormatting.GOLD);
        editor.addLabel(65, 0, "Target", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.duration).setNumericResponder(value -> this.selectDuration(consumer, (Double)value));
        editor.addSelection(65, 0, 50, 1, this.target).setNameGetter(target -> Component.m_237113_((String)target.toString())).setResponder(target -> this.selectTarget(editor, consumer, (SkillBonus.Target)((Object)target)));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerCondition).setResponder(condition -> this.selectPlayerCondition(editor, consumer, (LivingCondition)condition)).setMenuInitFunc(() -> this.addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerMultiplier).setResponder(multiplier -> this.selectPlayerMultiplier(editor, consumer, (LivingMultiplier)multiplier)).setMenuInitFunc(() -> this.addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
        if (this.target == SkillBonus.Target.ENEMY) {
            editor.addLabel(0, 0, "Enemy Condition", ChatFormatting.GOLD);
            editor.increaseHeight(19);
            editor.addSelectionMenu(0, 0, 200, this.enemyCondition).setResponder(condition -> this.selectEnemyCondition(editor, consumer, (LivingCondition)condition)).setMenuInitFunc(() -> this.addEnemyConditionWidgets(editor, consumer));
            editor.increaseHeight(19);
            editor.addLabel(0, 0, "Enemy Multiplier", ChatFormatting.GOLD);
            editor.increaseHeight(19);
            editor.addSelectionMenu(0, 0, 200, this.enemyMultiplier).setResponder(multiplier -> this.selectEnemyMultiplier(editor, consumer, (LivingMultiplier)multiplier)).setMenuInitFunc(() -> this.addEnemyMultiplierWidgets(editor, consumer));
            editor.increaseHeight(19);
        }
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, LivingMultiplier multiplier) {
        this.setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, LivingCondition condition) {
        this.setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectEnemyMultiplier(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, LivingMultiplier multiplier) {
        this.setEnemyMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectEnemyCondition(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, LivingCondition condition) {
        this.setEnemyCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectEffectType(Consumer<EffectDurationBonus> consumer, EffectType effectType) {
        this.setEffectType(effectType);
        consumer.accept(this.copy());
    }

    private void selectDuration(Consumer<EffectDurationBonus> consumer, Double duration) {
        this.setDuration(duration.floatValue());
        consumer.accept(this.copy());
    }

    private void selectTarget(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer, SkillBonus.Target target) {
        this.setTarget(target);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer) {
        this.playerCondition.addEditorWidgets(editor, c -> {
            this.setPlayerCondition((LivingCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, m -> {
            this.setPlayerMultiplier((LivingMultiplier)m);
            consumer.accept(this.copy());
        });
    }

    private void addEnemyConditionWidgets(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer) {
        this.enemyCondition.addEditorWidgets(editor, c -> {
            this.setPlayerCondition((LivingCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void addEnemyMultiplierWidgets(SkillTreeEditor editor, Consumer<EffectDurationBonus> consumer) {
        this.enemyMultiplier.addEditorWidgets(editor, m -> {
            this.setPlayerMultiplier((LivingMultiplier)m);
            consumer.accept(this.copy());
        });
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setEffectType(EffectType effectType) {
        this.effectType = effectType;
    }

    public SkillBonus<?> setPlayerCondition(LivingCondition condition) {
        this.playerCondition = condition;
        return this;
    }

    public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
        this.playerMultiplier = multiplier;
        return this;
    }

    public void setTarget(SkillBonus.Target target) {
        this.target = target;
    }

    public void setEnemyCondition(@Nonnull LivingCondition enemyCondition) {
        this.enemyCondition = enemyCondition;
    }

    public void setEnemyMultiplier(@Nonnull LivingMultiplier enemyMultiplier) {
        this.enemyMultiplier = enemyMultiplier;
    }

    public SkillBonus.Target getTarget() {
        return this.target;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public EffectDurationBonus deserialize(JsonObject json) throws JsonParseException {
            EffectType effectType = EffectType.fromName(json.get("effect_type").getAsString());
            float duration = json.get("duration").getAsFloat();
            SkillBonus.Target target = SkillBonus.Target.fromName(json.get("target").getAsString());
            EffectDurationBonus bonus = new EffectDurationBonus(effectType, duration, target);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.enemyMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier");
            bonus.enemyCondition = SerializationHelper.deserializeLivingCondition(json, "enemy_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof EffectDurationBonus)) {
                throw new IllegalArgumentException();
            }
            EffectDurationBonus aBonus = (EffectDurationBonus)bonus;
            json.addProperty("effect_type", aBonus.effectType.getName());
            json.addProperty("duration", (Number)Float.valueOf(aBonus.duration));
            json.addProperty("target", aBonus.target.getName());
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(json, aBonus.enemyMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.enemyCondition, "enemy_condition");
        }

        @Override
        public EffectDurationBonus deserialize(CompoundTag tag) {
            EffectType effectType = EffectType.fromName(tag.m_128461_("effect_type"));
            float duration = tag.m_128457_("duration");
            SkillBonus.Target target = SkillBonus.Target.fromName(tag.m_128461_("target"));
            EffectDurationBonus bonus = new EffectDurationBonus(effectType, duration, target);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.enemyMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier");
            bonus.enemyCondition = SerializationHelper.deserializeLivingCondition(tag, "enemy_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof EffectDurationBonus)) {
                throw new IllegalArgumentException();
            }
            EffectDurationBonus aBonus = (EffectDurationBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128359_("effect_type", aBonus.effectType.getName());
            tag.m_128350_("duration", aBonus.duration);
            tag.m_128359_("target", aBonus.target.getName());
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.enemyMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.enemyCondition, "enemy_condition");
            return tag;
        }

        @Override
        public EffectDurationBonus deserialize(FriendlyByteBuf buf) {
            EffectType effectType = EffectType.values()[buf.readInt()];
            float duration = buf.readFloat();
            SkillBonus.Target target = SkillBonus.Target.values()[buf.readInt()];
            EffectDurationBonus bonus = new EffectDurationBonus(effectType, duration, target);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.enemyMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.enemyCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof EffectDurationBonus)) {
                throw new IllegalArgumentException();
            }
            EffectDurationBonus aBonus = (EffectDurationBonus)bonus;
            buf.writeInt(aBonus.effectType.ordinal());
            buf.writeFloat(aBonus.duration);
            buf.writeInt(aBonus.target.ordinal());
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.enemyMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.enemyCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new EffectDurationBonus(EffectType.BENEFICIAL, 0.1f, SkillBonus.Target.PLAYER);
        }
    }
}

