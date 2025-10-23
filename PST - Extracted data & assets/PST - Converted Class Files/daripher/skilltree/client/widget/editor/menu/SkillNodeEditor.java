/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 */
package daripher.skilltree.client.widget.editor.menu;

import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.NumericTextField;
import daripher.skilltree.client.widget.editor.SkillFactory;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.EditorMenu;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SkillNodeEditor
extends EditorMenu {
    private NumericTextField distanceEditor;
    private NumericTextField angleEditor;
    private static double lastUsedDistance = 10.0;
    private static double lastUsedAngle = 0.0;

    public SkillNodeEditor(SkillTreeEditor editor, EditorMenu previousMenu) {
        super(editor, previousMenu);
    }

    @Override
    public void init() {
        this.editor.addButton(0, 0, 90, 14, "Back").setPressFunc(b -> this.editor.selectMenu(this.previousMenu));
        this.editor.increaseHeight(29);
        if (this.editor.getSelectedSkills().isEmpty()) {
            return;
        }
        this.editor.addLabel(0, 0, "Distance", ChatFormatting.GOLD);
        this.editor.addLabel(65, 0, "Angle", ChatFormatting.GOLD);
        this.editor.increaseHeight(19);
        this.distanceEditor = this.editor.addNumericTextField(0, 0, 60, 14, lastUsedDistance);
        this.distanceEditor.setNumericResponder(v -> {
            lastUsedDistance = v;
        });
        this.angleEditor = this.editor.addNumericTextField(65, 0, 60, 14, lastUsedAngle);
        this.angleEditor.setNumericResponder(v -> {
            lastUsedAngle = v;
        });
        this.editor.increaseHeight(19);
        this.editor.addButton(0, 0, 60, 14, "Add").setPressFunc(b -> this.createSkills(this::createNewSkill));
        this.editor.addButton(65, 0, 60, 14, "Copy").setPressFunc(b -> this.createSkills(this::createSkillCopy));
        this.editor.increaseHeight(19);
        this.editor.addMirrorerWidgets();
    }

    private void createSkills(SkillFactory factory) {
        float angle = (float)this.angleEditor.getNumericValue();
        float distance = (float)this.distanceEditor.getNumericValue();
        this.createSkills(angle, distance, factory);
    }

    private void createSkills(float angle, float distance, SkillFactory skillFactory) {
        this.editor.getSelectedSkills().forEach(skill -> this.createSkill(distance, angle, (PassiveSkill)skill, skillFactory));
        this.editor.getSkillMirrorer().createSkills(angle, distance, skillFactory);
        this.editor.rebuildWidgets();
    }

    private void createSkill(float distance, float angle, PassiveSkill skill, SkillFactory skillFactory) {
        angle = (float)Math.toRadians(angle);
        float skillSize = (float)skill.getSkillSize() / 2.0f + 8.0f;
        float skillX = skill.getPositionX() + Mth.m_14031_((float)angle) * (distance + skillSize);
        float skillY = skill.getPositionY() + Mth.m_14089_((float)angle) * (distance + skillSize);
        skillFactory.accept(Float.valueOf(skillX), Float.valueOf(skillY), skill);
    }

    private void createSkillCopy(float x, float y, PassiveSkill original) {
        ResourceLocation skillTreeId = this.editor.getSkillTree().getId();
        PassiveSkill skill = new PassiveSkill(SkillNodeEditor.createNewSkillId(skillTreeId), original.getSkillSize(), original.getFrameTexture(), original.getIconTexture(), original.getTooltipFrameTexture(), original.isStartingPoint());
        skill.setPosition(x, y);
        skill.setConnectedTree(original.getConnectedTreeId());
        skill.setStartingPoint(original.isStartingPoint());
        original.getBonuses().stream().map(SkillBonus::copy).forEach(skill::addSkillBonus);
        original.getRequirements().stream().map(SkillRequirement::copy).forEach(skill::addSkillRequirement);
        original.getTags().forEach(skill.getTags()::add);
        skill.setTitle(original.getTitle());
        skill.setTitleColor(original.getTitleColor());
        skill.setDescription(original.getDescription());
        skill.connect(original);
        SkillTreeClientData.saveEditorSkill(skill);
        SkillTreeClientData.loadEditorSkill(skill.getId());
        this.editor.getSkillTree().getSkillIds().add(skill.getId());
        SkillTreeClientData.saveEditorSkillTree(this.editor.getSkillTree());
    }

    private void createNewSkill(float x, float y, @Nullable PassiveSkill original) {
        ResourceLocation background = new ResourceLocation("skilltree", "textures/icons/background/lesser.png");
        ResourceLocation icon = new ResourceLocation("skilltree", "textures/icons/void.png");
        ResourceLocation border = new ResourceLocation("skilltree", "textures/tooltip/lesser.png");
        ResourceLocation skillTreeId = this.editor.getSkillTree().getId();
        PassiveSkill skill = new PassiveSkill(SkillNodeEditor.createNewSkillId(skillTreeId), 16, background, icon, border, false);
        skill.setPosition(x, y);
        if (original != null) {
            skill.connect(original);
        }
        SkillTreeClientData.saveEditorSkill(skill);
        SkillTreeClientData.loadEditorSkill(skill.getId());
        this.editor.getSkillTree().getSkillIds().add(skill.getId());
        SkillTreeClientData.saveEditorSkillTree(this.editor.getSkillTree());
    }

    public static ResourceLocation createNewSkillId(ResourceLocation skillTreeId) {
        ResourceLocation id;
        int counter = 1;
        while (SkillTreeClientData.getEditorSkill(id = new ResourceLocation("skilltree", skillTreeId.m_135815_() + "_" + counter++)) != null) {
        }
        return id;
    }
}

