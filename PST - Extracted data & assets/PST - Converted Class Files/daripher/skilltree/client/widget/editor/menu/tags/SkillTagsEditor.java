/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 */
package daripher.skilltree.client.widget.editor.menu.tags;

import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.client.widget.editor.menu.tags.SkillTagLimitsEditor;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;

public class SkillTagsEditor
extends EditorMenu {
    public SkillTagsEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        this.editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> this.editor.selectMenu(this.previousMenu));
        this.editor.increaseHeight(29);
        if (this.editor.getSelectedSkills().isEmpty()) {
            return;
        }
        PassiveSkill selectedSkill = this.editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        if (!this.canEditTags()) {
            return;
        }
        PassiveSkillTree skillTree = this.editor.getSkillTree();
        Map<String, Integer> limitations = skillTree.getSkillLimitations();
        this.editor.addLabel(0, 0, "Tag List", ChatFormatting.GOLD);
        this.editor.increaseHeight(19);
        List<String> tags = selectedSkill.getTags();
        for (int i = 0; i < tags.size(); ++i) {
            int index = i;
            this.editor.addTextField(0, 0, 200, 14, tags.get(i)).m_94151_(v -> {
                this.editor.getSelectedSkills().forEach(s -> s.getTags().set(index, (String)v));
                this.editor.saveSelectedSkills();
            });
            this.editor.increaseHeight(19);
        }
        this.editor.increaseHeight(10);
        this.editor.addButton(0, 0, 90, 14, "Add").setPressFunc(b -> {
            Object name = "New Tag";
            while (selectedSkill.getTags().contains(name)) {
                name = (String)name + "1";
            }
            String finalName = name;
            this.editor.getSelectedSkills().forEach(s -> s.getTags().add(finalName));
            this.editor.saveSelectedSkills();
            this.editor.rebuildWidgets();
        });
        if (!tags.isEmpty()) {
            this.editor.addButton(110, 0, 90, 14, "Remove").setPressFunc(b -> {
                this.editor.getSelectedSkills().forEach(s -> s.getTags().remove(tags.size() - 1));
                this.editor.saveSelectedSkills();
                this.editor.rebuildWidgets();
            });
        }
        this.editor.increaseHeight(19);
        this.editor.addButton(0, 0, 200, 14, "Tree Limitations").setPressFunc(b -> this.editor.selectMenu(new SkillTagLimitsEditor(this.editor, this)));
        this.editor.increaseHeight(19);
        this.editor.increaseHeight(10);
        this.editor.addButton(0, 0, 90, 14, "Add").setPressFunc(b -> {
            Object name = "New Tag";
            while (limitations.containsKey(name)) {
                name = (String)name + "1";
            }
            limitations.put((String)name, 1);
            this.editor.rebuildWidgets();
            SkillTreeClientData.saveEditorSkillTree(skillTree);
        });
        this.editor.increaseHeight(19);
    }

    protected boolean canEditTags() {
        PassiveSkill selectedSkill = this.editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return false;
        }
        if (this.editor.getSelectedSkills().size() < 2) {
            return true;
        }
        for (PassiveSkill otherSkill : this.editor.getSelectedSkills()) {
            if (selectedSkill == otherSkill) continue;
            List<String> tags = selectedSkill.getTags();
            List<String> otherTags = otherSkill.getTags();
            if (tags.size() != otherTags.size()) {
                return false;
            }
            for (int i = 0; i < tags.size(); ++i) {
                if (tags.get(i).equals(otherTags.get(i))) continue;
                return false;
            }
        }
        return true;
    }
}

