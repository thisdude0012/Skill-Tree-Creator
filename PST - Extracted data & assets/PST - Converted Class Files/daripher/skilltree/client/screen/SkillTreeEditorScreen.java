/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.achievement.StatsUpdateListener
 *  net.minecraft.client.multiplayer.ClientPacketListener
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
import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.client.widget.editor.menu.SkillNodeEditor;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SkillTreeEditorScreen
extends Screen
implements StatsUpdateListener {
    private final PassiveSkillTree skillTree;
    private final SkillButtons skillButtons;
    private final SkillTreeEditor editorWidgets;
    private boolean shouldCloseOnEsc = true;
    private int prevMouseX;
    private int prevMouseY;
    private boolean statsUpdated;

    public SkillTreeEditorScreen(ResourceLocation skillTreeId) {
        super((Component)Component.m_237119_());
        this.f_96541_ = Minecraft.m_91087_();
        this.skillTree = SkillTreeClientData.getOrCreateEditorTree(skillTreeId);
        this.skillButtons = new SkillButtons(this.skillTree, () -> Float.valueOf(0.0f));
        this.editorWidgets = new SkillTreeEditor(this.skillButtons);
    }

    public void m_7856_() {
        if (!this.statsUpdated) {
            ClientPacketListener connection = this.getMinecraft().m_91403_();
            Objects.requireNonNull(connection);
            connection.m_104955_((Packet)new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
        }
        if (this.skillTree == null) {
            this.getMinecraft().m_91152_(null);
            return;
        }
        this.m_169413_();
        this.skillButtons.m_93674_(this.f_96543_);
        this.skillButtons.setHeight(this.f_96544_);
        this.editorWidgets.m_93674_(210);
        this.editorWidgets.setHeight(10);
        this.editorWidgets.m_252865_(this.f_96543_ - this.editorWidgets.m_5711_());
        this.editorWidgets.init();
        this.editorWidgets.increaseHeight(5);
        this.editorWidgets.setRebuildFunc(this::m_232761_);
        this.skillButtons.setRebuildFunc(this::m_232761_);
        this.skillButtons.clearWidgets();
        this.editorWidgets.getSkills().forEach(this.editorWidgets::addSkillButton);
        this.skillButtons.updateSkillConnections();
        this.calculateMaxScroll();
        this.m_142416_((GuiEventListener)this.skillButtons);
        this.m_142416_((GuiEventListener)this.editorWidgets);
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
        this.m_280273_(graphics);
        this.skillButtons.m_88315_(graphics, mouseX, mouseY, partialTick);
        this.renderOverlay(graphics);
        this.editorWidgets.m_88315_(graphics, mouseX, mouseY, partialTick);
        if (mouseX < this.editorWidgets.m_252754_() || mouseY > this.editorWidgets.m_93694_()) {
            float tooltipX = (float)mouseX + (float)(this.prevMouseX - mouseX) * partialTick;
            float tooltipY = (float)mouseY + (float)(this.prevMouseY - mouseY) * partialTick;
            this.skillButtons.renderTooltip(graphics, tooltipX, tooltipY);
        }
        this.prevMouseX = mouseX;
        this.prevMouseY = mouseY;
    }

    private void createBlankSkill() {
        ResourceLocation background = new ResourceLocation("skilltree", "textures/icons/background/lesser.png");
        ResourceLocation icon = new ResourceLocation("skilltree", "textures/icons/void.png");
        ResourceLocation border = new ResourceLocation("skilltree", "textures/tooltip/lesser.png");
        ResourceLocation skillId = SkillNodeEditor.createNewSkillId(this.skillTree.getId());
        PassiveSkill skill = new PassiveSkill(skillId, 16, background, icon, border, false);
        skill.setPosition(0.0f, 0.0f);
        SkillTreeClientData.saveEditorSkill(skill);
        SkillTreeClientData.loadEditorSkill(skill.getId());
        this.editorWidgets.getSkillTree().getSkillIds().add(skill.getId());
        SkillTreeClientData.saveEditorSkillTree(this.editorWidgets.getSkillTree());
    }

    public boolean m_6913_() {
        if (!this.shouldCloseOnEsc) {
            this.shouldCloseOnEsc = true;
            return false;
        }
        return super.m_6913_();
    }

    public void m_86600_() {
        this.editorWidgets.onWidgetTick();
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

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        return this.editorWidgets.m_6375_(mouseX, mouseY, button);
    }

    public boolean m_6348_(double mouseX, double mouseY, int button) {
        return this.editorWidgets.m_6348_(mouseX, mouseY, button);
    }

    public boolean m_6050_(double mouseX, double mouseY, double amount) {
        return this.editorWidgets.m_6050_(mouseX, mouseY, amount) || this.skillButtons.m_6050_(mouseX, mouseY, amount);
    }

    public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.editorWidgets.m_7979_(mouseX, mouseY, button, dragX, dragY) | this.skillButtons.m_7979_(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (this.editorWidgets.m_7933_(keyCode, scanCode, modifiers)) {
            if (keyCode == 256) {
                this.shouldCloseOnEsc = false;
            }
            return true;
        }
        if (keyCode == 256 && this.m_6913_()) {
            this.m_7379_();
            return true;
        }
        if (keyCode == 78 && Screen.m_96637_()) {
            this.createBlankSkill();
            this.m_232761_();
            return true;
        }
        return false;
    }

    public boolean m_5534_(char codePoint, int modifiers) {
        return this.editorWidgets.m_5534_(codePoint, modifiers);
    }

    public void m_7819_() {
        this.statsUpdated = true;
        this.m_7856_();
    }
}

