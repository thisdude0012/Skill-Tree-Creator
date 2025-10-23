/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.config.ServerConfig;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ProgressBar
extends Button {
    public boolean showProgressInNumbers;

    public ProgressBar(int x, int y, Button.OnPress pressFunc) {
        super(x, y, 235, 19, (Component)Component.m_237119_(), pressFunc, Supplier::get);
    }

    private static int getCurrentLevel() {
        IPlayerSkills capability = PlayerSkillsProvider.get((Player)ProgressBar.getLocalPlayer());
        int skills = capability.getPlayerSkills().size();
        int points = capability.getSkillPoints();
        return skills + points;
    }

    private static boolean isMaxLevel(int currentLevel) {
        return currentLevel >= ServerConfig.max_skill_points;
    }

    public void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        this.renderCurrentLevel(graphics);
        this.renderNextLevel(graphics);
        this.renderProgress(graphics);
    }

    protected void renderBackground(GuiGraphics graphics) {
        float experienceProgress = this.getExperienceProgress();
        int filledBarWidth = (int)(experienceProgress * 183.0f);
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/progress_bars.png");
        graphics.m_280218_(texture, this.m_252754_() + 26, this.m_252907_() + 7, 0, 0, 182, 5);
        if (filledBarWidth == 0) {
            return;
        }
        graphics.m_280218_(texture, this.m_252754_() + 26, this.m_252907_() + 7, 0, 5, filledBarWidth, 5);
    }

    protected void renderProgress(GuiGraphics graphics) {
        if (this.showProgressInNumbers) {
            int cost = ServerConfig.getSkillPointCost(ProgressBar.getCurrentLevel());
            int exp = ProgressBar.getLocalPlayer().f_36079_;
            String text = exp + "/" + cost;
            ScreenHelper.drawCenteredOutlinedText(graphics, text, this.m_252754_() + this.f_93618_ / 2, this.getTextY(), 16573030);
        } else {
            float experienceProgress = this.getExperienceProgress();
            String text = (int)(experienceProgress * 100.0f) + "%";
            ScreenHelper.drawCenteredOutlinedText(graphics, text, this.m_252754_() + this.f_93618_ / 2, this.getTextY(), 16573030);
        }
    }

    protected void renderNextLevel(GuiGraphics graphics) {
        int currentLevel = ProgressBar.getCurrentLevel();
        if (ProgressBar.isMaxLevel(currentLevel)) {
            --currentLevel;
        }
        int nextLevel = currentLevel + 1;
        ScreenHelper.drawCenteredOutlinedText(graphics, "" + nextLevel, this.m_252754_() + this.f_93618_ - 17, this.getTextY(), 16573030);
    }

    protected void renderCurrentLevel(GuiGraphics graphics) {
        int currentLevel = ProgressBar.getCurrentLevel();
        if (ProgressBar.isMaxLevel(currentLevel)) {
            --currentLevel;
        }
        ScreenHelper.drawCenteredOutlinedText(graphics, "" + currentLevel, this.m_252754_() + 17, this.getTextY(), 16573030);
    }

    protected int getTextY() {
        return this.m_252907_() + 5;
    }

    private float getExperienceProgress() {
        int level = ProgressBar.getCurrentLevel();
        float progress = 1.0f;
        if (level < ServerConfig.max_skill_points) {
            int levelupCost = ServerConfig.getSkillPointCost(level);
            progress = (float)ProgressBar.getLocalPlayer().f_36079_ / (float)levelupCost;
            progress = Math.min(1.0f, progress);
        }
        return progress;
    }

    private static LocalPlayer getLocalPlayer() {
        return Minecraft.m_91087_().f_91074_;
    }
}

