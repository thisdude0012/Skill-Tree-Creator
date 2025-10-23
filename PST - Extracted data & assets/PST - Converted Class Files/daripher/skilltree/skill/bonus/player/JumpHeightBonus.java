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

public final class JumpHeightBonus
implements SkillBonus<JumpHeightBonus> {
    @Nonnull
    private LivingCondition playerCondition;
    private float multiplier;

    public JumpHeightBonus(@Nonnull LivingCondition playerCondition, float multiplier) {
        this.playerCondition = playerCondition;
        this.multiplier = multiplier;
    }

    public JumpHeightBonus(float multiplier) {
        this(NoneLivingCondition.INSTANCE, multiplier);
    }

    public float getJumpHeightMultiplier(Player player) {
        if (!this.playerCondition.isConditionMet((LivingEntity)player)) {
            return 0.0f;
        }
        return this.multiplier;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.JUMP_HEIGHT.get();
    }

    public JumpHeightBonus copy() {
        return new JumpHeightBonus(this.playerCondition, this.multiplier);
    }

    @Override
    public JumpHeightBonus multiply(double multiplier) {
        this.multiplier = (float)((double)this.multiplier * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof JumpHeightBonus)) {
            return false;
        }
        JumpHeightBonus otherBonus = (JumpHeightBonus)other;
        return Objects.equals(otherBonus.playerCondition, this.playerCondition);
    }

    @Override
    public SkillBonus<JumpHeightBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof JumpHeightBonus)) {
            throw new IllegalArgumentException();
        }
        JumpHeightBonus otherBonus = (JumpHeightBonus)other;
        return new JumpHeightBonus(this.playerCondition, otherBonus.multiplier + this.multiplier);
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent tooltip = TooltipHelper.getSkillBonusTooltip(this.getDescriptionId(), (double)this.multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
        tooltip = this.playerCondition.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.multiplier > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<JumpHeightBonus> consumer) {
        editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.multiplier).setNumericResponder(value -> this.selectMultiplier(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerCondition).setResponder(condition -> this.selectPlayerCondition(editor, consumer, (LivingCondition)condition)).setMenuInitFunc(() -> this.addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<JumpHeightBonus> consumer) {
        this.playerCondition.addEditorWidgets(editor, condition -> {
            this.setPlayerCondition((LivingCondition)condition);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<JumpHeightBonus> consumer, LivingCondition condition) {
        this.setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectMultiplier(Consumer<JumpHeightBonus> consumer, Double value) {
        this.setMultiplier(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setPlayerCondition(@Nonnull LivingCondition playerCondition) {
        this.playerCondition = playerCondition;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        JumpHeightBonus that = (JumpHeightBonus)obj;
        if (!Objects.equals(this.playerCondition, that.playerCondition)) {
            return false;
        }
        return this.multiplier == that.multiplier;
    }

    public int hashCode() {
        return Objects.hash(this.playerCondition, Float.valueOf(this.multiplier));
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public JumpHeightBonus deserialize(JsonObject json) throws JsonParseException {
            LivingCondition condition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
            return new JumpHeightBonus(condition, multiplier);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof JumpHeightBonus)) {
                throw new IllegalArgumentException();
            }
            JumpHeightBonus aBonus = (JumpHeightBonus)bonus;
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            json.addProperty("multiplier", (Number)Float.valueOf(aBonus.multiplier));
        }

        @Override
        public JumpHeightBonus deserialize(CompoundTag tag) {
            LivingCondition condition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            float multiplier = tag.m_128457_("multiplier");
            return new JumpHeightBonus(condition, multiplier);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof JumpHeightBonus)) {
                throw new IllegalArgumentException();
            }
            JumpHeightBonus aBonus = (JumpHeightBonus)bonus;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            tag.m_128350_("multiplier", aBonus.multiplier);
            return tag;
        }

        @Override
        public JumpHeightBonus deserialize(FriendlyByteBuf buf) {
            return new JumpHeightBonus(NetworkHelper.readLivingCondition(buf), buf.readFloat());
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof JumpHeightBonus)) {
                throw new IllegalArgumentException();
            }
            JumpHeightBonus aBonus = (JumpHeightBonus)bonus;
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            buf.writeFloat(aBonus.multiplier);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new JumpHeightBonus(0.1f);
        }
    }
}

