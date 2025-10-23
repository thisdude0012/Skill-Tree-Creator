/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.client.widget.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SkillTreeSelectionButton
extends Button {
    private final ResourceLocation skillTreeId;

    public SkillTreeSelectionButton(int x, int y, int width, int height, ResourceLocation skillTreeId) {
        super(x, y, width, height, (Component)Component.m_237115_((String)skillTreeId.toString()));
        this.setPressFunc(b -> SkillTreeSelectionButton.onPress(skillTreeId));
        this.skillTreeId = skillTreeId;
    }

    private static void onPress(ResourceLocation skillTreeId) {
        SkillTreeSelectionButton.getMinecraft().m_91152_((Screen)new SkillTreeScreen(skillTreeId));
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics graphics) {
        String texturesFolder = "textures/icons/skill_tree/";
        ResourceLocation texture = this.skillTreeId.m_246208_(texturesFolder).m_266382_(".png");
        int v = this.getTextureVariant() * 19;
        graphics.m_280163_(texture, this.m_252754_(), this.m_252907_(), 0.0f, (float)v, this.f_93618_, this.f_93619_, 19, 57);
    }

    @Override
    protected void renderText(@NotNull GuiGraphics graphics) {
    }

    private static Minecraft getMinecraft() {
        return Minecraft.m_91087_();
    }
}

