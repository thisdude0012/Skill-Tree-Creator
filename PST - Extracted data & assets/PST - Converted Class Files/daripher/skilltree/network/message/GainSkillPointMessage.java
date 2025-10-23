/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class GainSkillPointMessage {
    public static GainSkillPointMessage decode(FriendlyByteBuf buf) {
        return new GainSkillPointMessage();
    }

    public static void receive(GainSkillPointMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.setPacketHandled(true);
        ServerPlayer player = Objects.requireNonNull(ctx.getSender());
        IPlayerSkills capability = PlayerSkillsProvider.get((Player)player);
        int skills = capability.getPlayerSkills().size();
        int points = capability.getSkillPoints();
        int level = skills + points;
        if (level >= ServerConfig.max_skill_points) {
            return;
        }
        int cost = ServerConfig.getSkillPointCost(level);
        if (player.f_36079_ < cost) {
            return;
        }
        player.m_6756_(-cost);
        capability.grantSkillPoints(1);
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncPlayerSkillsMessage((Player)player));
    }

    public void encode(FriendlyByteBuf buf) {
    }
}

