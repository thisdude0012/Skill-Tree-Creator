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
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.living.HasItemEquippedCondition;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class ProjectileDuplicationBonus
implements SkillBonus<ProjectileDuplicationBonus> {
    private float chance;
    @Nonnull
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;

    public ProjectileDuplicationBonus(float chance) {
        this.chance = chance;
    }

    public float getChance(Player player) {
        if (!this.playerCondition.isConditionMet((LivingEntity)player)) {
            return 0.0f;
        }
        return this.chance * this.playerMultiplier.getValue((LivingEntity)player);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.PROJECTILE_DUPLICATION.get();
    }

    public ProjectileDuplicationBonus copy() {
        ProjectileDuplicationBonus bonus = new ProjectileDuplicationBonus(this.chance);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.playerCondition = this.playerCondition;
        return bonus;
    }

    @Override
    public ProjectileDuplicationBonus multiply(double multiplier) {
        this.chance *= (float)multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ProjectileDuplicationBonus)) {
            return false;
        }
        ProjectileDuplicationBonus otherBonus = (ProjectileDuplicationBonus)other;
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        return Objects.equals(otherBonus.playerCondition, this.playerCondition);
    }

    @Override
    public SkillBonus<ProjectileDuplicationBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ProjectileDuplicationBonus)) {
            throw new IllegalArgumentException();
        }
        ProjectileDuplicationBonus otherBonus = (ProjectileDuplicationBonus)other;
        float mergedChance = otherBonus.chance + this.chance;
        ProjectileDuplicationBonus mergedBonus = new ProjectileDuplicationBonus(mergedChance);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        return mergedBonus;
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent tooltip = this.chance < 1.0f || this.chance % 1.0f != 0.0f ? TooltipHelper.getSkillBonusTooltip(this.getDescriptionId() + ".chance", (double)this.chance, AttributeModifier.Operation.MULTIPLY_BASE) : (this.chance == 1.0f ? Component.m_237115_((String)this.getDescriptionId()) : Component.m_237110_((String)(this.getDescriptionId() + ".amount"), (Object[])new Object[]{(int)this.chance}));
        tooltip = this.playerMultiplier.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        tooltip = this.playerCondition.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.chance > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ProjectileDuplicationBonus> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.chance).setNumericResponder(value -> this.selectChance(consumer, (Double)value));
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

    private void selectChance(Consumer<ProjectileDuplicationBonus> consumer, Double value) {
        this.setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<ProjectileDuplicationBonus> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, multiplier -> {
            this.setPlayerMultiplier((LivingMultiplier)multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<ProjectileDuplicationBonus> consumer, LivingMultiplier multiplier) {
        this.setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<ProjectileDuplicationBonus> consumer) {
        this.playerCondition.addEditorWidgets(editor, c -> {
            this.setPlayerCondition((LivingCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<ProjectileDuplicationBonus> consumer, LivingCondition condition) {
        this.setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public SkillBonus<?> setPlayerCondition(LivingCondition condition) {
        this.playerCondition = condition;
        return this;
    }

    public SkillBonus<?> setPlayerMultiplier(LivingMultiplier multiplier) {
        this.playerMultiplier = multiplier;
        return this;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public ProjectileDuplicationBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            ProjectileDuplicationBonus bonus = new ProjectileDuplicationBonus(chance);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileDuplicationBonus)) {
                throw new IllegalArgumentException();
            }
            ProjectileDuplicationBonus aBonus = (ProjectileDuplicationBonus)bonus;
            json.addProperty("chance", (Number)Float.valueOf(aBonus.chance));
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
        }

        @Override
        public ProjectileDuplicationBonus deserialize(CompoundTag tag) {
            float chance = tag.m_128457_("chance");
            ProjectileDuplicationBonus bonus = new ProjectileDuplicationBonus(chance);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileDuplicationBonus)) {
                throw new IllegalArgumentException();
            }
            ProjectileDuplicationBonus aBonus = (ProjectileDuplicationBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("chance", aBonus.chance);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            return tag;
        }

        @Override
        public ProjectileDuplicationBonus deserialize(FriendlyByteBuf buf) {
            float chance = buf.readFloat();
            ProjectileDuplicationBonus bonus = new ProjectileDuplicationBonus(chance);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileDuplicationBonus)) {
                throw new IllegalArgumentException();
            }
            ProjectileDuplicationBonus aBonus = (ProjectileDuplicationBonus)bonus;
            buf.writeFloat(aBonus.chance);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ProjectileDuplicationBonus(0.1f).setPlayerCondition(new HasItemEquippedCondition(new EquipmentCondition(EquipmentCondition.Type.RANGED_WEAPON)));
        }
    }
}

