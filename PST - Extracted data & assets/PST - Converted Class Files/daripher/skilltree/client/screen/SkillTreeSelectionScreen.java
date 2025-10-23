/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.screen;

import daripher.skilltree.client.widget.SkillTreeSelectionButton;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SkillTreeSelectionScreen
extends Screen {
    public static final int BUTTONS_SIZE = 19;
    public static final int BUTTONS_SPACING = 5;

    public SkillTreeSelectionScreen() {
        super((Component)Component.m_237119_());
    }

    protected void m_7856_() {
        this.m_169413_();
        int buttonCount = SkillTreesReloader.getSkillTrees().size();
        int buttonRowWidth = buttonCount * 19 - (buttonCount - 1) * 5;
        int buttonX = this.f_96543_ / 2 - buttonRowWidth / 2;
        int buttonY = this.f_96544_ / 2 - 9;
        for (ResourceLocation skillTreeId : SkillTreesReloader.getSkillTrees().keySet()) {
            SkillTreeSelectionButton button = new SkillTreeSelectionButton(buttonX, buttonY, 19, 19, skillTreeId);
            buttonX += 24;
            this.m_142416_((GuiEventListener)button);
        }
    }

    public void m_88315_(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(guiGraphics);
        super.m_88315_(guiGraphics, mouseX, mouseY, partialTick);
        for (Renderable widget : this.f_169369_) {
            SkillTreeSelectionButton button;
            if (!(widget instanceof SkillTreeSelectionButton) || !(button = (SkillTreeSelectionButton)widget).m_5953_(mouseX, mouseY)) continue;
            guiGraphics.m_280557_(this.f_96547_, button.m_6035_(), mouseX, mouseY);
        }
    }

    public void m_280273_(GuiGraphics guiGraphics) {
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_tree_background.png");
        int size = 2048;
        guiGraphics.m_280398_(texture, (this.f_96543_ - size) / 2, (this.f_96544_ - size) / 2, 0, 0.0f, 0.0f, size, size, size, size);
    }
}

