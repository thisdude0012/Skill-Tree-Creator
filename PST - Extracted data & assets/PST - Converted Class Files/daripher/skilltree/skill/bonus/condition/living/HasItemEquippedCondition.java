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
 */
package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.NoneItemCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public final class HasItemEquippedCondition
implements LivingCondition {
    @Nonnull
    private ItemCondition itemCondition;

    public HasItemEquippedCondition(@Nonnull ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    @Override
    public boolean isConditionMet(LivingEntity living) {
        return PlayerHelper.getAllEquipment(living).anyMatch(this.itemCondition::met);
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        String key = this.getDescriptionId();
        MutableComponent targetDescription = Component.m_237115_((String)"%s.target.%s".formatted(new Object[]{key, target.getName()}));
        Component itemDescription = this.itemCondition.getTooltip();
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, targetDescription, itemDescription});
    }

    @Override
    public LivingCondition.Serializer getSerializer() {
        return (LivingCondition.Serializer)PSTLivingConditions.HAS_ITEM_EQUIPPED.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.itemCondition).setResponder(condition -> this.selectItemCondition(editor, consumer, (ItemCondition)condition)).setMenuInitFunc(() -> this.addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
        this.itemCondition.addEditorWidgets(editor, (ItemCondition condition) -> {
            this.setItemCondition((ItemCondition)condition);
            consumer.accept(this);
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<LivingCondition> consumer, ItemCondition condition) {
        this.setItemCondition(condition);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HasItemEquippedCondition that = (HasItemEquippedCondition)o;
        return Objects.equals(this.itemCondition, that.itemCondition);
    }

    public int hashCode() {
        return Objects.hash(this.itemCondition);
    }

    public void setItemCondition(@Nonnull ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public static class Serializer
    implements LivingCondition.Serializer {
        @Override
        public LivingCondition deserialize(JsonObject json) throws JsonParseException {
            return new HasItemEquippedCondition(SerializationHelper.deserializeItemCondition(json));
        }

        @Override
        public void serialize(JsonObject json, LivingCondition condition) {
            if (!(condition instanceof HasItemEquippedCondition)) {
                throw new IllegalArgumentException();
            }
            HasItemEquippedCondition aCondition = (HasItemEquippedCondition)condition;
            SerializationHelper.serializeItemCondition(json, aCondition.itemCondition);
        }

        @Override
        public LivingCondition deserialize(CompoundTag tag) {
            return new HasItemEquippedCondition(SerializationHelper.deserializeItemCondition(tag));
        }

        @Override
        public CompoundTag serialize(LivingCondition condition) {
            if (!(condition instanceof HasItemEquippedCondition)) {
                throw new IllegalArgumentException();
            }
            HasItemEquippedCondition aCondition = (HasItemEquippedCondition)condition;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemCondition(tag, aCondition.itemCondition);
            return tag;
        }

        @Override
        public LivingCondition deserialize(FriendlyByteBuf buf) {
            return new HasItemEquippedCondition(NetworkHelper.readItemCondition(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
            if (!(condition instanceof HasItemEquippedCondition)) {
                throw new IllegalArgumentException();
            }
            HasItemEquippedCondition aCondition = (HasItemEquippedCondition)condition;
            NetworkHelper.writeItemCondition(buf, aCondition.itemCondition);
        }

        @Override
        public LivingCondition createDefaultInstance() {
            return new HasItemEquippedCondition(NoneItemCondition.INSTANCE);
        }
    }
}

