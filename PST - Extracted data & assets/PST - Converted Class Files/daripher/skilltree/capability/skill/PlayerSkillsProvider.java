/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.CapabilityManager
 *  net.minecraftforge.common.capabilities.CapabilityToken
 *  net.minecraftforge.common.capabilities.ICapabilityProvider
 *  net.minecraftforge.common.capabilities.ICapabilitySerializable
 *  net.minecraftforge.common.util.LazyOptional
 *  net.minecraftforge.event.AttachCapabilitiesEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.network.PacketDistributor
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package daripher.skilltree.capability.skill;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkills;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid="skilltree")
public class PlayerSkillsProvider
implements ICapabilitySerializable<CompoundTag> {
    private static final ResourceLocation CAPABILITY_ID = new ResourceLocation("skilltree", "player_skills");
    private static final Capability<IPlayerSkills> CAPABILITY = CapabilityManager.get((CapabilityToken)new CapabilityToken<IPlayerSkills>(){});
    private final LazyOptional<IPlayerSkills> optionalCapability = LazyOptional.of(PlayerSkills::new);

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) {
            return;
        }
        PlayerSkillsProvider provider = new PlayerSkillsProvider();
        event.addCapability(CAPABILITY_ID, (ICapabilityProvider)provider);
    }

    @SubscribeEvent
    public static void persistThroughDeath(PlayerEvent.Clone event) {
        if (event.getEntity().m_9236_().f_46443_) {
            return;
        }
        event.getOriginal().reviveCaps();
        IPlayerSkills originalData = PlayerSkillsProvider.get(event.getOriginal());
        IPlayerSkills cloneData = PlayerSkillsProvider.get(event.getEntity());
        cloneData.deserializeNBT((Tag)((CompoundTag)originalData.serializeNBT()));
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void syncSkills(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().m_9236_().f_46443_) {
            return;
        }
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), (Object)new SyncServerDataMessage());
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void restoreSkillsAttributeModifiers(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)entity;
        PlayerSkillsProvider.get((Player)player).getPlayerSkills().forEach(skill -> skill.learn(player, false));
    }

    @SubscribeEvent
    public static void sendTreeResetMessage(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        if (event.getEntity().m_9236_().f_46443_) {
            return;
        }
        IPlayerSkills capability = PlayerSkillsProvider.get(player);
        if (capability.isTreeReset()) {
            player.m_213846_((Component)Component.m_237115_((String)"skilltree.message.reset").m_130940_(ChatFormatting.YELLOW));
            capability.setTreeReset(false);
        }
    }

    @SubscribeEvent
    public static void syncPlayerSkills(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)entity;
        NetworkDispatcher.network_channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncPlayerSkillsMessage((Player)player));
    }

    @NotNull
    public static IPlayerSkills get(Player player) {
        return (IPlayerSkills)player.getCapability(CAPABILITY).orElseThrow(NullPointerException::new);
    }

    public static boolean hasSkills(Player player) {
        return player.getCapability(CAPABILITY).isPresent();
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CAPABILITY ? this.optionalCapability.cast() : LazyOptional.empty();
    }

    public CompoundTag serializeNBT() {
        return (CompoundTag)((IPlayerSkills)this.optionalCapability.orElseThrow(NullPointerException::new)).serializeNBT();
    }

    public void deserializeNBT(CompoundTag compoundTag) {
        ((IPlayerSkills)this.optionalCapability.orElseThrow(NullPointerException::new)).deserializeNBT((Tag)compoundTag);
    }
}

