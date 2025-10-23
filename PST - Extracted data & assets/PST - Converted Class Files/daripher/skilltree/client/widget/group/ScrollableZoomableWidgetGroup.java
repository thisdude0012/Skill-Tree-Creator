/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget.group;

import daripher.skilltree.client.widget.group.WidgetGroup;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.NotNull;

public class ScrollableZoomableWidgetGroup<T extends AbstractWidget>
extends WidgetGroup<T> {
    protected float scrollX;
    protected float scrollY;
    protected int maxScrollX;
    protected int maxScrollY;
    private float zoom = 1.0f;

    public ScrollableZoomableWidgetGroup(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
    }

    @Override
    protected void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.m_280588_(this.m_252754_(), this.m_252907_(), this.m_252754_() + this.m_5711_(), this.m_252907_() + this.m_93694_());
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_252880_(this.scrollX, this.scrollY, 0.0f);
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        for (AbstractWidget widget : this.widgets) {
            graphics.m_280168_().m_85836_();
            double widgetCenterX = (float)widget.m_252754_() + (float)widget.m_5711_() / 2.0f;
            double widgetCenterY = (float)widget.m_252907_() + (float)widget.m_93694_() / 2.0f;
            graphics.m_280168_().m_85837_(widgetCenterX, widgetCenterY, 0.0);
            graphics.m_280168_().m_85841_(this.zoom, this.zoom, 1.0f);
            graphics.m_280168_().m_85837_(-widgetCenterX, -widgetCenterY, 0.0);
            widget.m_88315_(graphics, mouseX, mouseY, partialTick);
            graphics.m_280168_().m_85849_();
        }
        graphics.m_280168_().m_85849_();
        graphics.m_280618_();
    }

    protected void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button != 2) {
            return false;
        }
        if (this.maxScrollX > 0) {
            this.scrollX += (float)dragX;
        }
        if (this.maxScrollY > 0) {
            this.scrollY += (float)dragY;
        }
        return super.m_7979_(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (delta > 0.0 && this.zoom < 2.0f) {
            this.zoom += 0.05f;
            this.scrollX *= 1.05f;
            this.scrollY *= 1.05f;
        }
        if (delta < 0.0 && this.zoom > 0.25f) {
            this.zoom -= 0.05f;
            this.scrollX *= 0.95f;
            this.scrollY *= 0.95f;
        }
        this.rebuildFunc.run();
        return true;
    }

    @Override
    @Nullable
    public T getWidgetAt(double mouseX, double mouseY) {
        mouseX -= (double)this.scrollX;
        mouseY -= (double)this.scrollY;
        for (AbstractWidget widget : this.widgets) {
            Rectangle2D.Double widgetArea = this.getWidgetArea(widget);
            if (!widgetArea.contains(mouseX, mouseY)) continue;
            return (T)widget;
        }
        return null;
    }

    @Override
    @NotNull
    protected Rectangle2D.Double getWidgetArea(T widget) {
        double width = (float)widget.m_5711_() * this.zoom;
        double height = (float)widget.m_93694_() * this.zoom;
        double x = (double)widget.m_252754_() + (double)widget.m_5711_() / 2.0 - width / 2.0;
        double y = (double)widget.m_252907_() + (double)widget.m_93694_() / 2.0 - height / 2.0;
        return new Rectangle2D.Double(x, y, width, height);
    }

    public void setMaxScrollX(int maxScrollX) {
        this.maxScrollX = maxScrollX;
    }

    public void setMaxScrollY(int maxScrollY) {
        this.maxScrollY = maxScrollY;
    }

    public int getMaxScrollX() {
        return this.maxScrollX;
    }

    public int getMaxScrollY() {
        return this.maxScrollY;
    }

    public float getScrollX() {
        return this.scrollX;
    }

    public float getScrollY() {
        return this.scrollY;
    }

    public float getZoom() {
        return this.zoom;
    }
}

