/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package daripher.skilltree.client.widget;

import daripher.skilltree.client.widget.TickingWidget;
import daripher.skilltree.mixin.EditBoxAccessor;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextField
extends EditBox
implements TickingWidget {
    public static final int INVALID_TEXT_COLOR = 0xD80000;
    private static final int HINT_COLOR = 0x575757;
    private Predicate<String> softFilter = Objects::nonNull;
    private Function<String, @Nullable String> suggestionProvider = s -> null;
    private String hint = null;

    public TextField(int x, int y, int width, int height, String defaultText) {
        super(Minecraft.m_91087_().f_91062_, x, y, width, height, (Component)Component.m_237119_());
        this.m_94199_(80);
        this.m_94144_(defaultText);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (this.m_94204_() && keyCode == 256) {
            this.m_93692_(false);
            return true;
        }
        EditBoxAccessor accessor = (EditBoxAccessor)((Object)this);
        if (keyCode == 258 && accessor.getSuggestion() != null) {
            this.m_94144_(this.m_94155_() + accessor.getSuggestion());
            this.m_94167_(null);
            return true;
        }
        boolean result = super.m_7933_(keyCode, scanCode, modifiers);
        this.m_94167_(this.suggestionProvider.apply(this.m_94155_()));
        return result;
    }

    public boolean m_5534_(char codePoint, int modifiers) {
        boolean result = super.m_5534_(codePoint, modifiers);
        this.m_94167_(this.suggestionProvider.apply(this.m_94155_()));
        return result;
    }

    public void m_94151_(@NotNull Consumer<String> responder) {
        super.m_94151_(s -> {
            if (!this.isValueValid()) {
                return;
            }
            responder.accept((String)s);
        });
    }

    public void setSuggestionProvider(Function<String, @Nullable String> suggestionProvider) {
        this.suggestionProvider = suggestionProvider;
    }

    public TextField setSoftFilter(Predicate<String> filter) {
        this.softFilter = filter;
        return this;
    }

    public void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int textX;
        boolean isCursorVisible;
        EditBoxAccessor accessor = (EditBoxAccessor)((Object)this);
        if (!this.m_94213_()) {
            return;
        }
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets.png");
        int v = this.m_198029_() ? 42 : 56;
        graphics.m_280218_(texture, this.m_252754_(), this.m_252907_(), 0, v, this.f_93618_ / 2, this.f_93619_);
        graphics.m_280218_(texture, this.m_252754_() + this.f_93618_ / 2, this.m_252907_(), -this.f_93618_ / 2, v, this.f_93618_ / 2, this.f_93619_);
        int textColor = this.getTextColor();
        int cursorVisiblePosition = this.m_94207_() - accessor.getDisplayPos();
        int highlightWidth = accessor.getHighlightPos() - accessor.getDisplayPos();
        Minecraft minecraft = Minecraft.m_91087_();
        Font font = minecraft.f_91062_;
        String visibleText = font.m_92834_(this.m_94155_().substring(accessor.getDisplayPos()), this.m_94210_());
        boolean isTextSplitByCursor = cursorVisiblePosition >= 0 && cursorVisiblePosition <= visibleText.length();
        boolean bl = isCursorVisible = this.m_93696_() && accessor.getFrame() / 6 % 2 == 0 && isTextSplitByCursor;
        if (visibleText.isEmpty() && this.hint != null && !this.m_93696_()) {
            visibleText = this.hint;
        }
        int textStartX = textX = this.m_252754_() + 5;
        int textY = this.m_252907_() + 3;
        if (highlightWidth > visibleText.length()) {
            highlightWidth = visibleText.length();
        }
        if (!visibleText.isEmpty()) {
            String s1 = isTextSplitByCursor ? visibleText.substring(0, cursorVisiblePosition) : visibleText;
            textX = graphics.m_280649_(font, accessor.getFormatter().apply(s1, accessor.getDisplayPos()), textX, textY, textColor, true);
        }
        boolean isCursorSurrounded = this.m_94207_() < this.m_94155_().length() || this.m_94155_().length() >= accessor.getMaxLength();
        int cursorX = textX;
        if (!isTextSplitByCursor) {
            cursorX = cursorVisiblePosition > 0 ? this.m_252754_() + this.f_93618_ : this.m_252754_();
        } else if (isCursorSurrounded) {
            cursorX = textX - 1;
            --textX;
        }
        if (!visibleText.isEmpty() && isTextSplitByCursor && cursorVisiblePosition < visibleText.length()) {
            graphics.m_280649_(font, accessor.getFormatter().apply(visibleText.substring(cursorVisiblePosition), this.m_94207_()), textX, textY, textColor, true);
        }
        if (!isCursorSurrounded && accessor.getSuggestion() != null) {
            graphics.m_280056_(font, accessor.getSuggestion(), cursorX - 1, textY, -8355712, true);
        }
        if (isCursorVisible) {
            if (isCursorSurrounded) {
                graphics.m_280509_(cursorX, textY - 1, cursorX + 1, textY + 9, -3092272);
            } else {
                graphics.m_280056_(font, "_", cursorX, textY, textColor, true);
            }
        }
        if (highlightWidth != cursorVisiblePosition) {
            int highlightEndX = textStartX + font.m_92895_(visibleText.substring(0, highlightWidth));
            accessor.invokeRenderHighlight(graphics, cursorX, textY - 1, highlightEndX - 1, textY + 9);
        }
    }

    public boolean isValueValid() {
        return this.softFilter.test(this.m_94155_());
    }

    public TextField setHint(@Nullable String hint) {
        this.hint = hint;
        return this;
    }

    private int getTextColor() {
        return this.m_94155_().isEmpty() ? 0x575757 : (this.isValueValid() ? 0xE0E0E0 : 0xD80000);
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        this.m_93692_(this.m_93680_(mouseX, mouseY));
        return super.m_6375_(mouseX, mouseY, button);
    }

    @Override
    public void onWidgetTick() {
        this.m_94120_();
    }
}

