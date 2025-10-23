/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractButton
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import daripher.skilltree.client.tooltip.TooltipHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SelectionList<T>
extends AbstractButton {
    public static final ResourceLocation WIDGETS_TEXTURE = new ResourceLocation("skilltree:textures/screen/widgets.png");
    private static final int LINE_HEIGHT = 14;
    private Function<T, Component> nameGetter = t -> Component.m_237113_((String)t.toString());
    private Consumer<T> responder = t -> {};
    private final List<T> valuesList;
    private String search = "";
    private T value;
    private int maxDisplayed;
    private int maxScroll;
    private int scroll;

    public SelectionList(int x, int y, int width, Collection<T> possibleValues) {
        super(x, y, width, 14, (Component)Component.m_237119_());
        this.valuesList = new ArrayList<T>(possibleValues);
        this.setMaxDisplayed(10);
    }

    public void m_5691_() {
        this.responder.accept(this.value);
    }

    public void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.f_93624_) {
            return;
        }
        int y = this.m_252907_();
        this.renderLineBackground(graphics, y, 42, 7);
        for (int i = 0; i < this.maxDisplayed - 1; ++i) {
            int rowY = y + 7 + i * 14;
            this.renderLineBackground(graphics, rowY, 70, 14);
        }
        this.renderLineBackground(graphics, y += (this.maxDisplayed - 1) * 14 + 7, 49, 7);
        y -= (this.maxDisplayed - 1) * 14 + 7;
        Minecraft minecraft = Minecraft.m_91087_();
        Font font = minecraft.f_91062_;
        for (int i = 0; i < this.maxDisplayed; ++i) {
            this.renderLine(graphics, i, y, font);
        }
        this.renderScroll(graphics);
    }

    private void renderLine(@NotNull GuiGraphics graphics, int line, int y, Font font) {
        String selectedText;
        List<T> values = this.getValues();
        if (line + this.scroll >= values.size()) {
            return;
        }
        String text = this.nameGetter.apply(values.get(line + this.scroll)).getString();
        int textColor = text.equals(selectedText = this.nameGetter.apply(this.value).getString()) ? 0x55FF55 : 0xE0E0E0;
        text = TooltipHelper.getTrimmedString(text, this.f_93618_ - 10);
        int textX = this.m_252754_() + 5;
        int textY = y + 3 + line * 14;
        this.renderLine(graphics, font, text, textX, textY, textColor);
    }

    private void renderLine(@NotNull GuiGraphics graphics, Font font, String line, int textX, int textY, int textColor) {
        String lowerCase = line.toLowerCase();
        if (!this.search.isEmpty() && lowerCase.contains(this.search)) {
            String split1 = line.substring(0, lowerCase.indexOf(this.search));
            graphics.m_280488_(font, split1, textX, textY, textColor);
            String split2 = line.substring(lowerCase.indexOf(this.search), lowerCase.indexOf(this.search) + this.search.length());
            graphics.m_280488_(font, split2, textX += font.m_92895_(split1), textY, 16766530);
            String split3 = line.substring(lowerCase.indexOf(this.search) + this.search.length());
            graphics.m_280488_(font, split3, textX += font.m_92895_(split2), textY, textColor);
        } else {
            graphics.m_280488_(font, line, textX, textY, textColor);
        }
    }

    private List<T> getValues() {
        if (!this.search.isEmpty()) {
            return this.valuesList.stream().filter(this::isSearched).toList();
        }
        return this.valuesList;
    }

    private boolean isSearched(T value) {
        return this.nameGetter.apply(value).getString().toLowerCase().contains(this.search);
    }

    private void renderLineBackground(@NotNull GuiGraphics graphics, int rowY, int vOffset, int height) {
        graphics.m_280218_(WIDGETS_TEXTURE, this.m_252754_(), rowY, 0, vOffset, this.f_93618_ / 2, height);
        graphics.m_280218_(WIDGETS_TEXTURE, this.m_252754_() + this.f_93618_ / 2, rowY, -this.f_93618_ / 2, vOffset, this.f_93618_ / 2, height);
    }

    private void renderScroll(GuiGraphics graphics) {
        if (this.getValues().size() <= this.maxDisplayed) {
            return;
        }
        int maxScrollSize = this.f_93619_ - 8;
        int scrollSize = maxScrollSize * this.maxDisplayed / this.getValues().size();
        int x = this.m_252754_() + this.f_93618_ - 4;
        int y = this.m_252907_() + 3 + (maxScrollSize - scrollSize) * this.scroll / this.maxScroll;
        graphics.m_280509_(x, y, x + 1, y + scrollSize + 1, -5592406);
    }

    public void m_5716_(double mouseX, double mouseY) {
        List<T> values;
        if (!this.m_93680_(mouseX, mouseY)) {
            return;
        }
        int clickedLine = ((int)mouseY - this.m_252907_()) / 14 + this.scroll;
        if (clickedLine >= (values = this.getValues()).size()) {
            return;
        }
        this.value = values.get(clickedLine);
        this.m_5691_();
    }

    public void m_94757_(double mouseX, double mouseY) {
        super.m_94757_(mouseX, mouseY);
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.m_5953_(mouseX, mouseY)) {
            this.setScroll(this.scroll - Mth.m_14205_((double)delta));
            return true;
        }
        return false;
    }

    public boolean m_5534_(char codePoint, int modifiers) {
        if (!this.m_198029_()) {
            return false;
        }
        if (!SharedConstants.m_136188_((char)codePoint)) {
            return false;
        }
        this.search = this.search + Character.toLowerCase(codePoint);
        this.setScrollToSelection();
        return true;
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (this.search.isEmpty()) {
            return false;
        }
        if (keyCode == 259) {
            this.search = this.search.substring(0, this.search.length() - 1);
            this.setScrollToSelection();
            return true;
        }
        if (keyCode == 256) {
            this.search = "";
            this.setScrollToSelection();
            return true;
        }
        return false;
    }

    private void setScroll(int scroll) {
        this.scroll = Math.min(this.maxScroll, Math.max(0, scroll));
    }

    public SelectionList<T> setNameGetter(Function<T, Component> nameGetter) {
        this.nameGetter = nameGetter;
        this.getValues().sort((v1, v2) -> {
            String name1 = ((Component)nameGetter.apply(v1)).getString();
            String name2 = ((Component)nameGetter.apply(v2)).getString();
            return name1.compareTo(name2);
        });
        this.setScrollToSelection();
        return this;
    }

    public Function<T, Component> getNameGetter() {
        return this.nameGetter;
    }

    public SelectionList<T> setResponder(Consumer<T> responder) {
        this.responder = responder;
        return this;
    }

    public T getValue() {
        return this.value;
    }

    public SelectionList<T> setValue(T value) {
        this.value = value;
        this.setScrollToSelection();
        return this;
    }

    public int getMaxDisplayed() {
        return this.maxDisplayed;
    }

    public SelectionList<T> setMaxDisplayed(int maxDisplayed) {
        this.maxDisplayed = maxDisplayed = Math.min(maxDisplayed, this.getValues().size());
        this.maxScroll = this.getValues().size() - maxDisplayed;
        this.setHeight(14 * maxDisplayed);
        return this;
    }

    public void setScrollToSelection() {
        this.setScroll(this.getValues().indexOf(this.value));
    }

    protected void m_168797_(@NotNull NarrationElementOutput output) {
    }
}

