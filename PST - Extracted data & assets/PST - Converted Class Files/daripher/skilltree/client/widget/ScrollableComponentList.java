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
 *  net.minecraft.network.chat.FormattedText
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;

public class ScrollableComponentList
extends AbstractWidget {
    private final int maxHeight;
    private List<Component> components = new ArrayList<Component>();
    private int maxLines;
    private int scroll;

    public ScrollableComponentList(int y, int maxHeight) {
        super(0, y, 0, 0, (Component)Component.m_237119_());
        this.maxHeight = maxHeight;
    }

    public void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.components.isEmpty()) {
            return;
        }
        this.renderBackground(graphics);
        this.renderText(graphics);
        this.renderScrollBar(graphics);
    }

    private void renderBackground(@NotNull GuiGraphics graphics) {
        graphics.m_280509_(this.m_252754_(), this.m_252907_(), this.m_252754_() + this.f_93618_, this.m_252907_() + this.f_93619_, -587202560);
    }

    private void renderText(@NotNull GuiGraphics graphics) {
        Font font = Minecraft.m_91087_().f_91062_;
        for (int i = this.scroll; i < this.maxLines + this.scroll; ++i) {
            Component component = this.components.get(i);
            int x = this.m_252754_() + 5;
            int n = this.m_252907_() + 5;
            Objects.requireNonNull(font);
            int y = n + (i - this.scroll) * (9 + 3);
            graphics.m_280430_(font, component, x, y, 8092645);
        }
    }

    private void renderScrollBar(@NotNull GuiGraphics graphics) {
        if (this.components.size() > this.maxLines) {
            int scrollSize = this.f_93619_ * this.maxLines / this.components.size();
            int maxScroll = this.components.size() - this.maxLines;
            int scrollShift = (int)((float)(this.f_93619_ - scrollSize) / (float)maxScroll * (float)this.scroll);
            int x = this.m_252754_() + this.f_93618_ - 3;
            int y = this.m_252907_() + scrollShift;
            graphics.m_280509_(x, this.m_252907_(), this.m_252754_() + this.f_93618_, this.m_252907_() + this.f_93619_, -584965598);
            graphics.m_280509_(x, y, this.m_252754_() + this.f_93618_, this.m_252907_() + scrollShift + scrollSize, -578254712);
        }
    }

    public boolean m_6050_(double mouseX, double mouseY, double amount) {
        int maxScroll = this.components.size() - this.maxLines;
        if (amount < 0.0 && this.scroll < maxScroll) {
            ++this.scroll;
        }
        if (amount > 0.0 && this.scroll > 0) {
            --this.scroll;
        }
        return true;
    }

    public void setComponents(List<Component> components) {
        this.maxLines = components.size();
        this.components = components;
        this.f_93618_ = 0;
        Font font = Minecraft.m_91087_().f_91062_;
        for (Component stat : components) {
            int statWidth = font.m_92852_((FormattedText)stat);
            if (statWidth <= this.f_93618_) continue;
            this.f_93618_ = statWidth;
        }
        this.f_93618_ += 14;
        int n = components.size();
        Objects.requireNonNull(font);
        this.f_93619_ = n * (9 + 3) + 10;
        while (this.f_93619_ > this.maxHeight) {
            Objects.requireNonNull(font);
            this.f_93619_ -= 9 + 3;
            --this.maxLines;
        }
    }

    protected void m_168797_(@NotNull NarrationElementOutput output) {
    }
}

