/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import daripher.skilltree.client.widget.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfirmationButton
extends Button {
    protected boolean confirming;
    private Component confirmationMessage;

    public ConfirmationButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @NotNull
    public Component m_6035_() {
        if (this.confirming && this.confirmationMessage != null) {
            return this.confirmationMessage;
        }
        return super.m_6035_();
    }

    @Override
    public void m_5691_() {
        if (!this.confirming) {
            this.confirming = true;
            return;
        }
        this.pressFunc.m_93750_((net.minecraft.client.gui.components.Button)this);
    }

    public boolean m_6375_(double pMouseX, double pMouseY, int pButton) {
        boolean clicked = super.m_6375_(pMouseX, pMouseY, pButton);
        if (!clicked) {
            this.confirming = false;
        }
        return clicked;
    }

    public void setConfirmationMessage(Component message) {
        this.confirmationMessage = message;
    }
}

