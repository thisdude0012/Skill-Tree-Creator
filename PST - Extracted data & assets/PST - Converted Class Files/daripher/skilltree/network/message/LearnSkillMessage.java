/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.skill.PassiveSkill;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class LearnSkillMessage {
    private ResourceLocation skillId;

    public LearnSkillMessage(PassiveSkill passiveSkill) {
        this.skillId = passiveSkill.getId();
    }

    private LearnSkillMessage() {
    }

    public static LearnSkillMessage decode(FriendlyByteBuf buf) {
        LearnSkillMessage message = new LearnSkillMessage();
        message.skillId = new ResourceLocation(buf.m_130277_());
        return message;
    }

    public static void receive(LearnSkillMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.setPacketHandled(true);
        ServerPlayer player = ctx.getSender();
        Objects.requireNonNull(player);
        IPlayerSkills capability = PlayerSkillsProvider.get((Player)player);
        PassiveSkill skill = SkillsReloader.getSkillById(message.skillId);
        Objects.requireNonNull(skill);
        if (capability.learnSkill(skill)) {
            skill.learn(player, true);
        }
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncPlayerSkillsMessage((Player)player));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.m_130070_(this.skillId.toString());
    }
}

