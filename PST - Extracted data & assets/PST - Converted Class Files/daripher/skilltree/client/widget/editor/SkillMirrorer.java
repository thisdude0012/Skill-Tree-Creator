/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.math.Axis
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget.editor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.client.widget.editor.SkillFactory;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.skill.PassiveSkill;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SkillMirrorer
extends AbstractWidget {
    private final SkillTreeEditor editor;
    private float mirrorCenterX;
    private float mirrorCenterY;
    private float mirrorAngle;
    private int mirrorSides = 2;

    public SkillMirrorer(SkillTreeEditor editor) {
        super(0, 0, 0, 0, (Component)Component.m_237119_());
        this.editor = editor;
        this.f_93623_ = false;
    }

    public void init() {
        this.editor.addLabel(0, 0, "Mirror", ChatFormatting.GOLD);
        this.editor.addCheckBox(186, 0, this.f_93623_).setResponder(v -> this.setActive(this.editor, (boolean)v));
        this.editor.increaseHeight(19);
        if (!this.f_93623_) {
            return;
        }
        this.editor.addLabel(0, 0, "Sectors", ChatFormatting.GOLD);
        this.editor.addNumericTextField(160, 0, 40, 14, this.mirrorSides).setNumericFilter(v -> v > 1.0).setNumericResponder(v -> {
            this.mirrorSides = v.intValue();
        });
        this.editor.increaseHeight(19);
        this.editor.addLabel(0, 0, "Angle", ChatFormatting.GOLD);
        this.editor.addNumericTextField(160, 0, 40, 14, this.mirrorAngle).setNumericResponder(v -> {
            this.mirrorAngle = v.floatValue();
        });
        this.editor.increaseHeight(19);
        this.editor.addLabel(0, 0, "Center", ChatFormatting.GOLD);
        this.editor.addNumericTextField(160, 0, 40, 14, this.mirrorCenterX).setNumericResponder(v -> {
            this.mirrorCenterX = v.floatValue();
        });
        this.editor.addNumericTextField(115, 0, 40, 14, this.mirrorCenterY).setNumericResponder(v -> {
            this.mirrorCenterY = v.floatValue();
        });
        if (this.editor.getSelectedSkills().size() != 1) {
            return;
        }
        this.editor.addButton(70, 0, 40, 14, "Set").setPressFunc(b -> this.setMirrorCenter(this.editor));
        this.editor.increaseHeight(19);
    }

    protected void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.f_93623_) {
            return;
        }
        graphics.m_280168_().m_85836_();
        int width = this.editor.getScreenWidth();
        int height = this.editor.getScreenHeight();
        float mirrorX = (float)width / 2.0f + this.mirrorCenterX * this.editor.getZoom() + this.editor.getScrollX();
        float mirrorY = (float)height / 2.0f + this.mirrorCenterY * this.editor.getZoom() + this.editor.getScrollY();
        graphics.m_280168_().m_252880_(mirrorX, mirrorY, 0.0f);
        graphics.m_280168_().m_252781_(Axis.f_252403_.m_252977_(this.mirrorAngle));
        for (int i = 0; i < this.mirrorSides; ++i) {
            graphics.m_280168_().m_252781_(Axis.f_252403_.m_252977_(360.0f / (float)this.mirrorSides));
            graphics.m_280509_(-1, -1, 1, width * 2, 0x55CFCFCF);
        }
        ScreenHelper.drawRectangle(graphics, -4, -4, 8, 8, 0x55CFCFCF);
        graphics.m_280168_().m_85849_();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    private void setActive(SkillTreeEditor editor, boolean active) {
        this.f_93623_ = active;
        editor.rebuildWidgets();
    }

    private void setMirrorCenter(SkillTreeEditor editor) {
        PassiveSkill selectedSkill = editor.getFirstSelectedSkill();
        if (selectedSkill == null) {
            return;
        }
        this.mirrorCenterX = selectedSkill.getPositionX();
        this.mirrorCenterY = selectedSkill.getPositionY();
        editor.rebuildWidgets();
    }

    @Nullable
    public PassiveSkill getMirroredSkill(PassiveSkill skill, int sector) {
        float skillX = skill.getPositionX();
        float skillY = skill.getPositionY();
        if (this.mirrorCenterX == skillX && this.mirrorCenterY == skillY) {
            return skill;
        }
        float originalAngle = (float)Math.toDegrees(Math.atan2(skillY - this.mirrorCenterY, skillX - this.mirrorCenterX)) + 90.0f;
        float sectorSize = 360.0f / (float)this.mirrorSides;
        float angle = (float)Math.toRadians(this.mirrorSides == 2 ? (double)(-originalAngle + this.mirrorAngle * 2.0f) : (double)(originalAngle + sectorSize * (float)sector));
        float distance = (float)Math.hypot(skillX - this.mirrorCenterX, skillY - this.mirrorCenterY);
        float mirroredSkillX = this.mirrorCenterX + Mth.m_14031_((float)angle) * distance;
        float mirroredSkillY = this.mirrorCenterY + Mth.m_14089_((float)((float)((double)angle + Math.PI))) * distance;
        return this.getSkillAtPosition(mirroredSkillX, mirroredSkillY);
    }

    public void createSkills(float angle, float distance, SkillFactory skillFactory) {
        if (!this.f_93623_) {
            return;
        }
        float sectorSize = 360.0f / (float)this.mirrorSides;
        int i = 1;
        while (i < this.mirrorSides) {
            angle = this.mirrorSides == 2 ? -angle - this.mirrorAngle * 2.0f : angle - sectorSize;
            float finalAngle = (float)Math.toRadians(angle);
            int sector = i++;
            this.editor.getSelectedSkills().forEach(skill -> this.createSkill(distance, finalAngle, sector, (PassiveSkill)skill, skillFactory));
        }
    }

    private void createSkill(float distance, float angle, int sector, PassiveSkill skill, SkillFactory skillFactory) {
        if ((skill = this.getMirroredSkill(skill, sector)) == null) {
            return;
        }
        float skillSize = (float)skill.getSkillSize() / 2.0f + 8.0f;
        float skillX = skill.getPositionX() + Mth.m_14031_((float)angle) * (distance + skillSize);
        float skillY = skill.getPositionY() + Mth.m_14089_((float)angle) * (distance + skillSize);
        skillFactory.accept(Float.valueOf(skillX), Float.valueOf(skillY), skill);
    }

    @Nullable
    private PassiveSkill getSkillAtPosition(float x, float y) {
        for (PassiveSkill skill : this.editor.getSkills()) {
            double distance = Math.hypot(x - skill.getPositionX(), y - skill.getPositionY());
            if (!(distance < (double)skill.getSkillSize())) continue;
            return skill;
        }
        return null;
    }

    protected void m_168797_(@NotNull NarrationElementOutput output) {
    }
}

