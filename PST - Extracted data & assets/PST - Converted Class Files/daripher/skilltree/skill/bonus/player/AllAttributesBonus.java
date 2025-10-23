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
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.registries.ForgeRegistries
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
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;

public final class AllAttributesBonus
implements SkillBonus<AllAttributesBonus>,
TickingSkillBonus {
    private static final Set<Attribute> AFFECTED_ATTRIBUTES = new HashSet<Attribute>();
    private AttributeModifier modifier;
    @Nonnull
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;

    public AllAttributesBonus(AttributeModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public void onSkillLearned(ServerPlayer player, boolean firstTime) {
        if (this.playerCondition != NoneLivingCondition.INSTANCE || this.playerMultiplier != NoneLivingMultiplier.INSTANCE) {
            return;
        }
        AllAttributesBonus.getAffectedAttributes().stream().map(arg_0 -> ((ServerPlayer)player).m_21051_(arg_0)).filter(Objects::nonNull).filter(a -> !a.m_22109_(this.modifier)).forEach(a -> this.applyAttributeModifier((AttributeInstance)a, this.modifier, (Player)player));
    }

    @Override
    public void onSkillRemoved(ServerPlayer player) {
        AllAttributesBonus.getAffectedAttributes().stream().map(arg_0 -> ((ServerPlayer)player).m_21051_(arg_0)).filter(Objects::nonNull).filter(a -> !a.m_22109_(this.modifier)).forEach(a -> a.m_22120_(this.modifier.m_22209_()));
    }

    @Override
    public void tick(ServerPlayer player) {
        if (this.playerCondition == NoneLivingCondition.INSTANCE && this.playerMultiplier == NoneLivingMultiplier.INSTANCE) {
            return;
        }
        if (this.playerCondition != NoneLivingCondition.INSTANCE && !this.playerCondition.isConditionMet((LivingEntity)player)) {
            this.onSkillRemoved(player);
            return;
        }
        if (this.playerMultiplier != NoneLivingMultiplier.INSTANCE && this.playerMultiplier.getValue((LivingEntity)player) == 0.0f) {
            this.onSkillRemoved(player);
            return;
        }
        this.applyDynamicAttributeBonus(player);
    }

    private void applyDynamicAttributeBonus(ServerPlayer player) {
        AllAttributesBonus.getAffectedAttributes().stream().map(arg_0 -> ((ServerPlayer)player).m_21051_(arg_0)).filter(Objects::nonNull).forEach(playerAttribute -> {
            AttributeModifier oldModifier = playerAttribute.m_22111_(this.modifier.m_22209_());
            double value = this.modifier.m_22218_();
            value *= (double)this.playerMultiplier.getValue((LivingEntity)player);
            if (oldModifier != null) {
                if (oldModifier.m_22218_() == value) {
                    return;
                }
                playerAttribute.m_22120_(this.modifier.m_22209_());
            }
            AttributeModifier dynamicModifier = new AttributeModifier(this.modifier.m_22209_(), "Dynamic", value, this.modifier.m_22217_());
            this.applyAttributeModifier((AttributeInstance)playerAttribute, dynamicModifier, (Player)player);
            if (playerAttribute.m_22099_() == Attributes.f_22276_) {
                player.m_21153_(player.m_21223_());
            }
        });
    }

    private void applyAttributeModifier(AttributeInstance instance, AttributeModifier modifier, Player player) {
        float healthPercentage = player.m_21223_() / player.m_21233_();
        instance.m_22118_(modifier);
        if (AllAttributesBonus.getAffectedAttributes().contains(Attributes.f_22276_)) {
            player.m_21153_(player.m_21233_() * healthPercentage);
        }
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.ALL_ATTRIBUTES.get();
    }

    public AllAttributesBonus copy() {
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), this.modifier.m_22214_(), this.modifier.m_22218_(), this.modifier.m_22217_());
        AllAttributesBonus bonus = new AllAttributesBonus(modifier);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.playerCondition = this.playerCondition;
        return bonus;
    }

    @Override
    public AllAttributesBonus multiply(double multiplier) {
        this.modifier = new AttributeModifier(this.modifier.m_22209_(), this.modifier.m_22214_(), this.modifier.m_22218_() * multiplier, this.modifier.m_22217_());
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof AllAttributesBonus)) {
            return false;
        }
        AllAttributesBonus otherBonus = (AllAttributesBonus)other;
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) {
            return false;
        }
        return otherBonus.modifier.m_22217_() == this.modifier.m_22217_();
    }

    @Override
    public SkillBonus<AllAttributesBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof AllAttributesBonus)) {
            throw new IllegalArgumentException();
        }
        AllAttributesBonus otherBonus = (AllAttributesBonus)other;
        AttributeModifier mergedModifier = new AttributeModifier(this.modifier.m_22209_(), "Merged", this.modifier.m_22218_() + otherBonus.modifier.m_22218_(), this.modifier.m_22217_());
        AllAttributesBonus mergedBonus = new AllAttributesBonus(mergedModifier);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        return mergedBonus;
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent tooltip = TooltipHelper.getSkillBonusTooltip(this.getDescriptionId(), this.modifier.m_22218_(), this.modifier.m_22217_());
        tooltip = this.playerMultiplier.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        tooltip = this.playerCondition.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.modifier.m_22218_() > 0.0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int index, Consumer<AllAttributesBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.addLabel(55, 0, "Operation", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.modifier.m_22218_()).setNumericResponder(value -> this.selectAmount(consumer, (Double)value));
        editor.addOperationSelection(55, 0, 145, this.modifier.m_22217_()).setResponder(operation -> this.selectOperation(consumer, (AttributeModifier.Operation)operation));
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

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<AllAttributesBonus> consumer, LivingMultiplier multiplier) {
        this.setMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<AllAttributesBonus> consumer, LivingCondition condition) {
        this.setCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectOperation(Consumer<AllAttributesBonus> consumer, AttributeModifier.Operation operation) {
        this.setOperation(operation);
        consumer.accept(this.copy());
    }

    private void selectAmount(Consumer<AllAttributesBonus> consumer, Double value) {
        this.setAmount(value);
        consumer.accept(this.copy());
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<AllAttributesBonus> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, multiplier -> {
            this.setMultiplier((LivingMultiplier)multiplier);
            consumer.accept(this.copy());
        });
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<AllAttributesBonus> consumer) {
        this.playerCondition.addEditorWidgets(editor, condition -> {
            this.setCondition((LivingCondition)condition);
            consumer.accept(this.copy());
        });
    }

    public void setAmount(double amount) {
        this.modifier = new AttributeModifier(this.modifier.m_22209_(), this.modifier.m_22214_(), amount, this.modifier.m_22217_());
    }

    public void setOperation(AttributeModifier.Operation operation) {
        this.modifier = new AttributeModifier(this.modifier.m_22209_(), this.modifier.m_22214_(), this.modifier.m_22218_(), operation);
    }

    public SkillBonus<?> setCondition(LivingCondition condition) {
        this.playerCondition = condition;
        return this;
    }

    public SkillBonus<?> setMultiplier(LivingMultiplier multiplier) {
        this.playerMultiplier = multiplier;
        return this;
    }

    private static Set<Attribute> getAffectedAttributes() {
        if (AFFECTED_ATTRIBUTES.isEmpty()) {
            ForgeRegistries.ATTRIBUTES.getValues().stream().filter(arg_0 -> ((AttributeSupplier)((AttributeSupplier)ForgeHooks.getAttributesView().get(EntityType.f_20532_))).m_22258_(arg_0)).forEach(AFFECTED_ATTRIBUTES::add);
        }
        return AFFECTED_ATTRIBUTES;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public AllAttributesBonus deserialize(JsonObject json) throws JsonParseException {
            AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(json);
            AllAttributesBonus bonus = new AllAttributesBonus(modifier);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof AllAttributesBonus)) {
                throw new IllegalArgumentException();
            }
            AllAttributesBonus aBonus = (AllAttributesBonus)bonus;
            SerializationHelper.serializeAttributeModifier(json, aBonus.modifier);
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
        }

        @Override
        public AllAttributesBonus deserialize(CompoundTag tag) {
            AttributeModifier modifier = SerializationHelper.deserializeAttributeModifier(tag);
            AllAttributesBonus bonus = new AllAttributesBonus(modifier);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof AllAttributesBonus)) {
                throw new IllegalArgumentException();
            }
            AllAttributesBonus aBonus = (AllAttributesBonus)bonus;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeAttributeModifier(tag, aBonus.modifier);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            return tag;
        }

        @Override
        public AllAttributesBonus deserialize(FriendlyByteBuf buf) {
            AttributeModifier modifier = NetworkHelper.readAttributeModifier(buf);
            AllAttributesBonus bonus = new AllAttributesBonus(modifier);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof AllAttributesBonus)) {
                throw new IllegalArgumentException();
            }
            AllAttributesBonus aBonus = (AllAttributesBonus)bonus;
            NetworkHelper.writeAttributeModifier(buf, aBonus.modifier);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new AllAttributesBonus(new AttributeModifier(UUID.randomUUID(), "Skill", 0.05, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }
}

