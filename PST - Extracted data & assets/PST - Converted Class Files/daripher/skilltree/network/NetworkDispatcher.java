/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package daripher.skilltree.network;

import daripher.skilltree.network.message.GainSkillPointMessage;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.network.message.SyncServerDataMessage;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid="skilltree")
public class NetworkDispatcher {
    public static SimpleChannel network_channel;

    @SubscribeEvent
    public static void registerNetworkChannel(FMLCommonSetupEvent event) {
        network_channel = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("skilltree", "channel"), () -> "1.0", s -> true, s -> true);
        network_channel.registerMessage(1, SyncServerDataMessage.class, SyncServerDataMessage::encode, SyncServerDataMessage::decode, SyncServerDataMessage::receive, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        network_channel.registerMessage(2, SyncPlayerSkillsMessage.class, SyncPlayerSkillsMessage::encode, SyncPlayerSkillsMessage::decode, SyncPlayerSkillsMessage::receive, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        network_channel.registerMessage(3, LearnSkillMessage.class, LearnSkillMessage::encode, LearnSkillMessage::decode, LearnSkillMessage::receive, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        network_channel.registerMessage(4, GainSkillPointMessage.class, GainSkillPointMessage::encode, GainSkillPointMessage::decode, GainSkillPointMessage::receive, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}

