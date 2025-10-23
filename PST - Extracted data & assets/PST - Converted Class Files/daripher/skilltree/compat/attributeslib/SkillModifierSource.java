/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.shadowsoffire.attributeslib.client.ModifierSource
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 */
package daripher.skilltree.compat.attributeslib;

import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.compat.attributeslib.AttributesLibCompatibility;
import daripher.skilltree.skill.PassiveSkill;
import dev.shadowsoffire.attributeslib.client.ModifierSource;
import java.util.Comparator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class SkillModifierSource
extends ModifierSource<PassiveSkill> {
    public SkillModifierSource(PassiveSkill skill) {
        super(AttributesLibCompatibility.SKILL_MODIFIER_TYPE, Comparator.comparing(PassiveSkill::getId), (Object)skill);
    }

    public void render(GuiGraphics graphics, Font font, int x, int y) {
        float scale = 0.5f;
        PoseStack stack = graphics.m_280168_();
        stack.m_85836_();
        stack.m_85841_(scale, scale, 1.0f);
        stack.m_252880_((float)x / scale, (float)y / scale, 0.0f);
        graphics.m_280163_(((PassiveSkill)this.data).getIconTexture(), 0, 0, 0.0f, 0.0f, 16, 16, 16, 16);
        stack.m_85849_();
    }
}

