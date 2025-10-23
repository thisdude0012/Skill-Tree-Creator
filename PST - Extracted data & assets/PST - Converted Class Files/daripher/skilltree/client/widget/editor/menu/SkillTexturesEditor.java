/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.resources.ResourceLocation
 */
package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.client.data.SkillTexturesData;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public class SkillTexturesEditor
extends EditorMenu {
    public SkillTexturesEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        this.editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> this.editor.selectMenu(this.previousMenu));
        this.editor.increaseHeight(29);
        PassiveSkill selectedSkill = this.editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        if (this.editor.canEdit(PassiveSkill::getFrameTexture)) {
            this.editor.addLabel(0, 0, "Frame Texture", ChatFormatting.GOLD);
            this.editor.increaseHeight(19);
            this.editor.addSelectionMenu(0, 0, 200, SkillTexturesData.BORDERS).setValue(selectedSkill.getFrameTexture()).setElementNameGetter(TooltipHelper::getTextureName).setResponder(this::setFrameTextures);
            this.editor.increaseHeight(19);
        }
        if (this.editor.canEdit(PassiveSkill::getTooltipFrameTexture)) {
            this.editor.addLabel(0, 0, "Tooltip Frame", ChatFormatting.GOLD);
            this.editor.increaseHeight(19);
            this.editor.addSelectionMenu(0, 0, 200, SkillTexturesData.TOOLTIP_BACKGROUNDS).setValue(selectedSkill.getTooltipFrameTexture()).setResponder(this::setTooltipFrameTextures).setElementNameGetter(TooltipHelper::getTextureName);
            this.editor.increaseHeight(19);
        }
        if (this.editor.canEdit(PassiveSkill::getIconTexture)) {
            this.editor.addLabel(0, 0, "Icon Texture", ChatFormatting.GOLD);
            this.editor.increaseHeight(19);
            this.editor.addSelectionMenu(0, 0, 200, SkillTexturesData.ICONS).setValue(selectedSkill.getIconTexture()).setElementNameGetter(TooltipHelper::getTextureName).setResponder(this::setIconTextures);
            this.editor.increaseHeight(19);
        }
    }

    private void setFrameTextures(ResourceLocation value) {
        this.editor.getSelectedSkills().forEach(s -> s.setBackgroundTexture(value));
        this.editor.saveSelectedSkills();
    }

    private void setTooltipFrameTextures(ResourceLocation value) {
        this.editor.getSelectedSkills().forEach(s -> s.setBorderTexture(value));
        this.editor.saveSelectedSkills();
    }

    private void setIconTextures(ResourceLocation value) {
        this.editor.getSelectedSkills().forEach(s -> s.setIconTexture(value));
        this.editor.saveSelectedSkills();
    }
}

