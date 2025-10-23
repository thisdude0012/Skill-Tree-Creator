/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterClientReloadListenersEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.data;

import java.util.Set;
import java.util.TreeSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid="skilltree", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class SkillTexturesData
implements ResourceManagerReloadListener {
    public static final Set<ResourceLocation> TOOLTIP_BACKGROUNDS = new TreeSet<ResourceLocation>();
    public static final Set<ResourceLocation> BORDERS = new TreeSet<ResourceLocation>();
    public static final Set<ResourceLocation> ICONS = new TreeSet<ResourceLocation>();

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((PreparableReloadListener)new SkillTexturesData());
    }

    public void m_6213_(@NotNull ResourceManager resourceManager) {
        this.reloadTextures(TOOLTIP_BACKGROUNDS, "tooltip", resourceManager);
        this.reloadTextures(BORDERS, "icons/background", resourceManager);
        this.reloadTextures(ICONS, "icons", resourceManager);
        ICONS.removeAll(BORDERS);
    }

    private void reloadTextures(Set<ResourceLocation> storage, String folder, ResourceManager resourceManager) {
        storage.clear();
        storage.addAll(resourceManager.m_214159_("textures/" + folder, l -> l.m_135815_().endsWith(".png")).keySet());
    }
}

