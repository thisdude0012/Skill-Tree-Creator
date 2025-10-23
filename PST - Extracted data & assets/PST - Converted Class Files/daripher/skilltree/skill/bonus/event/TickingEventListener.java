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

public class TickingEventListener
implements SkillEventListener {
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;

    public void onEvent(@Nonnull Player player, @Nonnull EventListenerBonus<?> skill) {
        if (!this.playerCondition.isConditionMet((LivingEntity)player)) {
            return;
        }
        ((EventListenerBonus)skill.multiply(this.playerMultiplier.getValue((LivingEntity)player))).applyEffect((LivingEntity)player);
    }

    @Override
    public MutableComponent getTooltip(Component bonusTooltip) {
        MutableComponent eventTooltip = Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{bonusTooltip});
        eventTooltip = this.playerCondition.getTooltip(eventTooltip, SkillBonus.Target.PLAYER);
        eventTooltip = this.playerMultiplier.getTooltip(eventTooltip, SkillBonus.Target.PLAYER);
        return eventTooltip;
    }

    @Override
    public SkillEventListener.Serializer getSerializer() {
        return (SkillEventListener.Serializer)PSTEventListeners.TICKING.get();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TickingEventListener listener = (TickingEventListener)o;
        return Objects.equals(this.playerCondition, listener.playerCondition) && Objects.equals(this.playerMultiplier, listener.playerMultiplier);
    }

    public int hashCode() {
        return Objects.hash(this.playerCondition, this.playerMultiplier);
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

    public static class Serializer
    implements SkillEventListener.Serializer {
        @Override
        public SkillEventListener deserialize(JsonObject json) throws JsonParseException {
            TickingEventListener listener = new TickingEventListener();
            listener.setPlayerCondition(SerializationHelper.deserializeLivingCondition(json, "player_condition"));
            listener.setPlayerMultiplier(SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier"));
            return listener;
        }

        @Override
        public void serialize(JsonObject json, SkillEventListener listener) {
            if (!(listener instanceof TickingEventListener)) {
                throw new IllegalArgumentException();
            }
            TickingEventListener aListener = (TickingEventListener)listener;
            SerializationHelper.serializeLivingCondition(json, aListener.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(json, aListener.playerMultiplier, "player_multiplier");
        }

        @Override
        public SkillEventListener deserialize(CompoundTag tag) {
            TickingEventListener listener = new TickingEventListener();
            listener.setPlayerCondition(SerializationHelper.deserializeLivingCondition(tag, "player_condition"));
            listener.setPlayerMultiplier(SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier"));
            return listener;
        }

        @Override
        public CompoundTag serialize(SkillEventListener listener) {
            if (!(listener instanceof TickingEventListener)) {
                throw new IllegalArgumentException();
            }
            TickingEventListener aListener = (TickingEventListener)listener;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeLivingCondition(tag, aListener.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(tag, aListener.playerMultiplier, "player_multiplier");
            return tag;
        }

        @Override
        public SkillEventListener deserialize(FriendlyByteBuf buf) {
            TickingEventListener listener = new TickingEventListener();
            listener.setPlayerCondition(NetworkHelper.readLivingCondition(buf));
            listener.setPlayerMultiplier(NetworkHelper.readLivingMultiplier(buf));
            return listener;
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillEventListener listener) {
            if (!(listener instanceof TickingEventListener)) {
                throw new IllegalArgumentException();
            }
            TickingEventListener aListener = (TickingEventListener)listener;
            NetworkHelper.writeLivingCondition(buf, aListener.playerCondition);
            NetworkHelper.writeLivingMultiplier(buf, aListener.playerMultiplier);
        }

        @Override
        public SkillEventListener createDefaultInstance() {
            return new TickingEventListener();
        }
    }
}

