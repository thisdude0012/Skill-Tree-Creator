/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.resources.ResourceLocation
 */
package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.client.widget.skill.SkillConnection;
import daripher.skilltree.skill.PassiveSkill;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public class SkillConnectionsEditor
extends EditorMenu {
    public SkillConnectionsEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        this.editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> this.editor.selectMenu(this.previousMenu));
        this.editor.increaseHeight(29);
        if (this.editor.getSelectedSkills().size() < 2) {
            return;
        }
        if (this.selectedSkillsConnected()) {
            this.editor.addButton(0, 0, 100, 14, "Disconnect").setPressFunc(b -> this.disconnectSelectedSkills());
        } else {
            this.editor.addLabel(0, 0, "Connect", new ChatFormatting[0]);
            this.editor.increaseHeight(19);
            this.editor.addButton(0, 0, 100, 14, "Direct").setPressFunc(b -> this.connectSelectedSkills(SkillConnection.Type.DIRECT));
            this.editor.increaseHeight(19);
            this.editor.addButton(0, 0, 100, 14, "Long").setPressFunc(b -> this.connectSelectedSkills(SkillConnection.Type.LONG));
            this.editor.increaseHeight(19);
            this.editor.addButton(0, 0, 100, 14, "One Way").setPressFunc(b -> this.connectSelectedSkills(SkillConnection.Type.ONE_WAY));
        }
        this.editor.increaseHeight(19);
    }

    private boolean selectedSkillsConnected() {
        PassiveSkill[] selectedSkills = this.editor.getSelectedSkills().toArray(new PassiveSkill[0]);
        for (int i = 0; i < selectedSkills.length - 1; ++i) {
            PassiveSkill skill1 = selectedSkills[i];
            PassiveSkill skill2 = selectedSkills[i + 1];
            if (this.skillsConnected(skill1, skill2)) continue;
            return false;
        }
        return true;
    }

    private boolean skillsConnected(PassiveSkill first, PassiveSkill second) {
        return first.getDirectConnections().contains(second.getId()) || second.getDirectConnections().contains(first.getId()) || first.getLongConnections().contains(second.getId()) || second.getLongConnections().contains(first.getId()) || first.getOneWayConnections().contains(second.getId()) || second.getOneWayConnections().contains(first.getId());
    }

    private void disconnectSelectedSkills() {
        PassiveSkill[] selectedSkills = this.editor.getSelectedSkills().toArray(new PassiveSkill[0]);
        for (int i = 0; i < selectedSkills.length - 1; ++i) {
            PassiveSkill skill1 = selectedSkills[i];
            PassiveSkill skill2 = selectedSkills[i + 1];
            skill1.getDirectConnections().remove(skill2.getId());
            skill2.getDirectConnections().remove(skill1.getId());
            skill1.getLongConnections().remove(skill2.getId());
            skill2.getLongConnections().remove(skill1.getId());
            skill1.getOneWayConnections().remove(skill2.getId());
            skill2.getOneWayConnections().remove(skill1.getId());
        }
        this.editor.saveSelectedSkills();
        this.editor.rebuildWidgets();
    }

    private void connectSelectedSkills(SkillConnection.Type connectionType) {
        PassiveSkill[] selectedSkills = this.editor.getSelectedSkills().toArray(new PassiveSkill[0]);
        for (int i = 0; i < selectedSkills.length - 1; ++i) {
            PassiveSkill skill = selectedSkills[i];
            List<ResourceLocation> connections = switch (connectionType) {
                default -> throw new IncompatibleClassChangeError();
                case SkillConnection.Type.DIRECT -> skill.getDirectConnections();
                case SkillConnection.Type.LONG -> skill.getLongConnections();
                case SkillConnection.Type.ONE_WAY -> skill.getOneWayConnections();
            };
            connections.add(selectedSkills[i + 1].getId());
        }
        this.editor.saveSelectedSkills();
        this.editor.rebuildWidgets();
    }
}

