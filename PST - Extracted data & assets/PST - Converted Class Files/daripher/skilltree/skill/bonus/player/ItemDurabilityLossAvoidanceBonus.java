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
 *  net.minecraft.world.item.ItemStack
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
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.NoneItemCondition;
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
import net.minecraft.world.item.ItemStack;

public final class ItemDurabilityLossAvoidanceBonus
implements SkillBonus<ItemDurabilityLossAvoidanceBonus> {
    private float chance;
    @Nonnull
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    @Nonnull
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
    @Nonnull
    private ItemCondition itemCondition = NoneItemCondition.INSTANCE;

    public ItemDurabilityLossAvoidanceBonus(float chance) {
        this.chance = chance;
    }

    public float getChance(Player player, ItemStack itemStack) {
        if (!this.playerCondition.isConditionMet((LivingEntity)player)) {
            return 0.0f;
        }
        if (!this.itemCondition.met(itemStack)) {
            return 0.0f;
        }
        return this.chance * this.playerMultiplier.getValue((LivingEntity)player);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.ITEM_DURABILITY_LOSS_AVOIDANCE.get();
    }

    public ItemDurabilityLossAvoidanceBonus copy() {
        ItemDurabilityLossAvoidanceBonus bonus = new ItemDurabilityLossAvoidanceBonus(this.chance);
        bonus.playerMultiplier = this.playerMultiplier;
        bonus.playerCondition = this.playerCondition;
        bonus.itemCondition = this.itemCondition;
        return bonus;
    }

    @Override
    public ItemDurabilityLossAvoidanceBonus multiply(double multiplier) {
        this.chance *= (float)multiplier;
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ItemDurabilityLossAvoidanceBonus)) {
            return false;
        }
        ItemDurabilityLossAvoidanceBonus otherBonus = (ItemDurabilityLossAvoidanceBonus)other;
        if (!Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier)) {
            return false;
        }
        if (!Objects.equals(otherBonus.itemCondition, this.itemCondition)) {
            return false;
        }
        return Objects.equals(otherBonus.playerCondition, this.playerCondition);
    }

    @Override
    public SkillBonus<ItemDurabilityLossAvoidanceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ItemDurabilityLossAvoidanceBonus)) {
            throw new IllegalArgumentException();
        }
        ItemDurabilityLossAvoidanceBonus otherBonus = (ItemDurabilityLossAvoidanceBonus)other;
        float mergedChance = otherBonus.chance + this.chance;
        ItemDurabilityLossAvoidanceBonus mergedBonus = new ItemDurabilityLossAvoidanceBonus(mergedChance);
        mergedBonus.playerMultiplier = this.playerMultiplier;
        mergedBonus.playerCondition = this.playerCondition;
        mergedBonus.itemCondition = this.itemCondition;
        return mergedBonus;
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent tooltip;
        if (this.chance < 1.0f) {
            tooltip = Component.m_237110_((String)(this.getDescriptionId() + ".chance"), (Object[])new Object[]{this.itemCondition.getTooltip()});
            tooltip = TooltipHelper.getSkillBonusTooltip((Component)tooltip, (double)this.chance, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            tooltip = Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{this.itemCondition.getTooltip()});
        }
        tooltip = this.playerMultiplier.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        tooltip = this.playerCondition.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.chance > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ItemDurabilityLossAvoidanceBonus> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.chance).setNumericResponder(value -> this.selectChance(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.itemCondition).setResponder(condition -> this.selectItemCondition(editor, consumer, (ItemCondition)condition)).setMenuInitFunc(() -> this.addItemConditionWidgets(editor, consumer));
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

    private void selectChance(Consumer<ItemDurabilityLossAvoidanceBonus> consumer, Double value) {
        this.setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<ItemDurabilityLossAvoidanceBonus> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, multiplier -> {
            this.setPlayerMultiplier((LivingMultiplier)multiplier);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<ItemDurabilityLossAvoidanceBonus> consumer, LivingMultiplier multiplier) {
        this.setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<ItemDurabilityLossAvoidanceBonus> consumer) {
        this.playerCondition.addEditorWidgets(editor, c -> {
            this.setPlayerCondition((LivingCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<ItemDurabilityLossAvoidanceBonus> consumer, LivingCondition condition) {
        this.setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<ItemDurabilityLossAvoidanceBonus> consumer) {
        this.itemCondition.addEditorWidgets(editor, c -> {
            this.setItemCondition((ItemCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<ItemDurabilityLossAvoidanceBonus> consumer, ItemCondition condition) {
        this.setItemCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    public SkillBonus<?> setPlayerCondition(LivingCondition condition) {
        this.playerCondition = condition;
        return this;
    }

    public SkillBonus<?> setItemCondition(ItemCondition condition) {
        this.itemCondition = condition;
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
        public ItemDurabilityLossAvoidanceBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            ItemDurabilityLossAvoidanceBonus bonus = new ItemDurabilityLossAvoidanceBonus(chance);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            bonus.itemCondition = SerializationHelper.deserializeItemCondition(json);
            return bonus;
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemDurabilityLossAvoidanceBonus)) {
                throw new IllegalArgumentException();
            }
            ItemDurabilityLossAvoidanceBonus aBonus = (ItemDurabilityLossAvoidanceBonus)bonus;
            json.addProperty("chance", (Number)Float.valueOf(aBonus.chance));
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
        }

        @Override
        public ItemDurabilityLossAvoidanceBonus deserialize(CompoundTag tag) {
            float chance = tag.m_128457_("chance");
            ItemDurabilityLossAvoidanceBonus bonus = new ItemDurabilityLossAvoidanceBonus(chance);
            bonus.playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            bonus.playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            bonus.itemCondition = SerializationHelper.deserializeItemCondition(tag);
            return bonus;
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemDurabilityLossAvoidanceBonus)) {
                throw new IllegalArgumentException();
            }
            ItemDurabilityLossAvoidanceBonus aBonus = (ItemDurabilityLossAvoidanceBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("chance", aBonus.chance);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
            return tag;
        }

        @Override
        public ItemDurabilityLossAvoidanceBonus deserialize(FriendlyByteBuf buf) {
            float chance = buf.readFloat();
            ItemDurabilityLossAvoidanceBonus bonus = new ItemDurabilityLossAvoidanceBonus(chance);
            bonus.playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            bonus.playerCondition = NetworkHelper.readLivingCondition(buf);
            bonus.itemCondition = NetworkHelper.readItemCondition(buf);
            return bonus;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ItemDurabilityLossAvoidanceBonus)) {
                throw new IllegalArgumentException();
            }
            ItemDurabilityLossAvoidanceBonus aBonus = (ItemDurabilityLossAvoidanceBonus)bonus;
            buf.writeFloat(aBonus.chance);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ItemDurabilityLossAvoidanceBonus(0.1f);
        }
    }
}

