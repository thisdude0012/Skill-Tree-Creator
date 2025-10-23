/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package daripher.skilltree.network.message;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.client.screen.SkillTreeScreen;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class SyncPlayerSkillsMessage {
    private List<ResourceLocation> learnedSkills = new ArrayList<ResourceLocation>();
    private int skillPoints;

    private SyncPlayerSkillsMessage() {
    }

    public SyncPlayerSkillsMessage(Player player) {
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        this.learnedSkills = skillsCapability.getPlayerSkills().stream().map(PassiveSkill::getId).toList();
        this.skillPoints = skillsCapability.getSkillPoints();
    }

    public static SyncPlayerSkillsMessage decode(FriendlyByteBuf buf) {
        SyncPlayerSkillsMessage result = new SyncPlayerSkillsMessage();
        int learnedSkillsCount = buf.readInt();
        for (int i = 0; i < learnedSkillsCount; ++i) {
            result.learnedSkills.add(new ResourceLocation(buf.m_130277_()));
        }
        result.skillPoints = buf.readInt();
        return result;
    }

    public static void receive(SyncPlayerSkillsMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.setPacketHandled(true);
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> SyncPlayerSkillsMessage.handlePacket(message, ctx)));
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void handlePacket(SyncPlayerSkillsMessage message, NetworkEvent.Context ctx) {
        ctx.setPacketHandled(true);
        Minecraft minecraft = Minecraft.m_91087_();
        assert (minecraft.f_91074_ != null);
        IPlayerSkills capability = PlayerSkillsProvider.get((Player)minecraft.f_91074_);
        capability.getPlayerSkills().clear();
        message.learnedSkills.stream().map(SkillsReloader::getSkillById).filter(Objects::nonNull).forEach(arg_0 -> capability.getPlayerSkills().add(arg_0));
        capability.setSkillPoints(message.skillPoints);
        Screen screen = minecraft.f_91080_;
        if (screen instanceof SkillTreeScreen) {
            SkillTreeScreen screen2 = (SkillTreeScreen)screen;
            screen2.updateSkillPoints(capability.getSkillPoints());
            screen2.m_7856_();
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.learnedSkills.size());
        this.learnedSkills.stream().map(ResourceLocation::toString).forEach(arg_0 -> ((FriendlyByteBuf)buf).m_130070_(arg_0));
        buf.writeInt(this.skillPoints);
    }
}

