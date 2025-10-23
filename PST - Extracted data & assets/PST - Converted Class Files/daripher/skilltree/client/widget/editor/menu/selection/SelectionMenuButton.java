/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package daripher.skilltree.client.widget.editor.menu.selection;

import daripher.skilltree.client.widget.Button;
import daripher.skilltree.client.widget.SelectionList;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.selection.SelectionMenu;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public class SelectionMenuButton<T>
extends Button {
    private final SelectionList<T> selectionList;
    private Runnable onMenuInit = () -> {};
    private Consumer<T> responder = t -> {};

    public SelectionMenuButton(SkillTreeEditor editor, int x, int y, int width, String message, Collection<T> values) {
        super(x, y, width, 14, (Component)Component.m_237113_((String)message));
        this.selectionList = new SelectionList<T>(0, 0, 200, values).setMaxDisplayed(8);
        this.setPressFunc(b -> this.selectMenu(editor));
    }

    public SelectionMenuButton(SkillTreeEditor editor, int x, int y, int width, Collection<T> values) {
        this(editor, x, y, width, "", values);
    }

    public SelectionMenuButton<T> setResponder(Consumer<T> responder) {
        this.responder = responder;
        return this;
    }

    public SelectionMenuButton<T> setValue(T value) {
        this.selectionList.setValue(value);
        return this;
    }

    public SelectionMenuButton<T> setElementNameGetter(Function<T, Component> nameGetter) {
        this.selectionList.setNameGetter(nameGetter);
        T value = this.selectionList.getValue();
        if (this.m_6035_().getString().isEmpty() && value != null) {
            this.m_93666_(this.selectionList.getNameGetter().apply(value));
        }
        return this;
    }

    public void setMenuInitFunc(Runnable onMenuInit) {
        this.onMenuInit = onMenuInit;
    }

    private void selectMenu(SkillTreeEditor editor) {
        SelectionMenu<T> menu = new SelectionMenu<T>(editor, editor.getSelectedMenu(), this.selectionList, this.onMenuInit).setResponder(this.responder);
        editor.selectMenu(menu);
    }
}

