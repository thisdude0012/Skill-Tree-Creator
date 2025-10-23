/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.GrindstoneEvent$OnTakeItem
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package daripher.skilltree.event;

import daripher.skilltree.config.ServerConfig;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="skilltree")
public class PSTEvents {
    @SubscribeEvent
    public static void applyGrindstoneExpPenalty(GrindstoneEvent.OnTakeItem event) {
        event.setXp((int)((double)event.getXp() * ServerConfig.grindstone_exp_multiplier));
    }
}

