/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.NonNullList
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.common.capabilities.AutoRegisterCapability
 *  net.minecraftforge.common.util.INBTSerializable
 */
package daripher.skilltree.capability.skill;

import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IPlayerSkills
extends INBTSerializable<CompoundTag> {
    public NonNullList<PassiveSkill> getPlayerSkills();

    public boolean learnSkill(PassiveSkill var1);

    public int getSkillPoints();

    public void setSkillPoints(int var1);

    public void grantSkillPoints(int var1);

    public boolean isTreeReset();

    public void setTreeReset(boolean var1);

    public void resetTree(ServerPlayer var1);
}

