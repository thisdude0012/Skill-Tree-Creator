/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.core.Holder
 *  net.minecraft.core.NonNullList
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.widget;

import com.google.common.collect.Streams;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.client.widget.Button;
import daripher.skilltree.client.widget.Label;
import daripher.skilltree.client.widget.ProgressBar;
import daripher.skilltree.client.widget.ScrollableComponentList;
import daripher.skilltree.client.widget.TextField;
import daripher.skilltree.client.widget.group.WidgetGroup;
import daripher.skilltree.client.widget.skill.SkillButton;
import daripher.skilltree.client.widget.skill.SkillButtons;
import daripher.skilltree.client.widget.skill.SkillConnection;
import daripher.skilltree.config.ClientConfig;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SkillTreeWidgets
extends WidgetGroup<AbstractWidget> {
    private final SkillButtons skills;
    private final PassiveSkillTree skillTree;
    private final List<ResourceLocation> learnedSkills = new ArrayList<ResourceLocation>();
    public final List<ResourceLocation> newlyLearnedSkills = new ArrayList<ResourceLocation>();
    private final List<SkillButton> startingPoints = new ArrayList<SkillButton>();
    private Button buyButton;
    private Label pointsInfo;
    private ProgressBar progressBar;
    private ScrollableComponentList statsInfo;
    public int skillPoints;
    private boolean showStats;
    private boolean showProgressInNumbers;
    private String search = "";
    private final LocalPlayer player;

    public SkillTreeWidgets(LocalPlayer player, SkillButtons skills, PassiveSkillTree skillTree) {
        super(0, 0, 0, 0);
        this.skills = skills;
        this.skillTree = skillTree;
        this.player = player;
        this.readPlayerData(player);
    }

    public void init() {
        this.progressBar = new ProgressBar(this.f_93618_ / 2 - 117, this.f_93619_ - 17, b -> this.toggleProgressDisplayMode());
        this.progressBar.showProgressInNumbers = this.showProgressInNumbers;
        this.addWidget(this.progressBar);
        this.addTopWidgets();
        if (!ServerConfig.enable_exp_exchange) {
            this.progressBar.f_93624_ = false;
            this.buyButton.f_93624_ = false;
        }
        this.statsInfo = new ScrollableComponentList(48, this.f_93619_ - 60);
        this.statsInfo.setComponents(this.getMergedSkillBonusesTooltips());
        this.addWidget(this.statsInfo);
        this.startingPoints.clear();
        this.skills.getWidgets().stream().filter(button -> button.skill.isStartingPoint()).forEach(this.startingPoints::add);
        this.highlightSkills();
        this.updateSearch();
    }

    @Override
    protected void m_87963_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.updateBuyPointButton();
        Style pointsStyle = Style.f_131099_.m_178520_(16573030);
        MutableComponent pointsLeft = Component.m_237113_((String)("" + this.skillPoints)).m_130948_(pointsStyle);
        this.pointsInfo.m_93666_((Component)Component.m_237110_((String)"widget.skill_points_left", (Object[])new Object[]{pointsLeft}));
        this.statsInfo.m_252865_(this.f_93618_ - this.statsInfo.m_5711_() - 10);
        this.statsInfo.f_93624_ = this.showStats;
        super.m_87963_(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean m_6375_(double mouseX, double mouseY, int button) {
        Object widget = this.getWidgetAt(mouseX, mouseY);
        if (widget != null) {
            widget.m_93692_(true);
            return widget.m_6375_(mouseX, mouseY, button);
        }
        SkillButton skill = (SkillButton)((Object)this.skills.getWidgetAt(mouseX, mouseY));
        if (skill == null) {
            return false;
        }
        if (button == 0) {
            this.playButtonSound();
            this.skillButtonPressed(skill);
            return true;
        }
        if (button == 1) {
            ClientConfig.toggleFavoriteSkill(skill.skill);
            this.playButtonSound();
            return true;
        }
        return false;
    }

    private void updateSearch() {
        if (this.search.isEmpty()) {
            for (SkillButton button : this.skills.getWidgets()) {
                button.searched = false;
            }
            return;
        }
        block1: for (SkillButton button : this.skills.getWidgets()) {
            for (MutableComponent component : button.getSkillTooltip(this.skillTree)) {
                if (!component.getString().toLowerCase().contains(this.search.toLowerCase())) continue;
                button.searched = true;
                continue block1;
            }
            button.searched = false;
        }
    }

    private void playButtonSound() {
        SoundManager soundManager = Minecraft.m_91087_().m_91106_();
        SimpleSoundInstance sound = SimpleSoundInstance.m_263171_((Holder)SoundEvents.f_12490_, (float)1.0f);
        soundManager.m_120367_((SoundInstance)sound);
    }

    private void highlightSkills() {
        if (this.skillPoints == 0) {
            return;
        }
        if (this.getLearnedSkillsOnTree().isEmpty() && this.newlyLearnedSkills.isEmpty()) {
            this.startingPoints.stream().filter(button -> this.canLearnSkill(button.skill)).forEach(SkillButton::setCanLearn);
            return;
        }
        if (this.learnedSkills.size() + this.newlyLearnedSkills.size() >= ServerConfig.max_skill_points) {
            return;
        }
        this.skills.getSkillConnections().forEach(connection -> {
            SkillButton button1 = connection.getFirstButton();
            SkillButton button2 = connection.getSecondButton();
            if (button1.skillLearned == button2.skillLearned) {
                return;
            }
            if (connection.getType() != SkillConnection.Type.ONE_WAY && !button1.skillLearned && this.canLearnSkill(button1.skill)) {
                button1.setCanLearn();
                button1.setActive();
            }
            if (!button2.skillLearned && this.canLearnSkill(button2.skill)) {
                button2.setCanLearn();
                button2.setActive();
            }
        });
    }

    private List<ResourceLocation> getLearnedSkillsOnTree() {
        return this.learnedSkills.stream().filter(this.skillTree.getSkillIds()::contains).toList();
    }

    private void addTopWidgets() {
        MutableComponent buyButtonText = Component.m_237115_((String)"widget.buy_skill_button");
        MutableComponent pointsInfoText = Component.m_237110_((String)"widget.skill_points_left", (Object[])new Object[]{100});
        MutableComponent confirmButtonText = Component.m_237115_((String)"widget.confirm_button");
        MutableComponent cancelButtonText = Component.m_237115_((String)"widget.cancel_button");
        MutableComponent showStatsButtonText = Component.m_237115_((String)"widget.show_stats");
        Font font = Minecraft.m_91087_().f_91062_;
        int buttonWidth = Math.max(font.m_92852_((FormattedText)buyButtonText), font.m_92852_((FormattedText)pointsInfoText));
        buttonWidth = Math.max(buttonWidth, font.m_92852_((FormattedText)confirmButtonText));
        buttonWidth = Math.max(buttonWidth, font.m_92852_((FormattedText)cancelButtonText));
        int buttonsY = 8;
        Button showStatsButton = new Button(this.f_93618_ - (buttonWidth += 20) - 8, buttonsY, buttonWidth, 14, (Component)showStatsButtonText);
        showStatsButton.setPressFunc(b -> this.showStats ^= true);
        this.addWidget(showStatsButton);
        TextField searchField = new TextField(8, buttonsY, buttonWidth, 14, this.search);
        this.addWidget(searchField).setHint("Search...").m_94151_(s -> {
            this.search = s;
            this.updateSearch();
        });
        this.buyButton = new Button(this.f_93618_ / 2 - 8 - buttonWidth, buttonsY, buttonWidth, 14, (Component)buyButtonText);
        this.buyButton.setPressFunc(b -> this.buySkillPoint());
        this.addWidget(this.buyButton);
        this.pointsInfo = new Label(this.f_93618_ / 2 + 8, buttonsY, buttonWidth, 14, (Component)Component.m_237119_());
        if (!ServerConfig.enable_exp_exchange) {
            this.pointsInfo.m_252865_(this.f_93618_ / 2 - buttonWidth / 2);
        }
        this.addWidget(this.pointsInfo);
        Button confirmButton = new Button(this.f_93618_ / 2 - 8 - buttonWidth, buttonsY += 20, buttonWidth, 14, (Component)confirmButtonText);
        confirmButton.setPressFunc(b -> this.confirmLearnSkills());
        this.addWidget(confirmButton);
        Button cancelButton = new Button(this.f_93618_ / 2 + 8, buttonsY, buttonWidth, 14, (Component)cancelButtonText);
        cancelButton.setPressFunc(b -> this.cancelLearnSkills());
        this.addWidget(cancelButton);
        cancelButton.f_93623_ = !this.newlyLearnedSkills.isEmpty();
        confirmButton.f_93623_ = cancelButton.f_93623_;
    }

    private static void addToMergeList(SkillBonus<?> b, List<SkillBonus<?>> bonuses) {
        Optional<SkillBonus> same = bonuses.stream().filter(b::canMerge).findAny();
        if (same.isPresent()) {
            bonuses.remove(same.get());
            bonuses.add(same.get().copy().merge(b));
        } else {
            bonuses.add(b);
        }
    }

    private boolean canLearnSkill(PassiveSkill skill) {
        if (!this.player.m_7500_()) {
            for (SkillRequirement<?> requirement : skill.getRequirements()) {
                if (requirement.isRequirementMet((Player)this.player)) continue;
                return false;
            }
        }
        Map<String, Integer> limitations = this.skillTree.getSkillLimitations();
        for (String tag : skill.getTags()) {
            int limit = limitations.getOrDefault(tag, 0);
            if (limit <= 0 || this.getLearnedSkillsWithTag(tag) < (long)limit) continue;
            return false;
        }
        return true;
    }

    private long getLearnedSkillsWithTag(String tag) {
        return Streams.concat((Stream[])new Stream[]{this.learnedSkills.stream(), this.newlyLearnedSkills.stream()}).map(SkillsReloader::getSkillById).filter(Objects::nonNull).filter(skill -> skill.getTags().contains(tag)).count();
    }

    private void confirmLearnSkills() {
        this.newlyLearnedSkills.forEach(id -> this.learnSkill(this.skills.getWidgetById((ResourceLocation)id).skill));
        this.newlyLearnedSkills.clear();
    }

    private void cancelLearnSkills() {
        this.skillPoints += this.newlyLearnedSkills.size();
        this.newlyLearnedSkills.clear();
        this.rebuildWidgets();
    }

    private void buySkillPoint() {
        int currentLevel = this.getCurrentLevel();
        if (!this.canBuySkillPoint(currentLevel)) {
            return;
        }
        int cost = ServerConfig.getSkillPointCost(currentLevel);
        NetworkDispatcher.network_channel.sendToServer((Object)new GainSkillPointMessage());
        this.player.m_6756_(-cost);
    }

    private boolean canBuySkillPoint(int currentLevel) {
        if (!ServerConfig.enable_exp_exchange) {
            return false;
        }
        if (this.isMaxLevel(currentLevel)) {
            return false;
        }
        int cost = ServerConfig.getSkillPointCost(currentLevel);
        return this.player.f_36079_ >= cost;
    }

    private boolean isMaxLevel(int currentLevel) {
        return currentLevel >= ServerConfig.max_skill_points;
    }

    private int getCurrentLevel() {
        IPlayerSkills capability = PlayerSkillsProvider.get((Player)this.player);
        int learnedSkills = capability.getPlayerSkills().size();
        int skillPoints = capability.getSkillPoints();
        return learnedSkills + skillPoints;
    }

    protected void skillButtonPressed(SkillButton button) {
        int lastLearned;
        PassiveSkill skill = button.skill;
        if (!this.newlyLearnedSkills.isEmpty() && this.newlyLearnedSkills.get(lastLearned = this.newlyLearnedSkills.size() - 1).equals((Object)skill.getId())) {
            ++this.skillPoints;
            this.newlyLearnedSkills.remove(lastLearned);
            this.rebuildWidgets();
            return;
        }
        if (button.canLearn) {
            --this.skillPoints;
            this.newlyLearnedSkills.add(skill.getId());
            this.rebuildWidgets();
            return;
        }
        ResourceLocation connectedTree = skill.getConnectedTreeId();
        if (connectedTree != null) {
            Minecraft.m_91087_().m_91152_((Screen)new SkillTreeScreen(connectedTree));
        }
    }

    protected void learnSkill(PassiveSkill skill) {
        this.learnedSkills.add(skill.getId());
        NetworkDispatcher.network_channel.sendToServer((Object)new LearnSkillMessage(skill));
        this.rebuildWidgets();
    }

    protected void updateBuyPointButton() {
        int currentLevel = this.getCurrentLevel();
        this.buyButton.f_93623_ = false;
        if (this.isMaxLevel(currentLevel)) {
            return;
        }
        int pointCost = ServerConfig.getSkillPointCost(currentLevel);
        this.buyButton.f_93623_ = this.player.f_36079_ >= pointCost;
    }

    private void toggleProgressDisplayMode() {
        this.progressBar.showProgressInNumbers ^= true;
        this.showProgressInNumbers ^= true;
    }

    private void readPlayerData(LocalPlayer player) {
        IPlayerSkills capability = PlayerSkillsProvider.get((Player)player);
        NonNullList<PassiveSkill> skills = capability.getPlayerSkills();
        skills.stream().map(PassiveSkill::getId).forEach(this.learnedSkills::add);
        this.skillPoints = capability.getSkillPoints();
    }

    private List<Component> getMergedSkillBonusesTooltips() {
        ArrayList bonuses = new ArrayList();
        this.learnedSkills.stream().map(this.skills::getWidgetById).filter(Objects::nonNull).map(button -> button.skill).map(PassiveSkill::getBonuses).flatMap(Collection::stream).forEach(b -> SkillTreeWidgets.addToMergeList(b, bonuses));
        return bonuses.stream().sorted().map(SkillBonus::getTooltip).map(Component.class::cast).toList();
    }

    public void updateSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints - this.newlyLearnedSkills.size();
    }

    public void addSkillButton(PassiveSkill skill, Supplier<Float> renderAnimation) {
        SkillButton button = this.skills.addSkillButton(skill, renderAnimation);
        if (this.learnedSkills.contains(skill.getId()) || this.newlyLearnedSkills.contains(skill.getId())) {
            button.skillLearned = true;
        }
    }
}

