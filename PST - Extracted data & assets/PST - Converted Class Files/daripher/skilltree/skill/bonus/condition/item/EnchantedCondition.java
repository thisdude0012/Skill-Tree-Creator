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
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 */
package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemTagCondition;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class EnchantedCondition
implements ItemCondition {
    private ItemCondition itemCondition;

    public EnchantedCondition(ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    @Override
    public boolean met(ItemStack stack) {
        return !EnchantmentHelper.m_44831_((ItemStack)stack).isEmpty() && this.itemCondition.met(stack);
    }

    @Override
    public Component getTooltip() {
        return Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{this.itemCondition.getTooltip("type")});
    }

    @Override
    public Component getTooltip(String type) {
        return Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{this.itemCondition.getTooltip(type + ".type")});
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EnchantedCondition that = (EnchantedCondition)o;
        return this.itemCondition.equals(that.itemCondition);
    }

    public int hashCode() {
        return Objects.hash(this.itemCondition);
    }

    @Override
    public ItemCondition.Serializer getSerializer() {
        return (ItemCondition.Serializer)PSTItemConditions.ENCHANTED.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemCondition> consumer) {
        editor.addLabel(0, 0, "Inner Item Condition", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.itemCondition).setResponder(condition -> this.selectItemCondition(editor, consumer, (ItemCondition)condition)).setMenuInitFunc(() -> this.addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<ItemCondition> consumer) {
        this.itemCondition.addEditorWidgets(editor, condition -> {
            this.setItemCondition((ItemCondition)condition);
            consumer.accept(this);
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<ItemCondition> consumer, ItemCondition condition) {
        this.setItemCondition(condition);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    public void setItemCondition(ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public static class Serializer
    implements ItemCondition.Serializer {
        @Override
        public ItemCondition deserialize(JsonObject json) throws JsonParseException {
            return new EnchantedCondition(SerializationHelper.deserializeItemCondition(json));
        }

        @Override
        public void serialize(JsonObject json, ItemCondition condition) {
            if (!(condition instanceof EnchantedCondition)) {
                throw new IllegalArgumentException();
            }
            EnchantedCondition aCondition = (EnchantedCondition)condition;
            SerializationHelper.serializeItemCondition(json, aCondition.itemCondition);
        }

        @Override
        public ItemCondition deserialize(CompoundTag tag) {
            return new EnchantedCondition(SerializationHelper.deserializeItemCondition(tag));
        }

        @Override
        public CompoundTag serialize(ItemCondition condition) {
            if (!(condition instanceof EnchantedCondition)) {
                throw new IllegalArgumentException();
            }
            EnchantedCondition aCondition = (EnchantedCondition)condition;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemCondition(tag, aCondition.itemCondition);
            return tag;
        }

        @Override
        public ItemCondition deserialize(FriendlyByteBuf buf) {
            return new EnchantedCondition(NetworkHelper.readItemCondition(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
            if (!(condition instanceof EnchantedCondition)) {
                throw new IllegalArgumentException();
            }
            EnchantedCondition aCondition = (EnchantedCondition)condition;
            NetworkHelper.writeItemCondition(buf, aCondition.itemCondition);
        }

        @Override
        public ItemCondition createDefaultInstance() {
            return new EnchantedCondition(new ItemTagCondition(ItemTags.f_271388_.f_203868_()));
        }
    }
}

