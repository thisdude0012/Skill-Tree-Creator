/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.network.PacketDistributor
 */
package daripher.skilltree.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid="skilltree")
public class PSTCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder resetCommand = (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"skilltree").then(Commands.m_82127_((String)"reset").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91466_()).executes(PSTCommands::executeResetCommand)))).requires(PSTCommands::hasPermission);
        event.getDispatcher().register(resetCommand);
        LiteralArgumentBuilder addPointsCommand = (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"skilltree").then(Commands.m_82127_((String)"points").then(Commands.m_82127_((String)"add").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91466_()).then(Commands.m_82129_((String)"chance", (ArgumentType)IntegerArgumentType.integer()).executes(PSTCommands::executeAddPointsCommand)))))).requires(PSTCommands::hasPermission);
        event.getDispatcher().register(addPointsCommand);
        LiteralArgumentBuilder setPointsCommand = (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"skilltree").then(Commands.m_82127_((String)"points").then(Commands.m_82127_((String)"set").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91466_()).then(Commands.m_82129_((String)"chance", (ArgumentType)IntegerArgumentType.integer()).executes(PSTCommands::executeSetPointsCommand)))))).requires(PSTCommands::hasPermission);
        event.getDispatcher().register(setPointsCommand);
    }

    private static int executeResetCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.m_91474_(ctx, (String)"player");
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get((Player)player);
        skillsCapability.resetTree(player);
        player.m_213846_((Component)Component.m_237115_((String)"skilltree.message.reset_command").m_130940_(ChatFormatting.YELLOW));
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncPlayerSkillsMessage((Player)player));
        return 1;
    }

    private static int executeAddPointsCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.m_91474_(ctx, (String)"player");
        int amount = IntegerArgumentType.getInteger(ctx, (String)"chance");
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get((Player)player);
        skillsCapability.setSkillPoints(amount + skillsCapability.getSkillPoints());
        player.m_213846_((Component)Component.m_237115_((String)"skilltree.message.point_command").m_130940_(ChatFormatting.YELLOW));
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncPlayerSkillsMessage((Player)player));
        return 1;
    }

    private static int executeSetPointsCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.m_91474_(ctx, (String)"player");
        int amount = IntegerArgumentType.getInteger(ctx, (String)"chance");
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get((Player)player);
        skillsCapability.setSkillPoints(amount);
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncPlayerSkillsMessage((Player)player));
        return 1;
    }

    private static boolean hasPermission(CommandSourceStack commandSourceStack) {
        return commandSourceStack.m_6761_(2);
    }
}

