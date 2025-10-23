/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package daripher.skilltree.client.init;

import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.client.screen.SkillTreeSelectionScreen;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="skilltree", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class PSTKeybinds {
    private static final KeyMapping SKILL_TREE_KEY = new KeyMapping("key.display_skill_tree", 79, "key.categories.skilltree");

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(SKILL_TREE_KEY);
    }

    @Mod.EventBusSubscriber(modid="skilltree", value={Dist.CLIENT})
    private static class KeyEvents {
        private KeyEvents() {
        }

        @SubscribeEvent
        public static void keyPressed(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.m_91087_();
            if (event.getAction() != 1) {
                return;
            }
            if (minecraft.f_91080_ != null) {
                return;
            }
            if (minecraft.f_91074_ == null) {
                return;
            }
            if (event.getKey() == SKILL_TREE_KEY.getKey().m_84873_()) {
                ResourceLocation defaultTreeId = SkillTreesReloader.getDefaultSkillTreeId();
                if (defaultTreeId == null) {
                    SkillTreeClientData.printMessage("No skill trees found.", ChatFormatting.DARK_RED);
                    return;
                }
                if (SkillTreesReloader.getSkillTrees().size() == 1) {
                    PassiveSkillTree skillTree = SkillTreesReloader.getSkillTreeById(defaultTreeId);
                    for (ResourceLocation skillId : skillTree.getSkillIds()) {
                        if (SkillsReloader.getSkillById(skillId) != null) continue;
                        SkillTreeClientData.printMessage("This skill tree is broken.", ChatFormatting.DARK_RED);
                        SkillTreeClientData.printMessage("Open it in the editor to resolve issues.", ChatFormatting.RED);
                        return;
                    }
                    SkillTreeScreen screen = new SkillTreeScreen(defaultTreeId);
                    minecraft.m_91152_((Screen)screen);
                } else {
                    minecraft.m_91152_((Screen)new SkillTreeSelectionScreen());
                }
            }
        }
    }
}

