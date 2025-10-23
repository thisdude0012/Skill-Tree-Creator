/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 */
package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillConnection;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScreenHelper {
    public static void drawCenteredOutlinedText(GuiGraphics graphics, String text, int x, int y, int color) {
        Font font = Minecraft.m_91087_().f_91062_;
        graphics.m_280056_(font, text, (x -= font.m_92895_(text) / 2) + 1, y, 0, false);
        graphics.m_280056_(font, text, x - 1, y, 0, false);
        graphics.m_280056_(font, text, x, y + 1, 0, false);
        graphics.m_280056_(font, text, x, y - 1, 0, false);
        graphics.m_280056_(font, text, x, y, color, false);
    }

    public static void drawRectangle(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.m_280509_(x, y, x + width, y + 1, color);
        graphics.m_280509_(x, y + height - 1, x + width, y + height, color);
        graphics.m_280509_(x, y + 1, x + 1, y + height - 1, color);
        graphics.m_280509_(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    public static float getAngleBetweenButtons(Button button1, Button button2) {
        float x1 = (float)button1.m_252754_() + (float)button1.m_5711_() / 2.0f;
        float y1 = (float)button1.m_252907_() + (float)button1.m_93694_() / 2.0f;
        float x2 = (float)button2.m_252754_() + (float)button2.m_5711_() / 2.0f;
        float y2 = (float)button2.m_252907_() + (float)button2.m_93694_() / 2.0f;
        return (float)Mth.m_14136_((double)(y2 - y1), (double)(x2 - x1));
    }

    public static float getDistanceBetweenButtons(Button button1, Button button2) {
        float x1 = (float)button1.m_252754_() + (float)button1.m_5711_() / 2.0f;
        float y1 = (float)button1.m_252907_() + (float)button1.m_93694_() / 2.0f;
        float x2 = (float)button2.m_252754_() + (float)button2.m_5711_() / 2.0f;
        float y2 = (float)button2.m_252907_() + (float)button2.m_93694_() / 2.0f;
        return Mth.m_14116_((float)((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
    }

    public static void renderSkillTooltip(PassiveSkillTree skillTree, SkillButton button, GuiGraphics graphics, float x, float y, int width, int height) {
        int partWidth;
        Font font = Minecraft.m_91087_().f_91062_;
        int maxWidth = width - 10;
        ArrayList<MutableComponent> tooltip = new ArrayList<MutableComponent>();
        for (MutableComponent component : button.getSkillTooltip(skillTree)) {
            if (font.m_92852_((FormattedText)component) > maxWidth) {
                tooltip.addAll(TooltipHelper.split(component, font, maxWidth));
                continue;
            }
            tooltip.add(component);
        }
        if (tooltip.isEmpty()) {
            return;
        }
        int tooltipWidth = 0;
        int tooltipHeight = tooltip.size() == 1 ? 8 : 10;
        for (MutableComponent component : tooltip) {
            int k = font.m_92852_((FormattedText)component);
            if (k > tooltipWidth) {
                tooltipWidth = k;
            }
            Objects.requireNonNull(font);
            tooltipHeight += 9 + 2;
        }
        tooltipWidth += 42;
        float tooltipX = x + 12.0f;
        float tooltipY = y - 12.0f;
        if (tooltipX + (float)tooltipWidth > (float)width) {
            tooltipX -= (float)(28 + tooltipWidth);
        }
        if (tooltipY + (float)tooltipHeight + 6.0f > (float)height) {
            tooltipY = height - tooltipHeight - 6;
        }
        if (tooltipX < 5.0f) {
            tooltipX = 5.0f;
        }
        if (tooltipY < 5.0f) {
            tooltipY = 5.0f;
        }
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_252880_(tooltipX, tooltipY, 10.0f);
        graphics.m_280509_(1, 4, tooltipWidth - 1, tooltipHeight + 4, -587202560);
        int textX = 5;
        int textY = 2;
        ResourceLocation texture = button.skill.getTooltipFrameTexture();
        graphics.m_280163_(texture, -4, -4, 0.0f, 0.0f, 21, 20, 110, 20);
        graphics.m_280163_(texture, tooltipWidth + 4 - 21, -4, -21.0f, 0.0f, 21, 20, 110, 20);
        int centerX = 17;
        for (int centerWidth = tooltipWidth + 8 - 42; centerWidth > 0; centerWidth -= partWidth) {
            partWidth = Math.min(centerWidth, 68);
            graphics.m_280163_(texture, centerX, -4, 21.0f, 0.0f, partWidth, 20, 110, 20);
            centerX += partWidth;
        }
        MutableComponent title = (MutableComponent)tooltip.remove(0);
        graphics.m_280653_(font, (Component)title, tooltipWidth / 2, textY, 0xFFFFFF);
        textY += 19;
        for (MutableComponent component : tooltip) {
            graphics.m_280430_(font, (Component)component, textX, textY, 0xFFFFFF);
            Objects.requireNonNull(font);
            textY += 9 + 2;
        }
        graphics.m_280168_().m_85849_();
    }

    public static void renderGatewayConnection(GuiGraphics graphics, SkillConnection connection, boolean highlighted, float zoom, float animation) {
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/long_connection.png");
        graphics.m_280168_().m_85836_();
        SkillButton button1 = connection.getFirstButton();
        SkillButton button2 = connection.getSecondButton();
        double connectionX = button1.x + (float)button1.m_5711_() / 2.0f;
        double connectionY = button1.y + (float)button1.m_93694_() / 2.0f;
        graphics.m_280168_().m_85837_(connectionX, connectionY, 0.0);
        float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
        graphics.m_280168_().m_252781_(Axis.f_252403_.m_252961_(rotation));
        int length = (int)(ScreenHelper.getDistanceBetweenButtons(button1, button2) / zoom);
        graphics.m_280168_().m_85841_(zoom, zoom, 1.0f);
        graphics.m_280411_(texture, 0, -8, length, 6, -animation, highlighted ? 0.0f : 6.0f, length, 6, 30, 12);
        graphics.m_280411_(texture, 0, 2, length, 6, animation, highlighted ? 0.0f : 6.0f, length, 6, -30, 12);
        graphics.m_280168_().m_85849_();
    }

    public static void renderOneWayConnection(GuiGraphics graphics, SkillConnection connection, boolean highlighted, float zoom, float animation) {
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/one_way_connection.png");
        graphics.m_280168_().m_85836_();
        SkillButton button1 = connection.getFirstButton();
        SkillButton button2 = connection.getSecondButton();
        double connectionX = button1.x + (float)button1.m_5711_() / 2.0f;
        double connectionY = button1.y + (float)button1.m_93694_() / 2.0f;
        graphics.m_280168_().m_85837_(connectionX, connectionY, 0.0);
        float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
        graphics.m_280168_().m_252781_(Axis.f_252403_.m_252961_(rotation));
        int length = (int)(ScreenHelper.getDistanceBetweenButtons(button1, button2) / zoom);
        graphics.m_280168_().m_85841_(zoom, zoom, 1.0f);
        graphics.m_280411_(texture, 0, -3, length, 6, -animation, highlighted ? 0.0f : 6.0f, length, 6, 30, 12);
        graphics.m_280168_().m_85849_();
    }

    public static void renderConnection(GuiGraphics graphics, SkillConnection connection, float zoom, float animation) {
        boolean shouldAnimate;
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/direct_connection.png");
        graphics.m_280168_().m_85836_();
        SkillButton button1 = connection.getFirstButton();
        SkillButton button2 = connection.getSecondButton();
        double connectionX = button1.x + (float)button1.m_5711_() / 2.0f;
        double connectionY = button1.y + (float)button1.m_93694_() / 2.0f;
        graphics.m_280168_().m_85837_(connectionX, connectionY, 0.0);
        float rotation = ScreenHelper.getAngleBetweenButtons(button1, button2);
        graphics.m_280168_().m_252781_(Axis.f_252403_.m_252961_(rotation));
        int length = (int)ScreenHelper.getDistanceBetweenButtons(button1, button2);
        boolean highlighted = button1.skillLearned && button2.skillLearned;
        graphics.m_280168_().m_85841_(1.0f, zoom, 1.0f);
        graphics.m_280411_(texture, 0, -3, length, 6, 0.0f, highlighted ? 0.0f : 6.0f, length, 6, 50, 12);
        boolean bl = shouldAnimate = button1.skillLearned && button2.canLearn || button2.skillLearned && button1.canLearn;
        if (!highlighted && shouldAnimate) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)((Mth.m_14031_((float)(animation / 3.0f)) + 1.0f) / 2.0f));
            graphics.m_280411_(texture, 0, -3, length, 6, 0.0f, 0.0f, length, 6, 50, 12);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        graphics.m_280168_().m_85849_();
    }
}

