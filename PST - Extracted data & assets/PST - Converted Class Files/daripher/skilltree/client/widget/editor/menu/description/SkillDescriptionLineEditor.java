/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.chat.TextColor
 */
package daripher.skilltree.client.widget.editor.menu.description;

import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class SkillDescriptionLineEditor
extends EditorMenu {
    private final int selectedLine;

    public SkillDescriptionLineEditor(SkillTreeEditor editor, EditorMenu previousMenu, int selectedLine) {
        super(editor, previousMenu);
        this.selectedLine = selectedLine;
    }

    @Override
    public void init() {
        this.editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> this.editor.selectMenu(this.previousMenu));
        if (this.editor.getSelectedSkills().isEmpty()) {
            return;
        }
        PassiveSkill selectedSkill = this.editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        if (!this.canEditDescription()) {
            return;
        }
        List<MutableComponent> description = selectedSkill.getDescription();
        this.editor.addConfirmationButton(110, 0, 90, 14, "Remove", "Confirm").setPressFunc(b -> this.removeDescriptionLine());
        this.editor.increaseHeight(29);
        if (description == null || this.selectedLine > description.size()) {
            this.editor.selectMenu(this.previousMenu);
            return;
        }
        MutableComponent component = description.get(this.selectedLine);
        this.editor.addTextArea(0, 0, 200, 70, component.getString()).setResponder(this::setDescription);
        this.editor.increaseHeight(75);
        this.editor.addLabel(0, 0, "Color", ChatFormatting.GOLD);
        Style originalStyle = component.m_7383_();
        TextColor textColor = originalStyle.m_131135_();
        if (textColor == null) {
            textColor = TextColor.m_131266_((int)0xFFFFFF);
        }
        String color = Integer.toHexString(textColor.m_131265_());
        this.editor.addTextField(120, 0, 80, 14, color).setSoftFilter(SkillDescriptionLineEditor::isColorString).m_94151_(v -> {
            if (SkillDescriptionLineEditor.isColorString(v)) {
                int rgb = Integer.parseInt(v, 16);
                this.setDescriptionStyle(s -> s.m_178520_(rgb));
            }
        });
        this.editor.increaseHeight(19);
        this.editor.addLabel(0, 0, "Bold", ChatFormatting.GOLD);
        this.editor.addCheckBox(186, 0, originalStyle.m_131154_()).setResponder(v -> {
            this.setDescriptionStyle(s -> s.m_131136_(v));
            this.editor.rebuildWidgets();
        });
        this.editor.increaseHeight(19);
        this.editor.addLabel(0, 0, "Italic", ChatFormatting.GOLD);
        this.editor.addCheckBox(186, 0, originalStyle.m_131161_()).setResponder(v -> {
            this.setDescriptionStyle(s -> s.m_131155_(v));
            this.editor.rebuildWidgets();
        });
        this.editor.increaseHeight(19);
        this.editor.addLabel(0, 0, "Underline", ChatFormatting.GOLD);
        this.editor.addCheckBox(186, 0, originalStyle.m_131171_()).setResponder(v -> {
            this.setDescriptionStyle(s -> s.m_131162_(v));
            this.editor.rebuildWidgets();
        });
        this.editor.increaseHeight(19);
        this.editor.addLabel(0, 0, "Strikethrough", ChatFormatting.GOLD);
        this.editor.addCheckBox(186, 0, originalStyle.m_131168_()).setResponder(v -> {
            this.setDescriptionStyle(s -> s.m_178522_(v));
            this.editor.rebuildWidgets();
        });
        this.editor.increaseHeight(19);
        this.editor.addLabel(0, 0, "Obfuscated", ChatFormatting.GOLD);
        this.editor.addCheckBox(186, 0, originalStyle.m_131176_()).setResponder(v -> {
            this.setDescriptionStyle(s -> s.m_178524_(v));
            this.editor.rebuildWidgets();
        });
        this.editor.increaseHeight(19);
    }

    private boolean canEditDescription() {
        PassiveSkill selectedSkill = this.editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return false;
        }
        if (this.editor.getSelectedSkills().size() < 2) {
            return true;
        }
        for (PassiveSkill otherSkill : this.editor.getSelectedSkills()) {
            List<MutableComponent> description = selectedSkill.getDescription();
            List<MutableComponent> otherDescription = otherSkill.getDescription();
            if (description == null && otherDescription == null) continue;
            if (description == null || otherDescription == null) {
                return false;
            }
            if (description.size() != otherDescription.size()) {
                return false;
            }
            for (int i = 0; i < description.size(); ++i) {
                if (description.get(i).equals((Object)otherDescription.get(i))) continue;
                return false;
            }
        }
        return true;
    }

    private void removeDescriptionLine() {
        this.editor.getSelectedSkills().forEach(skill -> {
            List<MutableComponent> description = skill.getDescription();
            Objects.requireNonNull(description);
            description.remove(this.selectedLine);
        });
        this.editor.saveSelectedSkills();
        this.editor.selectMenu(this.previousMenu);
        this.editor.rebuildWidgets();
    }

    private void setDescription(String line) {
        this.editor.getSelectedSkills().forEach(skill -> {
            List<MutableComponent> description = skill.getDescription();
            Objects.requireNonNull(description);
            MutableComponent component = description.get(this.selectedLine);
            Style style = component.m_7383_();
            description.set(this.selectedLine, Component.m_237113_((String)line).m_130948_(style));
        });
        this.editor.saveSelectedSkills();
    }

    private void setDescriptionStyle(Function<Style, Style> styleFunc) {
        this.editor.getSelectedSkills().forEach(skill -> {
            List<MutableComponent> description = skill.getDescription();
            Objects.requireNonNull(description);
            MutableComponent component = description.get(this.selectedLine);
            Style style = (Style)styleFunc.apply(component.m_7383_());
            description.set(this.selectedLine, component.m_130948_(style));
            SkillTreeClientData.saveEditorSkill(skill);
            SkillTreeClientData.loadEditorSkill(skill.getId());
        });
    }

    private static boolean isColorString(String v) {
        return v.matches("^[a-fA-F0-9]{6}");
    }
}

