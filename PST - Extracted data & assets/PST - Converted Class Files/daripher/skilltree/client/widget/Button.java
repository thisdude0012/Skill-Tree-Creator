/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class Button
extends net.minecraft.client.gui.components.Button {
    protected Button.OnPress pressFunc = b -> {};

    public Button(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message, b -> {}, Supplier::get);
    }

    public void setPressFunc(Button.OnPress pressFunc) {
        this.pressFunc = pressFunc;
    }

    public void m_5691_() {
        this.pressFunc.m_93750_((net.minecraft.client.gui.components.Button)this);
    }

    public void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        this.renderText(graphics);
    }

    protected void renderBackground(@NotNull GuiGraphics graphics) {
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets.png");
        int v = this.getTextureVariant() * 14;
        graphics.m_280218_(texture, this.m_252754_(), this.m_252907_(), 0, v, this.f_93618_ / 2, this.f_93619_);
        graphics.m_280218_(texture, this.m_252754_() + this.f_93618_ / 2, this.m_252907_(), -this.f_93618_ / 2, v, this.f_93618_ / 2, this.f_93619_);
    }

    protected void renderText(@NotNull GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.m_91087_();
        Font font = minecraft.f_91062_;
        int textColor = this.getFGColor();
        graphics.m_280653_(font, this.m_6035_(), this.m_252754_() + this.f_93618_ / 2, this.m_252907_() + (this.f_93619_ - 8) / 2, textColor |= Mth.m_14167_((float)(this.f_93625_ * 255.0f)) << 24);
    }

    protected int getTextureVariant() {
        return !this.m_142518_() ? 0 : (this.m_198029_() ? 2 : 1);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean m_5953_(double mouseX, double mouseY) {
        return this.f_93624_ && mouseX >= (double)this.m_252754_() && mouseY >= (double)this.m_252907_() && mouseX < (double)(this.m_252754_() + this.f_93618_) && mouseY < (double)(this.m_252907_() + this.f_93619_);
    }
}

