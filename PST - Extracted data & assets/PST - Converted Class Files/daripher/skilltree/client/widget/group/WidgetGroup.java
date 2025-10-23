/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget.group;

import daripher.skilltree.client.widget.TickingWidget;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class WidgetGroup<T extends AbstractWidget>
extends AbstractWidget
implements TickingWidget {
    protected final Set<T> widgets = new HashSet<T>();
    protected Runnable rebuildFunc = () -> {};

    public WidgetGroup(int x, int y, int width, int height) {
        super(x, y, width, height, (Component)Component.m_237119_());
    }

    protected void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.widgetsCopy().forEach(widget -> widget.m_88315_(graphics, mouseX, mouseY, partialTick));
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_252880_(0.0f, 0.0f, 1.0f);
        graphics.m_280168_().m_85849_();
    }

    protected void m_168797_(@NotNull NarrationElementOutput output) {
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        boolean result = false;
        for (AbstractWidget widget : this.widgetsCopy()) {
            if (!widget.m_7933_(keyCode, scanCode, modifiers)) continue;
            result = true;
        }
        return result;
    }

    public boolean m_7920_(int keyCode, int scanCode, int modifiers) {
        boolean result = false;
        for (AbstractWidget widget : this.widgetsCopy()) {
            if (!widget.m_7920_(keyCode, scanCode, modifiers)) continue;
            result = true;
        }
        return result;
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        boolean result = false;
        for (AbstractWidget widget : this.widgetsCopy()) {
            if (!widget.m_6375_(mouseX, mouseY, button)) continue;
            result = true;
        }
        return result;
    }

    public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
        boolean result = false;
        for (AbstractWidget widget : this.widgetsCopy()) {
            if (!widget.m_7979_(mouseX, mouseY, button, dragX, dragY)) continue;
            result = true;
        }
        return result;
    }

    public boolean m_6348_(double mouseX, double mouseY, int button) {
        boolean result = false;
        for (AbstractWidget widget : this.widgetsCopy()) {
            if (!widget.m_6348_(mouseX, mouseY, button)) continue;
            result = true;
        }
        return result;
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        boolean result = false;
        for (AbstractWidget widget : this.widgetsCopy()) {
            if (!widget.m_6050_(mouseX, mouseY, delta)) continue;
            result = true;
        }
        return result;
    }

    public boolean m_5534_(char codePoint, int modifiers) {
        boolean result = false;
        for (AbstractWidget widget : this.widgetsCopy()) {
            if (!widget.m_5534_(codePoint, modifiers)) continue;
            result = true;
        }
        return result;
    }

    public void m_94757_(double mouseX, double mouseY) {
        this.widgetsCopy().forEach(widget -> widget.m_94757_(mouseX, mouseY));
    }

    @Override
    public void onWidgetTick() {
        for (AbstractWidget t : this.widgetsCopy()) {
            if (!(t instanceof TickingWidget)) continue;
            TickingWidget tickingWidget = (TickingWidget)t;
            tickingWidget.onWidgetTick();
        }
    }

    @NotNull
    public <W extends T> W addWidget(@NotNull W widget) {
        this.widgets.add(widget);
        return widget;
    }

    public Set<T> getWidgets() {
        return this.widgets;
    }

    public void clearWidgets() {
        this.widgets.clear();
    }

    public void setRebuildFunc(Runnable rebuildFunc) {
        this.rebuildFunc = rebuildFunc;
    }

    public void rebuildWidgets() {
        this.rebuildFunc.run();
    }

    protected HashSet<T> widgetsCopy() {
        return new HashSet<T>(this.widgets);
    }

    public Rectangle2D.Float getArea() {
        return new Rectangle2D.Float(this.m_252754_(), this.m_252907_(), this.f_93618_, this.f_93619_);
    }

    @Nullable
    public T getWidgetAt(double mouseX, double mouseY) {
        for (AbstractWidget widget : this.widgets) {
            Rectangle2D.Double widgetArea = this.getWidgetArea(widget);
            if (!widgetArea.contains(mouseX, mouseY)) continue;
            return (T)widget;
        }
        return null;
    }

    @NotNull
    protected Rectangle2D.Double getWidgetArea(T widget) {
        double width = widget.m_5711_();
        double height = widget.m_93694_();
        double x = (double)widget.m_252754_() + width / 2.0 - width / 2.0;
        double y = (double)widget.m_252907_() + height / 2.0 - height / 2.0;
        return new Rectangle2D.Double(x, y, width, height);
    }
}

