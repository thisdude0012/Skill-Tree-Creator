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

public class AmnesiaScrollItem
extends Item {
    public AmnesiaScrollItem() {
        super(new Item.Properties());
    }

    @NotNull
    public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack scroll = player.m_21120_(hand);
        IPlayerSkills skills = PlayerSkillsProvider.get(player);
        if (!player.m_150110_().f_35937_) {
            scroll.m_41774_(1);
        }
        if (!level.f_46443_) {
            level.m_6269_(null, (Entity)player, SoundEvents.f_11713_, player.m_5720_(), 0.9f, 0.7f + player.m_217043_().m_188501_() * 0.3f);
            level.m_6269_(null, (Entity)player, SoundEvents.f_215671_, player.m_5720_(), 0.4f, 0.2f + player.m_217043_().m_188501_() * 0.2f);
            skills.resetTree((ServerPlayer)player);
            skills.setSkillPoints((int)((double)skills.getSkillPoints() * (1.0 - ServerConfig.amnesia_scroll_penalty)));
            player.m_213846_((Component)Component.m_237115_((String)"skilltree.message.reset_command").m_130940_(ChatFormatting.YELLOW));
            NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), (Object)new SyncPlayerSkillsMessage(player));
        }
        return InteractionResultHolder.m_19092_((Object)scroll, (boolean)level.f_46443_);
    }

    public void m_7373_(@NotNull ItemStack itemStack, Level level, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        components.add((Component)Component.m_237115_((String)(this.m_5524_() + ".tooltip")).m_130940_(ChatFormatting.GOLD));
        double penalty = ServerConfig.amnesia_scroll_penalty;
        if (penalty > 0.0) {
            int textPenalty = (int)(penalty * 100.0);
            components.add((Component)Component.m_237110_((String)(this.m_5524_() + ".warning"), (Object[])new Object[]{textPenalty}).m_130940_(ChatFormatting.RED));
        }
    }
}

