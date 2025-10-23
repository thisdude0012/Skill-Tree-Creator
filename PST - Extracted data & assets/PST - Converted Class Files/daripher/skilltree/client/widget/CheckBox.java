/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import daripher.skilltree.client.widget.Button;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CheckBox
extends Button {
    private boolean value;
    private Consumer<Boolean> responder = b -> {};

    public CheckBox(int x, int y, boolean defaultValue) {
        super(x, y, 14, 14, (Component)Component.m_237119_());
        this.value = defaultValue;
    }

    @Override
    public void m_5691_() {
        this.value ^= true;
        this.responder.accept(this.value);
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics graphics) {
        super.renderBackground(graphics);
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets.png");
        if (this.value) {
            graphics.m_280218_(texture, this.m_252754_(), this.m_252907_(), 0, 242, this.f_93618_, this.f_93619_);
        }
    }

    @Override
    protected int getTextureVariant() {
        return this.m_198029_() ? 3 : 4;
    }

    public void setResponder(Consumer<Boolean> responder) {
        this.responder = responder;
    }
}

