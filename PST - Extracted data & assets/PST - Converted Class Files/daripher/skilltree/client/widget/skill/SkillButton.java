/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.math.Axis
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget.skill;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SkillButton
extends Button {
    private static final Style LESSER_TITLE_STYLE = Style.f_131099_.m_178520_(15376745);
    private static final Style NOTABLE_TITLE_STYLE = Style.f_131099_.m_178520_(10184408);
    private static final Style CLASS_TITLE_STYLE = Style.f_131099_.m_178520_(16766815);
    private static final Style KEYSTONE_TITLE_STYLE = Style.f_131099_.m_178520_(15430960);
    private static final Style GATEWAY_TITLE_STYLE = Style.f_131099_.m_178520_(8689302);
    private static final Style DESCRIPTION_STYLE = Style.f_131099_.m_178520_(8092645);
    private static final Style ID_STYLE = Style.f_131099_.m_178520_(0x545454);
    private final Supplier<Float> animationFunction;
    public final PassiveSkill skill;
    public float x;
    public float y;
    public boolean skillLearned;
    public boolean canLearn;
    public boolean searched;
    public boolean selected;

    public SkillButton(Supplier<Float> animationFunc, float x, float y, PassiveSkill skill) {
        super((int)x, (int)y, skill.getSkillSize(), skill.getSkillSize(), (Component)Component.m_237119_(), b -> {}, Supplier::get);
        this.x = x;
        this.y = y;
        this.skill = skill;
        this.animationFunction = animationFunc;
        this.f_93623_ = false;
    }

    public void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        float rb;
        RenderSystem.enableBlend();
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_252880_(this.x, this.y, 0.0f);
        this.renderFavoriteSkillHighlight(graphics);
        this.renderBackground(graphics);
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_85837_((double)this.f_93618_ / 2.0, (double)this.f_93619_ / 2.0, 0.0);
        graphics.m_280168_().m_85841_(0.5f, 0.5f, 1.0f);
        if (this.f_93618_ == 32) {
            graphics.m_280168_().m_85841_(0.75f, 0.75f, 1.0f);
        }
        graphics.m_280168_().m_85837_((double)(-this.f_93618_) / 2.0, (double)(-this.f_93619_) / 2.0, 0.0);
        this.renderIcon(graphics);
        graphics.m_280168_().m_85849_();
        float animation = (Mth.m_14031_((float)(this.animationFunction.get().floatValue() / 3.0f)) + 1.0f) / 2.0f;
        float f = rb = this.searched ? 0.1f : 1.0f;
        if (this.canLearn || this.searched) {
            graphics.m_280246_(rb, 1.0f, rb, 1.0f - animation);
        }
        if (!this.skillLearned) {
            this.renderDarkening(graphics);
        }
        if (this.canLearn || this.searched) {
            graphics.m_280246_(rb, 1.0f, rb, animation);
        }
        if (this.skillLearned || this.canLearn || this.searched) {
            this.renderFrame(graphics);
        }
        if (this.canLearn || this.searched || this.selected) {
            graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
        }
        graphics.m_280168_().m_85849_();
        RenderSystem.disableBlend();
    }

    private void renderFavoriteSkillHighlight(GuiGraphics graphics) {
        if (!ClientConfig.favorite_skills.contains(this.skill.getId())) {
            return;
        }
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/favorite_skill.png");
        int color = ClientConfig.favorite_color_is_rainbow ? Color.getHSBColor(this.animationFunction.get().floatValue() / 240.0f, 1.0f, 1.0f).getRGB() : ClientConfig.favorite_color;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        graphics.m_280246_(r, g, b, 1.0f);
        int size = (int)((double)this.f_93618_ * 1.4);
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_252880_((float)this.f_93618_ / 2.0f, (float)this.f_93619_ / 2.0f, 0.0f);
        float animation = 1.0f + 0.3f * (Mth.m_14031_((float)(this.animationFunction.get().floatValue() / 3.0f)) + 1.0f) / 2.0f;
        graphics.m_280168_().m_85841_(animation, animation, 1.0f);
        graphics.m_280168_().m_252781_(Axis.f_252403_.m_252977_(this.animationFunction.get().floatValue()));
        graphics.m_280168_().m_252880_((float)(-size) / 2.0f, (float)(-size) / 2.0f, 0.0f);
        graphics.m_280411_(texture, 0, 0, size, size, 0.0f, 0.0f, 80, 80, 80, 80);
        graphics.m_280168_().m_85849_();
        graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderFrame(GuiGraphics graphics) {
        ResourceLocation texture = this.skill.getFrameTexture();
        graphics.m_280411_(texture, 0, 0, this.f_93618_, this.f_93619_, (float)(this.f_93618_ * 2), 0.0f, this.f_93618_, this.f_93619_, this.f_93618_ * 3, this.f_93619_);
    }

    private void renderDarkening(GuiGraphics graphics) {
        ResourceLocation texture = this.skill.getFrameTexture();
        graphics.m_280411_(texture, 0, 0, this.f_93618_, this.f_93619_, (float)this.f_93618_, 0.0f, this.f_93618_, this.f_93619_, this.f_93618_ * 3, this.f_93619_);
    }

    private void renderIcon(GuiGraphics graphics) {
        ResourceLocation texture = this.skill.getIconTexture();
        graphics.m_280411_(texture, 0, 0, this.f_93618_, this.f_93619_, 0.0f, 0.0f, this.f_93618_, this.f_93619_, this.f_93618_, this.f_93619_);
    }

    private void renderBackground(GuiGraphics graphics) {
        ResourceLocation texture = this.skill.getFrameTexture();
        graphics.m_280411_(texture, 0, 0, this.f_93618_, this.f_93619_, 0.0f, 0.0f, this.f_93618_, this.f_93619_, this.f_93618_ * 3, this.f_93619_);
    }

    public void setButtonSize(int size) {
        this.f_93618_ = this.f_93619_ = size;
    }

    public List<MutableComponent> getSkillTooltip(PassiveSkillTree skillTree) {
        ArrayList<MutableComponent> tooltip = new ArrayList<MutableComponent>();
        this.addTitleTooltip(tooltip);
        this.addLimitationsTooltip(skillTree, tooltip);
        List<MutableComponent> description = this.skill.getDescription();
        if (description != null) {
            tooltip.addAll(description);
        } else {
            this.addSkillBonusTooltip(tooltip);
        }
        this.addRequirementsTooltip(tooltip);
        this.addAdvancedTooltip(tooltip);
        return tooltip;
    }

    public void addRequirementsTooltip(ArrayList<MutableComponent> tooltip) {
        if (this.skill.getRequirements().isEmpty()) {
            return;
        }
        if (tooltip.size() > 1) {
            tooltip.add(Component.m_237119_());
        }
        tooltip.add(Component.m_237113_((String)"Requirements:").m_130948_(TooltipHelper.getSkillBonusStyle(true)));
        this.skill.getRequirements().forEach(requirement -> {
            MutableComponent requirementTooltip = requirement.getTooltip();
            LocalPlayer localPlayer = Minecraft.m_91087_().f_91074_;
            Style style = TooltipHelper.getSkillRequirementStyle(requirement.isRequirementMet((Player)localPlayer));
            requirementTooltip = requirementTooltip.m_130948_(style);
            tooltip.add(Component.m_237113_((String)"  ").m_7220_((Component)requirementTooltip));
        });
    }

    public void addSkillBonusTooltip(List<MutableComponent> tooltip) {
        this.addDescriptionTooltip(tooltip);
        this.addInfoTooltip(tooltip);
    }

    private void addInfoTooltip(List<MutableComponent> tooltip) {
        if (!Screen.m_96639_()) {
            return;
        }
        ArrayList info = new ArrayList();
        for (SkillBonus<?> skillBonus : this.skill.getBonuses()) {
            skillBonus.gatherInfo(component -> {
                component = component.m_130944_(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY});
                info.add(component);
            });
        }
        if (!info.isEmpty()) {
            tooltip.add(Component.m_237119_());
            tooltip.addAll(info);
        }
    }

    protected void addAdvancedTooltip(List<MutableComponent> tooltip) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (!minecraft.f_91066_.f_92125_) {
            return;
        }
        this.addIdTooltip(tooltip);
    }

    protected void addDescriptionTooltip(List<MutableComponent> tooltip) {
        this.skill.getBonuses().stream().map(SkillBonus::getTooltip).forEach(tooltip::add);
        String descriptionId = this.getSkillId() + ".description";
        String description = Component.m_237115_((String)descriptionId).getString();
        if (!description.equals(descriptionId)) {
            List<String> descriptionStrings = Arrays.asList(description.split("/n"));
            descriptionStrings.stream().map(Component::m_237115_).map(this::applyDescriptionStyle).forEach(tooltip::add);
        }
    }

    private void addLimitationsTooltip(PassiveSkillTree skillTree, ArrayList<MutableComponent> tooltips) {
        boolean addedLimitTooltip = false;
        for (String tag : this.skill.getTags()) {
            int limit = skillTree.getSkillLimitations().getOrDefault(tag, 0);
            if (limit <= 0) continue;
            addedLimitTooltip = true;
            AtomicReference<MutableComponent> tagTooltip = new AtomicReference<MutableComponent>(Component.m_237113_((String)tag));
            TooltipHelper.consumeTranslated("skill.tag.%s.name".formatted(new Object[]{tag}), tagTooltip::set);
            tagTooltip.set(Component.m_237113_((String)(limit + " " + tagTooltip.get().getString())));
            tagTooltip.set(tagTooltip.get().m_130948_(TooltipHelper.getItemBonusStyle(true)));
            MutableComponent tooltip = Component.m_237110_((String)"skill.limitation", (Object[])new Object[]{tagTooltip.get()});
            tooltip = tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(true));
            tooltips.add(tooltip);
        }
        if (addedLimitTooltip) {
            tooltips.add(Component.m_237119_());
        }
    }

    protected void addTitleTooltip(List<MutableComponent> tooltip) {
        MutableComponent title = this.skill.getTitle().isEmpty() ? Component.m_237115_((String)(this.getSkillId() + ".name")) : Component.m_237113_((String)this.skill.getTitle());
        tooltip.add(title.m_130948_(this.getTitleStyle()));
    }

    private Style getTitleStyle() {
        String titleColor = this.skill.getTitleColor();
        if (titleColor.isEmpty()) {
            return this.f_93618_ == 30 ? GATEWAY_TITLE_STYLE : (this.f_93618_ == 24 ? CLASS_TITLE_STYLE : (this.f_93618_ == 20 ? NOTABLE_TITLE_STYLE : (this.f_93618_ == 32 ? KEYSTONE_TITLE_STYLE : LESSER_TITLE_STYLE)));
        }
        try {
            return Style.f_131099_.m_178520_(Integer.parseInt(titleColor, 16));
        }
        catch (NumberFormatException e) {
            return Style.f_131099_;
        }
    }

    protected void addIdTooltip(List<MutableComponent> tooltip) {
        MutableComponent idComponent = Component.m_237113_((String)this.skill.getId().toString()).m_130948_(ID_STYLE);
        tooltip.add(idComponent);
    }

    protected MutableComponent applyDescriptionStyle(MutableComponent component) {
        return component.m_130948_(DESCRIPTION_STYLE);
    }

    public void setCanLearn() {
        this.canLearn = true;
    }

    public void setActive() {
        this.f_93623_ = true;
    }

    private String getSkillId() {
        return "skill." + this.skill.getId().m_135827_() + "." + this.skill.getId().m_135815_();
    }
}

