/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.MultiLineEditBox
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import daripher.skilltree.client.widget.TickingWidget;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextArea
extends MultiLineEditBox
implements TickingWidget {
    public TextArea(int x, int y, int width, int height, String defaultValue) {
        super(Minecraft.m_91087_().f_91062_, x, y, width, height, (Component)Component.m_237119_(), (Component)Component.m_237119_());
        this.m_240159_(defaultValue);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        return this.m_93696_() && super.m_7933_(keyCode, scanCode, modifiers);
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        return this.m_93696_() && super.m_6050_(mouseX, mouseY, delta);
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        this.m_93692_(this.m_93680_(mouseX, mouseY));
        return super.m_6375_(mouseX, mouseY, button);
    }

    public TextArea setResponder(@NotNull Consumer<String> responder) {
        super.m_239273_(responder);
        return this;
    }

    @Override
    public void onWidgetTick() {
        this.m_239213_();
    }
}

