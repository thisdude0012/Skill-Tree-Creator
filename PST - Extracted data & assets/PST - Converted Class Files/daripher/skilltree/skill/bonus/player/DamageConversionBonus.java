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
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.NotNull
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
import daripher.skilltree.skill.bonus.condition.damage.MagicDamageCondition;
import daripher.skilltree.skill.bonus.condition.damage.MeleeDamageCondition;
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
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public final class DamageConversionBonus
implements SkillBonus<DamageConversionBonus> {
    private float amount;
    @Nonnull
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingMultiplier targetMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
    @Nonnull
    private LivingCondition targetCondition = NoneLivingCondition.INSTANCE;
    @Nonnull
    private DamageCondition originalDamageCondition;
    @Nonnull
    private DamageCondition resultDamageCondition;

    public DamageConversionBonus(float amount, @NotNull DamageCondition originalDamageCondition, @NotNull DamageCondition resultDamageCondition) {
        this.amount = amount;
        this.originalDamageCondition = originalDamageCondition;
        this.resultDamageCondition = resultDamageCondition;
    }

    public float getConversionRate(DamageSource source, Player player, LivingEntity target) {
        if (!this.originalDamageCondition.met(source)) {
            return 0.0f;
        }
        if (!this.playerCondition.isConditionMet((LivingEntity)player)) {
            return 0.0f;
        }
        if (!this.targetCondition.isConditionMet(target)) {
            return 0.0f;
        }
        return this.amount * this.playerMultiplier.getValue((LivingEntity)player) * this.targetMultiplier.getValue(target);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.DAMAGE_CONVERSION.get();
    }

    public DamageConversionBonus copy() {
        DamageConversionBonus bonus = new DamageConversionBonus(this.amount, this.originalDamageCondition, this.resultDamageCondition);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.targetMultiplier = this.targetMultiplier;
        bonus.playerCondition = this.playerCondition;
        bonus.targetCondition = this.targetCondition;
        return bonus;
    }

    @Override
    public DamageConversionBonus multiply(double multiplier) {
        this.amount *= (float)multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof DamageConversionBonus)) {
            return false;
        }
        DamageConversionBonus otherBonus = (DamageConversionBonus)other;
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.targetMultiplier, this.targetMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) {
            return false;
        }
        if (!Objects.equals(otherBonus.originalDamageCondition, this.originalDamageCondition)) {
            return false;
        }
        if (!Objects.equals(otherBonus.resultDamageCondition, this.resultDamageCondition)) {
            return false;
        }
        return Objects.equals(otherBonus.targetCondition, this.targetCondition);
    }

    @Override
    public SkillBonus<DamageConversionBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof DamageConversionBonus)) {
            throw new IllegalArgumentException();
        }
        DamageConversionBonus otherBonus = (DamageConversionBonus)other;
        float mergedAmount = otherBonus.amount + this.amount;
        DamageConversionBonus mergedBonus = new DamageConversionBonus(mergedAmount, this.originalDamageCondition, this.resultDamageCondition);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.targetMultiplier = this.targetMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        mergedBonus.targetCondition = this.targetCondition;
        return mergedBonus;
    }

    @Override
    public MutableComponent getTooltip() {
        String formattedAmount = TooltipHelper.formatNumber(this.amount * 100.0f);
        MutableComponent tooltip = Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{formattedAmount, this.originalDamageCondition.getTooltip(), this.resultDamageCondition.getTooltip()});
        tooltip = this.playerMultiplier.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        tooltip = this.targetMultiplier.getTooltip(tooltip, SkillBonus.Target.ENEMY);
        tooltip = this.playerCondition.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        tooltip = this.targetCondition.getTooltip(tooltip, SkillBonus.Target.ENEMY);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.amount > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<DamageConversionBonus> consumer) {
        editor.addLabel(0, 0, "Conversion", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.amount).setNumericResponder(value -> this.selectAmount(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Original Damage", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.originalDamageCondition).setResponder(condition -> this.selectDamageCondition(consumer, (DamageCondition)condition));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Result Damage", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.resultDamageCondition).setResponder(condition -> this.selectResultDamageCondition(consumer, (DamageCondition)condition));
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

    private void selectAmount(Consumer<DamageConversionBonus> consumer, Double value) {
        this.setAmount(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addTargetMultiplierWidgets(SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer) {
        this.targetMultiplier.addEditorWidgets(editor, multiplier -> {
            this.setEnemyMultiplier((LivingMultiplier)multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectTargetMultiplier(SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer, LivingMultiplier multiplier) {
        this.setEnemyMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, multiplier -> {
            this.setPlayerMultiplier((LivingMultiplier)multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer, LivingMultiplier multiplier) {
        this.setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addTargetConditionWidgets(SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer) {
        this.targetCondition.addEditorWidgets(editor, c -> {
            this.setTargetCondition((LivingCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void selectTargetCondition(SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer, LivingCondition condition) {
        this.setTargetCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer) {
        this.playerCondition.addEditorWidgets(editor, c -> {
            this.setPlayerCondition((LivingCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<DamageConversionBonus> consumer, LivingCondition condition) {
        this.setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectDamageCondition(Consumer<DamageConversionBonus> consumer, DamageCondition condition) {
        this.setDamageCondition(condition);
        consumer.accept(this.copy());
    }

    private void selectResultDamageCondition(Consumer<DamageConversionBonus> consumer, DamageCondition condition) {
        this.setResultDamageCondition(condition);
        consumer.accept(this.copy());
    }

    public SkillBonus<?> setPlayerCondition(LivingCondition condition) {
        this.playerCondition = condition;
        return this;
    }

    public SkillBonus<?> setDamageCondition(DamageCondition condition) {
        this.originalDamageCondition = condition;
        return this;
    }

    public SkillBonus<?> setResultDamageCondition(DamageCondition condition) {
        this.resultDamageCondition = condition;
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

    public SkillBonus<?> setEnemyMultiplier(LivingMultiplier multiplier) {
        this.targetMultiplier = multiplier;
        return this;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Nonnull
    public DamageCondition getOriginalDamageCondition() {
        return this.originalDamageCondition;
    }

    @Nonnull
    public DamageCondition getResultDamageCondition() {
        return this.resultDamageCondition;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public DamageConversionBonus deserialize(JsonObject json) throws JsonParseException {
            float amount = SerializationHelper.getElement(json, "amount").getAsFloat();
            DamageCondition originalDamageCondition = SerializationHelper.deserializeDamageCondition(json, "original_damage");
            DamageCondition resultDamageCondition = SerializationHelper.deserializeDamageCondition(json, "result_damage");
            DamageConversionBonus bonus = new DamageConversionBonus(amount, originalDamageCondition, resultDamageCondition);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.targetMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "enemy_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.targetCondition = SerializationHelper.deserializeLivingCondition(json, "target_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof DamageConversionBonus)) {
                throw new IllegalArgumentException();
            }
            DamageConversionBonus aBonus = (DamageConversionBonus)bonus;
            json.addProperty("amount", (Number)Float.valueOf(aBonus.amount));
            SerializationHelper.serializeDamageCondition(json, aBonus.originalDamageCondition, "original_damage");
            SerializationHelper.serializeDamageCondition(json, aBonus.resultDamageCondition, "result_damage");
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingMultiplier(json, aBonus.targetMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingCondition(json, aBonus.targetCondition, "target_condition");
        }

        @Override
        public DamageConversionBonus deserialize(CompoundTag tag) {
            float amount = tag.m_128457_("amount");
            DamageCondition originalDamageCondition = SerializationHelper.deserializeDamageCondition(tag, "original_damage");
            DamageCondition resultDamageCondition = SerializationHelper.deserializeDamageCondition(tag, "result_damage");
            DamageConversionBonus bonus = new DamageConversionBonus(amount, originalDamageCondition, resultDamageCondition);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.targetMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "enemy_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.targetCondition = SerializationHelper.deserializeLivingCondition(tag, "target_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof DamageConversionBonus)) {
                throw new IllegalArgumentException();
            }
            DamageConversionBonus aBonus = (DamageConversionBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("amount", aBonus.amount);
            SerializationHelper.serializeDamageCondition(tag, aBonus.originalDamageCondition, "original_damage");
            SerializationHelper.serializeDamageCondition(tag, aBonus.resultDamageCondition, "result_damage");
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.targetMultiplier, "enemy_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingCondition(tag, aBonus.targetCondition, "target_condition");
            return tag;
        }

        @Override
        public DamageConversionBonus deserialize(FriendlyByteBuf buf) {
            float amount = buf.readFloat();
            DamageCondition originalDamageCondition = NetworkHelper.readDamageCondition(buf);
            DamageCondition resultDamageCondition = NetworkHelper.readDamageCondition(buf);
            DamageConversionBonus bonus = new DamageConversionBonus(amount, originalDamageCondition, resultDamageCondition);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.targetMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.targetCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof DamageConversionBonus)) {
                throw new IllegalArgumentException();
            }
            DamageConversionBonus aBonus = (DamageConversionBonus)bonus;
            buf.writeFloat(aBonus.amount);
            NetworkHelper.writeDamageCondition(buf, aBonus.originalDamageCondition);
            NetworkHelper.writeDamageCondition(buf, aBonus.resultDamageCondition);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.targetMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeLivingCondition(buf, aBonus.targetCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new DamageConversionBonus(0.05f, new MeleeDamageCondition(), new MagicDamageCondition());
        }
    }
}

