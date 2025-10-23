/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package daripher.skilltree.capability.skill;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSkills
implements IPlayerSkills {
    private static final UUID TREE_VERSION = UUID.fromString("fd21c2a9-7ab5-4a1e-b06d-ddb87b56047f");
    private final NonNullList<PassiveSkill> skills = NonNullList.m_122779_();
    private int skillPoints;
    private boolean treeReset;

    @Override
    public NonNullList<PassiveSkill> getPlayerSkills() {
        return this.skills;
    }

    @Override
    public int getSkillPoints() {
        return this.skillPoints;
    }

    @Override
    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    @Override
    public void grantSkillPoints(int skillPoints) {
        this.skillPoints += skillPoints;
    }

    @Override
    public boolean learnSkill(@Nonnull PassiveSkill passiveSkill) {
        if (this.skillPoints == 0) {
            return false;
        }
        if (this.skills.contains((Object)passiveSkill)) {
            return false;
        }
        --this.skillPoints;
        return this.skills.add((Object)passiveSkill);
    }

    @Override
    public boolean isTreeReset() {
        return this.treeReset;
    }

    @Override
    public void setTreeReset(boolean reset) {
        this.treeReset = reset;
    }

    @Override
    public void resetTree(ServerPlayer player) {
        this.skillPoints += this.getPlayerSkills().size();
        this.getPlayerSkills().forEach(skill -> skill.remove(player));
        this.getPlayerSkills().clear();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.m_128362_("TreeVersion", TREE_VERSION);
        tag.m_128405_("Points", this.skillPoints);
        tag.m_128379_("TreeReset", this.treeReset);
        ListTag skillsTag = new ListTag();
        this.skills.forEach(skill -> skillsTag.add((Object)StringTag.m_129297_((String)skill.getId().toString())));
        tag.m_128365_("Skills", (Tag)skillsTag);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.skills.clear();
        UUID treeVersion = tag.m_128403_("TreeVersion") ? tag.m_128342_("TreeVersion") : null;
        this.skillPoints = tag.m_128451_("Points");
        ListTag skillsTag = tag.m_128437_("Skills", 8);
        if (!TREE_VERSION.equals(treeVersion)) {
            this.skillPoints += skillsTag.size();
            this.treeReset = true;
            return;
        }
        for (Tag skillTag : skillsTag) {
            ResourceLocation skillId = new ResourceLocation(skillTag.m_7916_());
            PassiveSkill passiveSkill = SkillsReloader.getSkillById(skillId);
            if (passiveSkill == null || passiveSkill.isInvalid()) {
                this.skills.clear();
                this.treeReset = true;
                this.skillPoints += skillsTag.size();
                return;
            }
            this.skills.add((Object)passiveSkill);
        }
    }
}

