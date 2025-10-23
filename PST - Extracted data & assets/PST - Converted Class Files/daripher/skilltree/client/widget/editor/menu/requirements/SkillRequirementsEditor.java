/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package daripher.skilltree.client.widget.editor.menu.requirements;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.client.widget.editor.menu.requirements.SkillRequirementEditor;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.util.List;
import net.minecraft.network.chat.Component;

public class SkillRequirementsEditor
extends EditorMenu {
    public SkillRequirementsEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        this.editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> this.editor.selectMenu(this.previousMenu));
        this.editor.increaseHeight(29);
        if (!this.editor.canEditSkillRequirements()) {
            return;
        }
        SkillRequirement<?> defaultRequirement = ((SkillRequirement.Serializer)PSTSkillRequirements.STAT_VALUE.get()).createDefaultInstance();
        this.editor.addSelectionMenu(110, -29, 90, defaultRequirement).setResponder(requirement -> this.addSkillRequirement(this.editor, (SkillRequirement<?>)requirement)).m_93666_((Component)Component.m_237113_((String)"Add"));
        PassiveSkill selectedSkill = this.editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        List<SkillRequirement<?>> requirements = selectedSkill.getRequirements();
        for (int i = 0; i < requirements.size(); ++i) {
            int requirementIndex = i;
            SkillRequirement<?> requirement2 = requirements.get(i);
            String message = requirement2.getTooltip().getString();
            message = TooltipHelper.getTrimmedString(message, 190);
            this.editor.addButton(0, 0, 200, 14, message).setPressFunc(b -> this.editor.selectMenu(new SkillRequirementEditor(this.editor, this, requirementIndex)));
            this.editor.increaseHeight(19);
        }
    }

    private void addSkillRequirement(SkillTreeEditor editor, SkillRequirement<?> requirement) {
        editor.getSelectedSkills().forEach(s -> s.getRequirements().add((SkillRequirement<?>)requirement.copy()));
        editor.saveSelectedSkills();
        editor.selectMenu(editor.getSelectedMenu().previousMenu);
    }
}

