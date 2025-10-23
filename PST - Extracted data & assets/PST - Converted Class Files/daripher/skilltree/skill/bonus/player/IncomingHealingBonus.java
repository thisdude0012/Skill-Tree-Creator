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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class IncomingHealingBonus
implements SkillBonus<IncomingHealingBonus> {
    private float multiplier;
    @Nonnull
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;

    public IncomingHealingBonus(float multiplier) {
        this.multiplier = multiplier;
    }

    public float getHealingMultiplier(Player player) {
        if (!this.playerCondition.isConditionMet((LivingEntity)player)) {
            return 0.0f;
        }
        return this.multiplier * this.playerMultiplier.getValue((LivingEntity)player);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.INCOMING_HEALING.get();
    }

    public IncomingHealingBonus copy() {
        IncomingHealingBonus bonus = new IncomingHealingBonus(this.multiplier);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.playerCondition = this.playerCondition;
        return bonus;
    }

    @Override
    public IncomingHealingBonus multiply(double multiplier) {
        this.multiplier = (float)((double)this.multiplier * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof IncomingHealingBonus)) {
            return false;
        }
        IncomingHealingBonus otherBonus = (IncomingHealingBonus)other;
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) {
            return false;
        }
        return otherBonus.multiplier == this.multiplier;
    }

    @Override
    public SkillBonus<IncomingHealingBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof IncomingHealingBonus)) {
            throw new IllegalArgumentException();
        }
        IncomingHealingBonus otherBonus = (IncomingHealingBonus)other;
        IncomingHealingBonus mergedBonus = new IncomingHealingBonus(this.multiplier + otherBonus.multiplier);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        return mergedBonus;
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent tooltip = TooltipHelper.getSkillBonusTooltip(this.getDescriptionId(), (double)this.multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
        tooltip = this.playerMultiplier.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        tooltip = this.playerCondition.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.multiplier > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int index, Consumer<IncomingHealingBonus> consumer) {
        editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.multiplier).setNumericResponder(value -> this.selectMultiplier(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerCondition).setResponder(condition -> this.selectPlayerCondition(editor, consumer, (LivingCondition)condition)).setMenuInitFunc(() -> this.addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerMultiplier).setResponder(multiplier -> this.selectPlayerMultiplier(editor, consumer, (LivingMultiplier)multiplier)).setMenuInitFunc(() -> this.addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<IncomingHealingBonus> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, multiplier -> {
            this.setMultiplier((LivingMultiplier)multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<IncomingHealingBonus> consumer, LivingMultiplier multiplier) {
        this.setMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<IncomingHealingBonus> consumer) {
        this.playerCondition.addEditorWidgets(editor, condition -> {
            this.setCondition((LivingCondition)condition);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<IncomingHealingBonus> consumer, LivingCondition condition) {
        this.setCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectMultiplier(Consumer<IncomingHealingBonus> consumer, Double value) {
        this.setMultiplier(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public SkillBonus<?> setCondition(LivingCondition condition) {
        this.playerCondition = condition;
        return this;
    }

    public SkillBonus<?> setMultiplier(LivingMultiplier multiplier) {
        this.playerMultiplier = multiplier;
        return this;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public IncomingHealingBonus deserialize(JsonObject json) throws JsonParseException {
            float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
            IncomingHealingBonus bonus = new IncomingHealingBonus(multiplier);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof IncomingHealingBonus)) {
                throw new IllegalArgumentException();
            }
            IncomingHealingBonus aBonus = (IncomingHealingBonus)bonus;
            json.addProperty("multiplier", (Number)Float.valueOf(aBonus.multiplier));
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
        }

        @Override
        public IncomingHealingBonus deserialize(CompoundTag tag) {
            float multiplier = tag.m_128457_("multiplier");
            IncomingHealingBonus bonus = new IncomingHealingBonus(multiplier);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof IncomingHealingBonus)) {
                throw new IllegalArgumentException();
            }
            IncomingHealingBonus aBonus = (IncomingHealingBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("multiplier", aBonus.multiplier);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            return tag;
        }

        @Override
        public IncomingHealingBonus deserialize(FriendlyByteBuf buf) {
            IncomingHealingBonus bonus = new IncomingHealingBonus(buf.readFloat());
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof IncomingHealingBonus)) {
                throw new IllegalArgumentException();
            }
            IncomingHealingBonus aBonus = (IncomingHealingBonus)bonus;
            buf.writeFloat(aBonus.multiplier);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new IncomingHealingBonus(0.15f);
        }
    }
}

