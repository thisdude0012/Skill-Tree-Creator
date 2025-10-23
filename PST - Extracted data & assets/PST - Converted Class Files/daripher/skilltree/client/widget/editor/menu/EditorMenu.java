/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.AbstractWidget
 *  org.jetbrains.annotations.Nullable
 */
package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.group.WidgetGroup;
import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.Nullable;

public abstract class EditorMenu
extends WidgetGroup<AbstractWidget> {
    protected final SkillTreeEditor editor;
    @Nullable
    public final EditorMenu previousMenu;

    public EditorMenu(SkillTreeEditor editor, @Nullable EditorMenu previousMenu) {
        super(0, 0, 0, 0);
        this.editor = editor;
        this.previousMenu = previousMenu;
    }

    public abstract void init();

    @FunctionalInterface
    protected static interface MenuConstructor {
        public EditorMenu construct(SkillTreeEditor var1, EditorMenu var2);
    }
}

