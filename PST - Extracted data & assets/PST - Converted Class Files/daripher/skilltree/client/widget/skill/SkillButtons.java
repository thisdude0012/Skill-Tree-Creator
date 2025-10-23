/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget.skill;

import daripher.skilltree.client.screen.ScreenHelper;
import daripher.skilltree.client.widget.group.ScrollableZoomableWidgetGroup;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillConnection;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SkillButtons
extends ScrollableZoomableWidgetGroup<SkillButton> {
    private final PassiveSkillTree skillTree;
    private final List<SkillConnection> skillConnections = new ArrayList<SkillConnection>();
    private final Map<ResourceLocation, SkillButton> idToWidget = new HashMap<ResourceLocation, SkillButton>();
    private final Supplier<Float> animationFunc;

    public SkillButtons(PassiveSkillTree skillTree, Supplier<Float> animationFunc) {
        super(0, 0, 0, 0);
        this.skillTree = skillTree;
        this.animationFunc = animationFunc;
    }

    @Override
    @NotNull
    public <W extends SkillButton> W addWidget(@NotNull W widget) {
        this.idToWidget.put(widget.skill.getId(), widget);
        return super.addWidget(widget);
    }

    @Override
    public void clearWidgets() {
        this.idToWidget.clear();
        super.clearWidgets();
    }

    @Override
    protected void renderBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderConnections(graphics, mouseX, mouseY);
    }

    protected void renderConnections(GuiGraphics graphics, int mouseX, int mouseY) {
        this.skillConnections.stream().filter(c -> c.getType() == SkillConnection.Type.DIRECT).forEach(c -> this.renderDirectConnection(graphics, (SkillConnection)c));
        this.skillConnections.stream().filter(c -> c.getType() == SkillConnection.Type.LONG).forEach(c -> this.renderLongConnection(graphics, (SkillConnection)c, mouseX, mouseY));
        this.skillConnections.stream().filter(c -> c.getType() == SkillConnection.Type.ONE_WAY).forEach(c -> this.renderOneWayConnection(graphics, (SkillConnection)c));
    }

    private void renderDirectConnection(GuiGraphics graphics, SkillConnection connection) {
        ScreenHelper.renderConnection(graphics, connection, this.getZoom(), this.animationFunc.get().floatValue());
    }

    private void renderLongConnection(GuiGraphics graphics, SkillConnection connection, int mouseX, int mouseY) {
        SkillButton hoveredSkill = (SkillButton)((Object)this.getWidgetAt(mouseX, mouseY));
        if (hoveredSkill != connection.getFirstButton() && hoveredSkill != connection.getSecondButton()) {
            return;
        }
        ScreenHelper.renderGatewayConnection(graphics, connection, true, this.getZoom(), this.animationFunc.get().floatValue());
    }

    private void renderOneWayConnection(GuiGraphics graphics, SkillConnection connection) {
        ScreenHelper.renderOneWayConnection(graphics, connection, true, this.getZoom(), this.animationFunc.get().floatValue());
    }

    public void renderTooltip(GuiGraphics graphics, float tooltipX, float tooltipY) {
        SkillButton skill = (SkillButton)((Object)this.getWidgetAt(tooltipX, tooltipY));
        if (skill == null) {
            return;
        }
        ScreenHelper.renderSkillTooltip(this.skillTree, skill, graphics, tooltipX, tooltipY, this.f_93618_, this.f_93619_);
    }

    public PassiveSkillTree getSkillTree() {
        return this.skillTree;
    }

    public SkillButton addSkillButton(PassiveSkill skill, Supplier<Float> animationFunc) {
        float skillX = skill.getPositionX();
        float skillY = skill.getPositionY();
        int skillSize = skill.getSkillSize();
        float buttonX = skillX - (float)skillSize / 2.0f + (float)this.f_93618_ / 2.0f + skillX * (this.getZoom() - 1.0f);
        float buttonY = skillY - (float)skillSize / 2.0f + (float)this.f_93619_ / 2.0f + skillY * (this.getZoom() - 1.0f);
        SkillButton button = new SkillButton(animationFunc, buttonX, buttonY, skill);
        return this.addWidget(button);
    }

    public void updateSkillConnections() {
        this.skillConnections.clear();
        this.getWidgets().forEach(this::addSkillConnections);
    }

    private void addSkillConnections(SkillButton skillButton) {
        PassiveSkill skill = skillButton.skill;
        this.readSkillConnections(skill, SkillConnection.Type.DIRECT, skill.getDirectConnections());
        this.readSkillConnections(skill, SkillConnection.Type.LONG, skill.getLongConnections());
        this.readSkillConnections(skill, SkillConnection.Type.ONE_WAY, skill.getOneWayConnections());
    }

    private void readSkillConnections(PassiveSkill skill, SkillConnection.Type type, List<ResourceLocation> connections) {
        for (ResourceLocation connectedSkillId : new ArrayList<ResourceLocation>(connections)) {
            if (this.idToWidget.get(connectedSkillId) == null) {
                connections.remove(connectedSkillId);
                continue;
            }
            this.connectSkills(type, skill.getId(), connectedSkillId);
        }
    }

    protected void connectSkills(SkillConnection.Type type, ResourceLocation skillId1, ResourceLocation skillId2) {
        SkillButton button1 = this.idToWidget.get(skillId1);
        SkillButton button2 = this.idToWidget.get(skillId2);
        this.skillConnections.add(new SkillConnection(type, button1, button2));
    }

    public SkillButton getWidgetById(ResourceLocation id) {
        return this.idToWidget.get(id);
    }

    public List<SkillConnection> getSkillConnections() {
        return this.skillConnections;
    }
}

