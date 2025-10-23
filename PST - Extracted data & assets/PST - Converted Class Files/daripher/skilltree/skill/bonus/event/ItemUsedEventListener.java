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
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 */
package daripher.skilltree.skill.bonus.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTEventListeners;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.PotionCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemUsedEventListener
implements SkillEventListener {
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;
    private ItemCondition itemCondition;

    public ItemUsedEventListener(ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public void onEvent(@Nonnull Player player, @Nonnull ItemStack stack, @Nonnull EventListenerBonus<?> skill) {
        if (!this.playerCondition.isConditionMet((LivingEntity)player)) {
            return;
        }
        if (!this.itemCondition.met(stack)) {
            return;
        }
        ((EventListenerBonus)skill.multiply(this.playerMultiplier.getValue((LivingEntity)player))).applyEffect((LivingEntity)player);
    }

    @Override
    public MutableComponent getTooltip(Component bonusTooltip) {
        Component itemTooltip = this.itemCondition.getTooltip();
        MutableComponent eventTooltip = Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{bonusTooltip, itemTooltip});
        eventTooltip = this.playerCondition.getTooltip(eventTooltip, SkillBonus.Target.PLAYER);
        eventTooltip = this.playerMultiplier.getTooltip(eventTooltip, SkillBonus.Target.PLAYER);
        return eventTooltip;
    }

    @Override
    public SkillEventListener.Serializer getSerializer() {
        return (SkillEventListener.Serializer)PSTEventListeners.ITEM_USED.get();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ItemUsedEventListener listener = (ItemUsedEventListener)o;
        return Objects.equals(this.playerCondition, listener.playerCondition) && Objects.equals(this.playerMultiplier, listener.playerMultiplier) && Objects.equals(this.itemCondition, listener.itemCondition);
    }

    public int hashCode() {
        return Objects.hash(this.playerCondition, this.playerMultiplier, this.itemCondition);
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerCondition).setResponder(condition -> this.selectPlayerCondition(editor, consumer, (LivingCondition)condition)).setMenuInitFunc(() -> this.addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerMultiplier).setResponder(multiplier -> this.selectPlayerMultiplier(editor, consumer, (LivingMultiplier)multiplier)).setMenuInitFunc(() -> this.addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.itemCondition).setResponder(condition -> this.selectItemCondition(editor, consumer, (ItemCondition)condition)).setMenuInitFunc(() -> this.addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
        this.itemCondition.addEditorWidgets(editor, (ItemCondition condition) -> {
            this.setItemCondition((ItemCondition)condition);
            consumer.accept(this);
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<SkillEventListener> consumer, ItemCondition condition) {
        this.setItemCondition(condition);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, (LivingMultiplier multiplier) -> {
            this.setPlayerMultiplier((LivingMultiplier)multiplier);
            consumer.accept(this);
        });
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<SkillEventListener> consumer, LivingMultiplier multiplier) {
        this.setPlayerMultiplier(multiplier);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
        this.playerCondition.addEditorWidgets(editor, (LivingCondition condition) -> {
            this.setPlayerCondition((LivingCondition)condition);
            consumer.accept(this);
        });
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<SkillEventListener> consumer, LivingCondition condition) {
        this.setPlayerCondition(condition);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    @Override
    public SkillBonus.Target getTarget() {
        return SkillBonus.Target.PLAYER;
    }

    public void setPlayerCondition(LivingCondition playerCondition) {
        this.playerCondition = playerCondition;
    }

    public void setPlayerMultiplier(LivingMultiplier playerMultiplier) {
        this.playerMultiplier = playerMultiplier;
    }

    public void setItemCondition(ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public static class Serializer
    implements SkillEventListener.Serializer {
        @Override
        public SkillEventListener deserialize(JsonObject json) throws JsonParseException {
            ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(json);
            ItemUsedEventListener listener = new ItemUsedEventListener(itemCondition);
            listener.setPlayerCondition(SerializationHelper.deserializeLivingCondition(json, "player_condition"));
            listener.setPlayerMultiplier(SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier"));
            return listener;
        }

        @Override
        public void serialize(JsonObject json, SkillEventListener listener) {
            if (!(listener instanceof ItemUsedEventListener)) {
                throw new IllegalArgumentException();
            }
            ItemUsedEventListener aListener = (ItemUsedEventListener)listener;
            SerializationHelper.serializeItemCondition(json, aListener.itemCondition);
            SerializationHelper.serializeLivingCondition(json, aListener.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(json, aListener.playerMultiplier, "player_multiplier");
        }

        @Override
        public SkillEventListener deserialize(CompoundTag tag) {
            ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(tag);
            ItemUsedEventListener listener = new ItemUsedEventListener(itemCondition);
            listener.setPlayerCondition(SerializationHelper.deserializeLivingCondition(tag, "player_condition"));
            listener.setPlayerMultiplier(SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier"));
            return listener;
        }

        @Override
        public CompoundTag serialize(SkillEventListener listener) {
            if (!(listener instanceof ItemUsedEventListener)) {
                throw new IllegalArgumentException();
            }
            ItemUsedEventListener aListener = (ItemUsedEventListener)listener;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemCondition(tag, aListener.itemCondition);
            SerializationHelper.serializeLivingCondition(tag, aListener.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(tag, aListener.playerMultiplier, "player_multiplier");
            return tag;
        }

        @Override
        public SkillEventListener deserialize(FriendlyByteBuf buf) {
            ItemCondition itemCondition = NetworkHelper.readItemCondition(buf);
            ItemUsedEventListener listener = new ItemUsedEventListener(itemCondition);
            listener.setPlayerCondition(NetworkHelper.readLivingCondition(buf));
            listener.setPlayerMultiplier(NetworkHelper.readLivingMultiplier(buf));
            return listener;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillEventListener listener) {
            if (!(listener instanceof ItemUsedEventListener)) {
                throw new IllegalArgumentException();
            }
            ItemUsedEventListener aListener = (ItemUsedEventListener)listener;
            NetworkHelper.writeItemCondition(buf, aListener.itemCondition);
            NetworkHelper.writeLivingCondition(buf, aListener.playerCondition);
            NetworkHelper.writeLivingMultiplier(buf, aListener.playerMultiplier);
        }

        @Override
        public SkillEventListener createDefaultInstance() {
            return new ItemUsedEventListener(new PotionCondition(PotionCondition.Type.ANY));
        }
    }
}

