/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  javax.annotation.Nullable
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.resources.ResourceLocation$Serializer
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraftforge.event.AddReloadListenerEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.data.reloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.data.serializers.SkillBonusSerializer;
import daripher.skilltree.data.serializers.SkillRequirementSerializer;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid="skilltree")
public class SkillsReloader
extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, (Object)new ResourceLocation.Serializer()).registerTypeAdapter(SkillBonus.class, (Object)new SkillBonusSerializer()).registerTypeAdapter(SkillRequirement.class, (Object)new SkillRequirementSerializer()).registerTypeAdapter(MutableComponent.class, (Object)new Component.Serializer()).setPrettyPrinting().create();
    private static final Map<ResourceLocation, PassiveSkill> SKILLS = new HashMap<ResourceLocation, PassiveSkill>();

    public SkillsReloader() {
        super(GSON, "skills");
    }

    @SubscribeEvent
    public static void reloadSkills(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener)new SkillsReloader());
    }

    public static Map<ResourceLocation, PassiveSkill> getSkills() {
        return SKILLS;
    }

    @Nullable
    public static PassiveSkill getSkillById(ResourceLocation id) {
        return SKILLS.get(id);
    }

    public static void loadFromByteBuf(FriendlyByteBuf buf) {
        SKILLS.clear();
        NetworkHelper.readPassiveSkills(buf).forEach(s -> SKILLS.put(s.getId(), (PassiveSkill)s));
    }

    protected void apply(Map<ResourceLocation, JsonElement> map, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        SKILLS.clear();
        map.forEach(this::readSkill);
    }

    protected void readSkill(ResourceLocation id, JsonElement json) {
        try {
            PassiveSkill skill = (PassiveSkill)GSON.fromJson(json, PassiveSkill.class);
            SKILLS.put(skill.getId(), skill);
        }
        catch (Exception exception) {
            SkillTreeMod.LOGGER.error("Couldn't load passive skill {}", (Object)id);
            exception.printStackTrace();
        }
    }
}

