/*
 * Decompiled with CFR 0.152.
 */
package daripher.skilltree.client.widget.editor.menu.bonuses;

import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.List;

public class SkillBonusEditor
extends EditorMenu {
    private final int selectedBonus;

    public SkillBonusEditor(SkillTreeEditor editor, EditorMenu previousMenu, int selectedBonus) {
        super(editor, previousMenu);
        this.selectedBonus = selectedBonus;
    }

    @Override
    public void init() {
        this.editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> this.editor.selectMenu(this.previousMenu));
        this.editor.addConfirmationButton(110, 0, 90, 14, "Remove", "Confirm").setPressFunc(b -> this.deleteSelectedSkillBonuses(this.editor));
        this.editor.increaseHeight(29);
        if (!this.editor.canEditSkillBonuses()) {
            return;
        }
        PassiveSkill selectedSkill = this.editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        List<SkillBonus<?>> bonuses = selectedSkill.getBonuses();
        if (this.selectedBonus >= bonuses.size()) {
            this.editor.selectMenu(this.previousMenu);
            return;
        }
        selectedSkill.getBonuses().get(this.selectedBonus).addEditorWidgets(this.editor, this.selectedBonus, b -> this.setSkillBonuses(this.editor, (SkillBonus<?>)b));
    }

    private void setSkillBonuses(SkillTreeEditor editor, SkillBonus<?> b) {
        editor.getSelectedSkills().forEach(s -> s.getBonuses().set(this.selectedBonus, b.copy()));
        editor.saveSelectedSkills();
    }

    private void deleteSelectedSkillBonuses(SkillTreeEditor editor) {
        editor.getSelectedSkills().forEach(s -> this.removeSkillBonus((PassiveSkill)s, this.selectedBonus));
        editor.selectMenu(this.previousMenu);
        editor.saveSelectedSkills();
        editor.rebuildWidgets();
    }

    private void removeSkillBonus(PassiveSkill skill, int index) {
        if (skill.getBonuses().size() > index) {
            skill.getBonuses().remove(index);
            SkillTreeClientData.saveEditorSkill(skill);
        }
    }
}

