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
 *  net.minecraft.world.damagesource.DamageSource
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
import daripher.skilltree.skill.bonus.condition.damage.DamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.NoneDamageCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class CritChanceBonus
implements SkillBonus<CritChanceBonus> {
    private float chance;
    @Nonnull
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingMultiplier targetMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
    @Nonnull
    private LivingCondition targetCondition = NoneLivingCondition.INSTANCE;
    @Nonnull
    private DamageCondition damageCondition = NoneDamageCondition.INSTANCE;

    public CritChanceBonus(float chance) {
        this.chance = chance;
    }

    public float getChanceBonus(DamageSource source, Player attacker, LivingEntity target) {
        if (!this.damageCondition.met(source)) {
            return 0.0f;
        }
        if (!this.playerCondition.isConditionMet((LivingEntity)attacker)) {
            return 0.0f;
        }
        if (!this.targetCondition.isConditionMet(target)) {
            return 0.0f;
        }
        return this.chance * this.playerMultiplier.getValue((LivingEntity)attacker);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.CRIT_CHANCE.get();
    }

    public CritChanceBonus copy() {
        CritChanceBonus bonus = new CritChanceBonus(this.chance);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.targetMultiplier = this.targetMultiplier;
        bonus.playerCondition = this.playerCondition;
        bonus.damageCondition = this.damageCondition;
        bonus.targetCondition = this.targetCondition;
        return bonus;
    }

    @Override
    public CritChanceBonus multiply(double multiplier) {
        this.chance *= (float)multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof CritChanceBonus)) {
            return false;
        }
        CritChanceBonus otherBonus = (CritChanceBonus)other;
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.targetMultiplier, this.targetMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) {
            return false;
        }
        if (!Objects.equals(otherBonus.damageCondition, this.damageCondition)) {
            return false;
        }
        return Objects.equals(otherBonus.targetCondition, this.targetCondition);
    }

    @Override
    public SkillBonus<CritChanceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof CritChanceBonus)) {
            throw new IllegalArgumentException();
        }
        CritChanceBonus otherBonus = (CritChanceBonus)other;
        CritChanceBonus mergedBonus = new CritChanceBonus(otherBonus.chance + this.chance);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.targetMultiplier = this.targetMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        mergedBonus.damageCondition = this.damageCondition;
        mergedBonus.targetCondition = this.targetCondition;
        return mergedBonus;
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent tooltip;
        AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_BASE;
        if (this.damageCondition == NoneDamageCondition.INSTANCE) {
            tooltip = TooltipHelper.getSkillBonusTooltip(this.getDescriptionId(), (double)this.chance, operation);
        } else {
            tooltip = Component.m_237110_((String)(this.getDescriptionId() + ".damage"), (Object[])new Object[]{this.damageCondition.getTooltip("type")});
            tooltip = TooltipHelper.getSkillBonusTooltip((Component)tooltip, (double)this.chance, operation);
        }
        tooltip = this.playerMultiplier.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        tooltip = this.targetMultiplier.getTooltip(tooltip, SkillBonus.Target.ENEMY);
        tooltip = this.playerCondition.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        tooltip = this.targetCondition.getTooltip(tooltip, SkillBonus.Target.ENEMY);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.chance > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<CritChanceBonus> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.chance).setNumericResponder(value -> this.selectChance(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Damage Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.damageCondition).setResponder(condition -> this.selectDamageCondition(consumer, (DamageCondition)condition));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerCondition).setResponder(condition -> this.selectPlayerCondition(editor, consumer, (LivingCondition)condition)).setMenuInitFunc(() -> this.addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Target Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.targetCondition).setResponder(condition -> this.selectTargetCondition(editor, consumer, (LivingCondition)condition)).setMenuInitFunc(() -> this.addTargetConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerMultiplier).setResponder(multiplier -> this.selectPlayerMultiplier(editor, consumer, (LivingMultiplier)multiplier)).setMenuInitFunc(() -> this.addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Target Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.targetMultiplier).setResponder(multiplier -> this.selectTargetMultiplier(editor, consumer, (LivingMultiplier)multiplier)).setMenuInitFunc(() -> this.addTargetMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addTargetMultiplierWidgets(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer) {
        this.targetMultiplier.addEditorWidgets(editor, multiplier -> {
            this.setEnemyMultiplier((LivingMultiplier)multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectTargetMultiplier(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer, LivingMultiplier multiplier) {
        this.setEnemyMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, multiplier -> {
            this.setPlayerMultiplier((LivingMultiplier)multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer, LivingMultiplier multiplier) {
        this.setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addTargetConditionWidgets(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer) {
        this.targetCondition.addEditorWidgets(editor, c -> {
            this.setTargetCondition((LivingCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void selectTargetCondition(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer, LivingCondition condition) {
        this.setTargetCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer) {
        this.playerCondition.addEditorWidgets(editor, c -> {
            this.setPlayerCondition((LivingCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<CritChanceBonus> consumer, LivingCondition condition) {
        this.setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectDamageCondition(Consumer<CritChanceBonus> consumer, DamageCondition condition) {
        this.setDamageCondition(condition);
        consumer.accept(this.copy());
    }

    private void selectChance(Consumer<CritChanceBonus> consumer, Double value) {
        this.setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public SkillBonus<?> setPlayerCondition(LivingCondition condition) {
        this.playerCondition = condition;
        return this;
    }

    public SkillBonus<?> setDamageCondition(DamageCondition condition) {
        this.damageCondition = condition;
        return this;
    }

    public SkillBonus<?> setTargetCondition(LivingCondition condition) {
        this.targetCondition = condition;
        return this;
    }

    public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
        this.playerMultiplier = multiplier;
        return this;
    }

    public SkillBonus<?> setEnemyMultiplier(@Nonnull LivingMultiplier multiplier) {
        this.targetMultiplier = multiplier;
        return this;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public CritChanceBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = SerializationHelper.getElement(json, "chance").getAsFloat();
            CritChanceBonus bonus = new CritChanceBonus(amount);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.targetMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.damageCondition = SerializationHelper.deserializeDamageCondition(json);
            bonus.targetCondition = SerializationHelper.deserializeLivingCondition(json, "target_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof CritChanceBonus)) {
                throw new IllegalArgumentException();
            }
            CritChanceBonus aBonus = (CritChanceBonus)bonus;
            json.addProperty("chance", (Number)Float.valueOf(aBonus.chance));
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingMultiplier(json, aBonus.targetMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeDamageCondition(json, aBonus.damageCondition);
            SerializationHelper.serializeLivingCondition(json, aBonus.targetCondition, "target_condition");
        }

        @Override
        public CritChanceBonus deserialize(CompoundTag tag) {
            float amount = tag.m_128457_("chance");
            CritChanceBonus bonus = new CritChanceBonus(amount);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.targetMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.damageCondition = SerializationHelper.deserializeDamageCondition(tag);
            bonus.targetCondition = SerializationHelper.deserializeLivingCondition(tag, "target_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof CritChanceBonus)) {
                throw new IllegalArgumentException();
            }
            CritChanceBonus aBonus = (CritChanceBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("chance", aBonus.chance);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.targetMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeDamageCondition(tag, aBonus.damageCondition);
            SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
            return tag;
        }

        @Override
        public CritChanceBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            CritChanceBonus bonus = new CritChanceBonus(amount);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.targetMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.damageCondition = NetworkHelper.readDamageCondition(buf);
            bonus.targetCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof CritChanceBonus)) {
                throw new IllegalArgumentException();
            }
            CritChanceBonus aBonus = (CritChanceBonus)bonus;
            buf.writeFloat(aBonus.chance);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.targetMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeDamageCondition(buf, aBonus.damageCondition);
            NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new CritChanceBonus(0.05f);
        }
    }
}

