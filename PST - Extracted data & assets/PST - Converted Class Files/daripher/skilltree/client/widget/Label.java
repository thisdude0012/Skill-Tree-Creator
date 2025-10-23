/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class Label
extends AbstractWidget {
    public static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation("skilltree:textures/screen/widgets.png");
    private boolean hasBackground;

    public Label(int x, int y, Component message) {
        super(x, y, 0, 14, message);
    }

    public Label(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.setHasBackground(true);
    }

    public void m_87963_(@NotNull GuiGraphics graphics, int m, int pMouseY, float partialTick) {
        Minecraft minecraft = Minecraft.m_91087_();
        Font font = minecraft.f_91062_;
        if (this.hasBackground) {
            graphics.m_280218_(WIDGETS_TEXTURE, this.m_252754_(), this.m_252907_(), 0, 14, this.f_93618_ / 2, this.f_93619_);
            graphics.m_280218_(WIDGETS_TEXTURE, this.m_252754_() + this.f_93618_ / 2, this.m_252907_(), 256 - this.f_93618_ / 2, 14, this.f_93618_ / 2, this.f_93619_);
            int textColor = this.getFGColor() | Mth.m_14167_((float)(this.f_93625_ * 255.0f)) << 24;
            graphics.m_280653_(font, this.m_6035_(), this.m_252754_() + this.f_93618_ / 2, this.m_252907_() + (this.f_93619_ - 8) / 2, textColor);
        } else {
            graphics.m_280430_(font, this.m_6035_(), this.m_252754_(), this.m_252907_() + 3, this.getFGColor());
        }
    }

    protected void m_168797_(@NotNull NarrationElementOutput output) {
    }

    public void setHasBackground(boolean hasBackground) {
        this.hasBackground = hasBackground;
    }
}

