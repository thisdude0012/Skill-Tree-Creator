/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.network.PacketDistributor
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.item;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class WisdomScrollItem
extends Item {
    public WisdomScrollItem() {
        super(new Item.Properties());
    }

    @NotNull
    public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.m_21120_(hand);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        int totalSkillPoints = skillsCapability.getPlayerSkills().size() + skillsCapability.getSkillPoints();
        if (totalSkillPoints >= ServerConfig.max_skill_points) {
            return InteractionResultHolder.m_19100_((Object)itemInHand);
        }
        if (!player.m_150110_().f_35937_) {
            itemInHand.m_41774_(1);
        }
        if (!level.f_46443_) {
            level.m_6269_(null, (Entity)player, SoundEvents.f_11713_, player.m_5720_(), 0.9f, 0.7f + player.m_217043_().m_188501_() * 0.3f);
            level.m_6269_(null, (Entity)player, SoundEvents.f_12275_, player.m_5720_(), 0.4f, 0.2f + player.m_217043_().m_188501_() * 0.3f);
            skillsCapability.grantSkillPoints(1);
            NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), (Object)new SyncPlayerSkillsMessage(player));
            if (ServerConfig.show_chat_messages) {
                player.m_213846_((Component)Component.m_237115_((String)"skilltree.message.point_command").m_130940_(ChatFormatting.YELLOW));
            }
        }
        return InteractionResultHolder.m_19092_((Object)itemInHand, (boolean)level.f_46443_);
    }

    public void m_7373_(@NotNull ItemStack itemStack, Level level, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        components.add((Component)Component.m_237115_((String)(this.m_5524_() + ".tooltip")).m_130940_(ChatFormatting.GOLD));
    }
}

