/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.SharedSuggestionProvider
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterClientCommandsEvent
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.screen.SkillTreeEditorScreen;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid="skilltree", value={Dist.CLIENT})
public class PSTClientCommands {
    public static final SuggestionProvider<CommandSourceStack> SKILL_TREE_ID_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.m_82981_(PSTClientCommands.gatherSkillTreesPaths(), (SuggestionsBuilder)builder);
    private static ResourceLocation tree_to_display;
    private static int timer;

    @NotNull
    private static Stream<String> gatherSkillTreesPaths() {
        return Stream.concat(SkillTreesReloader.getSkillTrees().keySet().stream(), SkillTreeClientData.getEditorTreesIDs().stream()).map(ResourceLocation::toString);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder editorCommand = (LiteralArgumentBuilder)Commands.m_82127_((String)"skilltree").then(Commands.m_82127_((String)"editor").then(Commands.m_82129_((String)"treeId", (ArgumentType)StringArgumentType.greedyString()).suggests(SKILL_TREE_ID_PROVIDER).executes(PSTClientCommands::displaySkillTreeEditor)));
        event.getDispatcher().register(editorCommand);
    }

    @SubscribeEvent
    public static void delayedCommandExecution(TickEvent.ClientTickEvent event) {
        if (timer > 0) {
            --timer;
            return;
        }
        if (tree_to_display != null) {
            Minecraft.m_91087_().m_91152_((Screen)new SkillTreeEditorScreen(tree_to_display));
            tree_to_display = null;
        }
    }

    private static int displaySkillTreeEditor(CommandContext<CommandSourceStack> ctx) {
        String treeIdArg = ((String)ctx.getArgument("treeId", String.class)).toLowerCase();
        tree_to_display = new ResourceLocation(treeIdArg);
        timer = 1;
        return 1;
    }
}

