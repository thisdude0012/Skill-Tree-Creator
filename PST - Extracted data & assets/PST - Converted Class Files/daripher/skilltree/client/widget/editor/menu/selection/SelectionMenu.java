/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package daripher.skilltree.client.widget.editor.menu.selection;

import daripher.skilltree.client.widget.SelectionList;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectionMenu<T>
extends EditorMenu {
    @NotNull
    private Consumer<T> responder = v -> {};
    private final SelectionList<T> selectionList;
    private final Runnable onInit;

    public SelectionMenu(SkillTreeEditor editor, @Nullable EditorMenu previousMenu, SelectionList<T> selectionList, Runnable onInit) {
        super(editor, previousMenu);
        this.selectionList = selectionList;
        this.onInit = onInit;
    }

    @Override
    public void init() {
        this.clearWidgets();
        this.editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> this.editor.selectMenu(this.previousMenu));
        this.editor.increaseHeight(29);
        this.selectionList.m_252865_(this.editor.getWidgetsX(0));
        this.selectionList.m_253211_(this.editor.getWidgetsY(0));
        this.editor.increaseHeight(this.selectionList.getMaxDisplayed() * 14 + 10);
        this.selectionList.setResponder(this.responder);
        this.addWidget(this.selectionList);
        this.onInit.run();
    }

    public SelectionMenu<T> setResponder(@NotNull Consumer<T> responder) {
        this.responder = responder;
        return this;
    }
}

