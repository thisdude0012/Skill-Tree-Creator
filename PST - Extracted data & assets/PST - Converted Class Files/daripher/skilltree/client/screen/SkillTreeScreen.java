/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.Nonnull
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.achievement.StatsUpdateListener
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ServerboundClientCommandPacket
 *  net.minecraft.network.protocol.game.ServerboundClientCommandPacket$Action
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import daripher.skilltree.client.screen.SkillTreeSelectionScreen;
import daripher.skilltree.client.widget.SkillTreeWidgets;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SkillTreeScreen
extends Screen
implements StatsUpdateListener {
    public static final int BACKGROUND_SIZE = 2048;
    private final PassiveSkillTree skillTree;
    private final SkillButtons skillButtons;
    private final SkillTreeWidgets skillTreeWidgets;
    public float renderAnimation;
    private int prevMouseX;
    private int prevMouseY;
    private boolean statsUpdated;

    public SkillTreeScreen(ResourceLocation skillTreeId) {
        super((Component)Component.m_237119_());
        this.skillTree = SkillTreesReloader.getSkillTreeById(skillTreeId);
        this.f_96541_ = Minecraft.m_91087_();
        this.skillButtons = new SkillButtons(this.skillTree, () -> Float.valueOf(this.renderAnimation));
        this.skillTreeWidgets = new SkillTreeWidgets(this.getLocalPlayer(), this.skillButtons, this.skillTree);
        this.skillButtons.setRebuildFunc(this::m_232761_);
        this.skillTreeWidgets.setRebuildFunc(this::m_232761_);
    }

    public void m_7856_() {
        if (!this.statsUpdated) {
            ClientPacketListener connection = this.getMinecraft().m_91403_();
            Objects.requireNonNull(connection);
            connection.m_104955_((Packet)new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
        }
        this.m_169413_();
        this.skillTreeWidgets.clearWidgets();
        this.skillTreeWidgets.m_93674_(this.f_96543_);
        this.skillTreeWidgets.setHeight(this.f_96544_);
        this.skillButtons.m_93674_(this.f_96543_);
        this.skillButtons.setHeight(this.f_96544_);
        this.skillButtons.clearWidgets();
        this.addSkillButtons();
        this.skillTreeWidgets.init();
        this.calculateMaxScroll();
        this.m_142416_((GuiEventListener)this.skillTreeWidgets);
        this.m_142416_((GuiEventListener)this.skillButtons);
    }

    private void addSkillButtons() {
        Stream<PassiveSkill> passiveSkills = this.skillTree.getSkillIds().stream().map(SkillsReloader::getSkillById).filter(Objects::nonNull);
        passiveSkills.forEach(skill -> this.skillTreeWidgets.addSkillButton((PassiveSkill)skill, () -> Float.valueOf(this.renderAnimation)));
        this.skillButtons.updateSkillConnections();
    }

    protected void m_232761_() {
        this.getMinecraft().m_6937_(() -> super.m_232761_());
    }

    private void calculateMaxScroll() {
        this.skillButtons.setMaxScrollX(Math.min(0, this.f_96543_ / 2 - 350));
        this.skillButtons.setMaxScrollY(Math.min(0, this.f_96544_ / 2 - 350));
        this.skillButtons.getWidgets().forEach(button -> {
            float skillX = button.skill.getPositionX();
            float skillY = button.skill.getPositionY();
            int maxScrollX = (int)Math.max((float)this.skillButtons.getMaxScrollX(), Mth.m_14154_((float)skillX));
            int maxScrollY = (int)Math.max((float)this.skillButtons.getMaxScrollY(), Mth.m_14154_((float)skillY));
            this.skillButtons.setMaxScrollX(maxScrollX);
            this.skillButtons.setMaxScrollY(maxScrollY);
        });
    }

    public void m_88315_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderAnimation += partialTick;
        this.m_280273_(graphics);
        this.skillButtons.m_88315_(graphics, mouseX, mouseY, partialTick);
        this.renderOverlay(graphics);
        this.skillTreeWidgets.m_88315_(graphics, mouseX, mouseY, partialTick);
        float tooltipX = (float)mouseX + (float)(this.prevMouseX - mouseX) * partialTick;
        float tooltipY = (float)mouseY + (float)(this.prevMouseY - mouseY) * partialTick;
        this.skillButtons.renderTooltip(graphics, tooltipX, tooltipY);
        this.prevMouseX = mouseX;
        this.prevMouseY = mouseY;
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (this.skillTreeWidgets.m_6375_(mouseX, mouseY, button)) {
            return true;
        }
        return this.skillButtons.m_6375_(mouseX, mouseY, button);
    }

    public void m_86600_() {
        this.skillTreeWidgets.onWidgetTick();
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (this.skillTreeWidgets.m_7933_(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256) {
            if (SkillTreesReloader.getSkillTrees().size() == 1) {
                this.m_7379_();
            } else {
                this.getMinecraft().m_91152_((Screen)new SkillTreeSelectionScreen());
            }
            return true;
        }
        return false;
    }

    public boolean m_7920_(int keyCode, int scanCode, int modifiers) {
        return this.skillTreeWidgets.m_7933_(keyCode, scanCode, modifiers);
    }

    public boolean m_5534_(char character, int keyCode) {
        return this.skillTreeWidgets.m_5534_(character, keyCode);
    }

    private void renderOverlay(GuiGraphics graphics) {
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_tree_overlay.png");
        RenderSystem.enableBlend();
        graphics.m_280398_(texture, 0, 0, 0, 0.0f, 0.0f, this.f_96543_, this.f_96544_, this.f_96543_, this.f_96544_);
        RenderSystem.disableBlend();
    }

    public void m_280273_(GuiGraphics graphics) {
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/skill_tree_background.png");
        PoseStack poseStack = graphics.m_280168_();
        poseStack.m_85836_();
        poseStack.m_252880_(this.skillButtons.getScrollX() / 3.0f, this.skillButtons.getScrollY() / 3.0f, 0.0f);
        int size = 2048;
        graphics.m_280398_(texture, (this.f_96543_ - size) / 2, (this.f_96544_ - size) / 2, 0, 0.0f, 0.0f, size, size, size, size);
        poseStack.m_85849_();
    }

    public boolean m_7979_(double mouseX, double mouseY, int mouseButton, double dragAmountX, double dragAmountY) {
        return this.skillButtons.m_7979_(mouseX, mouseY, mouseButton, dragAmountX, dragAmountY);
    }

    public boolean m_6050_(double mouseX, double mouseY, double amount) {
        return this.skillButtons.m_6050_(mouseX, mouseY, amount);
    }

    @Nonnull
    private LocalPlayer getLocalPlayer() {
        return Objects.requireNonNull(this.getMinecraft().f_91074_);
    }

    public void updateSkillPoints(int skillPoints) {
        this.skillTreeWidgets.updateSkillPoints(skillPoints);
    }

    public void m_7819_() {
        this.statsUpdated = true;
        this.m_7856_();
    }
}

