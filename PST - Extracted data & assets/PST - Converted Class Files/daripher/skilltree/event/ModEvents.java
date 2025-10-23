/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package daripher.skilltree.event;

import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.init.PSTItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="skilltree")
public class ModEvents {
    @SubscribeEvent
    public static void dropAmnesiaScroll(LivingDropsEvent event) {
        if (!ServerConfig.dragon_drops_amnesia_scroll) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity.m_6095_() == EntityType.f_20565_) {
            ItemStack scroll = new ItemStack((ItemLike)PSTItems.AMNESIA_SCROLL.get());
            event.getDrops().add(new ItemEntity(entity.m_9236_(), entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), scroll));
        }
    }
}

