/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nonnull
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSource
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
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
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.event.SkillLearnedEventListener;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class CommandBonus
implements EventListenerBonus<CommandBonus> {
    @Nonnull
    private String command;
    @Nonnull
    private String description;
    @Nonnull
    private SkillEventListener eventListener;

    public CommandBonus(@Nonnull String command, @Nonnull String description, @Nonnull SkillEventListener eventListener) {
        this.command = command;
        this.description = description;
        this.eventListener = eventListener;
    }

    @Override
    public void applyEffect(LivingEntity target) {
        if (!(target instanceof Player)) {
            return;
        }
        Player player = (Player)target;
        if (this.command.isEmpty()) {
            return;
        }
        MinecraftServer server = player.m_20194_();
        if (server == null) {
            return;
        }
        CommandSourceStack commandSourceStack = CommandBonus.createCommandSourceStack(player, (ServerLevel)player.m_9236_());
        Commands commands = server.m_129892_();
        commands.m_230957_(commandSourceStack, this.command);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.COMMAND.get();
    }

    public CommandBonus copy() {
        return new CommandBonus(this.command, this.description, this.eventListener);
    }

    @Override
    public CommandBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return false;
    }

    @Override
    public boolean sameBonus(SkillBonus<?> other) {
        if (!(other instanceof CommandBonus)) {
            return false;
        }
        CommandBonus otherBonus = (CommandBonus)other;
        if (!otherBonus.command.equals(this.command)) {
            return false;
        }
        return Objects.equals(otherBonus.eventListener, this.eventListener);
    }

    @Override
    public SkillBonus<EventListenerBonus<CommandBonus>> merge(SkillBonus<?> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableComponent getTooltip() {
        Style style = TooltipHelper.getSkillBonusStyle(this.isPositive());
        return Component.m_237115_((String)this.description).m_130948_(style);
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    @NotNull
    public SkillEventListener getEventListener() {
        return this.eventListener;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int index, Consumer<EventListenerBonus<CommandBonus>> consumer) {
        editor.addLabel(0, 0, "Command", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addTextArea(0, 0, 200, 70, this.command).setResponder(v -> this.selectCommand(consumer, (String)v));
        editor.increaseHeight(75);
        editor.addLabel(0, 0, "Description", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addTextArea(0, 0, 200, 70, this.description).setResponder(text -> this.selectDescription(consumer, (String)text));
        editor.increaseHeight(75);
        editor.addLabel(0, 0, "Event", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.eventListener).setResponder(eventListener -> this.selectEventListener(editor, consumer, (SkillEventListener)eventListener)).setMenuInitFunc(() -> this.addEventListenerWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectDescription(Consumer<EventListenerBonus<CommandBonus>> consumer, String text) {
        this.setDescription(text);
        consumer.accept(this.copy());
    }

    private void selectCommand(Consumer<EventListenerBonus<CommandBonus>> consumer, String text) {
        this.setCommand(text);
        consumer.accept(this.copy());
    }

    private void selectEventListener(SkillTreeEditor editor, Consumer<EventListenerBonus<CommandBonus>> consumer, SkillEventListener eventListener) {
        this.setEventListener(eventListener);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addEventListenerWidgets(SkillTreeEditor editor, Consumer<EventListenerBonus<CommandBonus>> consumer) {
        this.eventListener.addEditorWidgets(editor, e -> {
            this.setEventListener((SkillEventListener)e);
            consumer.accept(this.copy());
        });
    }

    public void setCommand(@Nonnull String command) {
        this.command = command;
    }

    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    public void setEventListener(@Nonnull SkillEventListener eventListener) {
        this.eventListener = eventListener;
    }

    private static CommandSourceStack createCommandSourceStack(Player player, ServerLevel level) {
        return new CommandSourceStack((CommandSource)player, player.m_20182_(), player.m_20155_(), level, 4, player.m_7755_().getString(), player.m_5446_(), level.m_7654_(), (Entity)player);
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public CommandBonus deserialize(JsonObject json) throws JsonParseException {
            String command = json.get("command").getAsString();
            String description = json.has("description") ? json.get("description").getAsString() : "";
            SkillEventListener eventListener = SerializationHelper.deserializeEventListener(json);
            return new CommandBonus(command, description, eventListener);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof CommandBonus)) {
                throw new IllegalArgumentException();
            }
            CommandBonus aBonus = (CommandBonus)bonus;
            json.addProperty("command", aBonus.command);
            json.addProperty("description", aBonus.description);
            SerializationHelper.serializeEventListener(json, aBonus.eventListener);
        }

        @Override
        public CommandBonus deserialize(CompoundTag tag) {
            String command = tag.m_128461_("command");
            String description = tag.m_128441_("description") ? tag.m_128461_("description") : "";
            SkillEventListener eventListener = !tag.m_128441_("event_listener") ? new SkillLearnedEventListener() : SerializationHelper.deserializeEventListener(tag);
            return new CommandBonus(command, description, eventListener);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof CommandBonus)) {
                throw new IllegalArgumentException();
            }
            CommandBonus aBonus = (CommandBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128359_("command", aBonus.command);
            tag.m_128359_("description", aBonus.description);
            SerializationHelper.serializeEventListener(tag, aBonus.eventListener);
            return tag;
        }

        @Override
        public CommandBonus deserialize(FriendlyByteBuf buf) {
            String command = buf.m_130277_();
            String description = buf.m_130277_();
            SkillEventListener eventListener = NetworkHelper.readEventListener(buf);
            return new CommandBonus(command, description, eventListener);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof CommandBonus)) {
                throw new IllegalArgumentException();
            }
            CommandBonus aBonus = (CommandBonus)bonus;
            buf.m_130070_(aBonus.command);
            buf.m_130070_(aBonus.description);
            NetworkHelper.writeEventListener(buf, aBonus.eventListener);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new CommandBonus("give @p minecraft:apple", "Grants an apple when learned", new SkillLearnedEventListener());
        }
    }
}

