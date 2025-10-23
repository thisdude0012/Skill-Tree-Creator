/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package daripher.skilltree.client.widget.editor;

import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.skill.PassiveSkill;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillSelector
extends AbstractWidget {
    private static final int SELECTION_COLOR = -292164812;
    private final Set<PassiveSkill> selectedSkills = new LinkedHashSet<PassiveSkill>();
    private final SkillButtons skillButtons;
    private final SkillTreeEditor editor;
    private int selectionStartX;
    private int selectionStartY;

    public SkillSelector(SkillTreeEditor editor, SkillButtons skillButtons) {
        super(0, 0, 0, 0, (Component)Component.m_237119_());
        this.skillButtons = skillButtons;
        this.editor = editor;
        this.f_93623_ = false;
    }

    protected void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.f_93623_) {
            this.renderSelectionArea(graphics, mouseX, mouseY);
        }
        this.renderSelectedSkillsHighlight(graphics);
    }

    private void renderSelectedSkillsHighlight(@NotNull GuiGraphics graphics) {
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_252880_(this.skillButtons.getScrollX(), this.skillButtons.getScrollY(), 0.0f);
        float zoom = this.skillButtons.getZoom();
        for (SkillButton widget : this.getSelectedButtons()) {
            this.renderSkillSelection(graphics, widget, zoom);
        }
        graphics.m_280168_().m_85849_();
    }

    private void renderSkillSelection(@NotNull GuiGraphics graphics, SkillButton widget, float zoom) {
        graphics.m_280168_().m_85836_();
        double widgetCenterX = (float)widget.m_252754_() + (float)widget.m_5711_() / 2.0f;
        double widgetCenterY = (float)widget.m_252907_() + (float)widget.m_93694_() / 2.0f;
        graphics.m_280168_().m_85837_(widgetCenterX, widgetCenterY, 0.0);
        graphics.m_280168_().m_85841_(zoom, zoom, 1.0f);
        graphics.m_280168_().m_85837_(-widgetCenterX, -widgetCenterY, 0.0);
        int x = widget.m_252754_() - 1;
        int y = widget.m_252907_() - 1;
        int width = widget.m_5711_() + 2;
        int height = widget.m_93694_() + 2;
        ScreenHelper.drawRectangle(graphics, x, y, width, height, -292164812);
        graphics.m_280168_().m_85849_();
    }

    private void renderSelectionArea(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        ScreenHelper.drawRectangle(graphics, this.selectionStartX, this.selectionStartY, mouseX - this.selectionStartX, mouseY - this.selectionStartY, -292164812);
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (this.editor.getArea().contains(mouseX, mouseY)) {
            return false;
        }
        if (Screen.m_96637_()) {
            return false;
        }
        if (Screen.m_96638_()) {
            this.f_93623_ = true;
            this.selectionStartX = (int)mouseX;
            this.selectionStartY = (int)mouseY;
        } else {
            SkillButton clickedWidget;
            if (!this.selectedSkills.isEmpty()) {
                this.clearSelection();
            }
            if ((clickedWidget = (SkillButton)((Object)this.skillButtons.getWidgetAt(mouseX, mouseY))) == null) {
                return false;
            }
            PassiveSkill clickedSkill = clickedWidget.skill;
            if (this.selectedSkills.contains(clickedSkill)) {
                this.selectedSkills.remove(clickedSkill);
            } else {
                this.selectedSkills.add(clickedSkill);
            }
            this.editor.rebuildWidgets();
        }
        return true;
    }

    public boolean m_6348_(double mouseX, double mouseY, int button) {
        if (this.f_93623_) {
            this.addSelectedSkills(mouseX, mouseY);
            this.f_93623_ = false;
            this.editor.rebuildWidgets();
            return true;
        }
        return false;
    }

    private void addSelectedSkills(double mouseX, double mouseY) {
        Rectangle2D selectedArea = this.getSelectionArea(mouseX, mouseY);
        for (SkillButton skillButton : this.skillButtons.getWidgets()) {
            Rectangle2D skillArea = this.getSkillArea(skillButton);
            if (!selectedArea.intersects(skillArea)) continue;
            this.selectedSkills.add(skillButton.skill);
        }
        this.editor.rebuildWidgets();
    }

    @NotNull
    private Rectangle2D getSelectionArea(double mouseX, double mouseY) {
        double x = Math.min(mouseX, (double)this.selectionStartX) - (double)this.skillButtons.getScrollX();
        double y = Math.min(mouseY, (double)this.selectionStartY) - (double)this.skillButtons.getScrollY();
        double width = Math.abs(mouseX - (double)this.selectionStartX);
        double height = Math.abs(mouseY - (double)this.selectionStartY);
        return new Rectangle2D.Double(x, y, width, height);
    }

    @NotNull
    private Rectangle2D getSkillArea(SkillButton skill) {
        double skillSize = (float)skill.skill.getSkillSize() * this.skillButtons.getZoom();
        double skillX = (double)skill.x + (double)skill.m_5711_() / 2.0 - skillSize / 2.0;
        double skillY = (double)skill.y + (double)skill.m_93694_() / 2.0 - skillSize / 2.0;
        return new Rectangle2D.Double(skillX, skillY, skillSize, skillSize);
    }

    public Set<PassiveSkill> getSelectedSkills() {
        return this.selectedSkills;
    }

    public void clearSelection() {
        this.selectedSkills.clear();
        this.editor.rebuildWidgets();
    }

    @Nullable
    public PassiveSkill getFirstSelectedSkill() {
        if (this.selectedSkills.isEmpty()) {
            return null;
        }
        return (PassiveSkill)this.selectedSkills.toArray()[0];
    }

    @NotNull
    private List<SkillButton> getSelectedButtons() {
        return this.selectedSkills.stream().map(PassiveSkill::getId).map(this.skillButtons::getWidgetById).toList();
    }

    protected void m_168797_(@NotNull NarrationElementOutput output) {
    }
}

