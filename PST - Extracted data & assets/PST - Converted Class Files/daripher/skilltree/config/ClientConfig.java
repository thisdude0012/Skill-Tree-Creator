/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$ConfigValue
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.config.ModConfigEvent$Loading
 */
package daripher.skilltree.config;

import daripher.skilltree.skill.PassiveSkill;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid="skilltree", bus=Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FAVORITE_SKILLS;
    private static final ForgeConfigSpec.ConfigValue<? extends String> FAVORITE_COLOR_HEX;
    public static Set<ResourceLocation> favorite_skills;
    public static int favorite_color;
    public static boolean favorite_color_is_rainbow;

    private static boolean isValidSkillId(Object o) {
        String s;
        return o instanceof String && ResourceLocation.m_135830_((String)(s = (String)o));
    }

    private static boolean isValidHexColor(Object o) {
        if (!(o instanceof String)) {
            return false;
        }
        String s = (String)o;
        if (s.equals("rainbow")) {
            return true;
        }
        try {
            Integer.decode(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @SubscribeEvent
    static void load(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        favorite_skills = ((List)FAVORITE_SKILLS.get()).stream().map(ResourceLocation::new).collect(Collectors.toSet());
        favorite_color_is_rainbow = ((String)FAVORITE_COLOR_HEX.get()).equals("rainbow");
        if (!favorite_color_is_rainbow) {
            favorite_color = Integer.decode((String)FAVORITE_COLOR_HEX.get());
        }
    }

    public static void toggleFavoriteSkill(PassiveSkill skill) {
        if (favorite_skills.contains(skill.getId())) {
            favorite_skills.remove(skill.getId());
        } else {
            favorite_skills.add(skill.getId());
        }
        FAVORITE_SKILLS.set(favorite_skills.stream().map(ResourceLocation::toString).collect(Collectors.toList()));
    }

    static {
        BUILDER = new ForgeConfigSpec.Builder();
        FAVORITE_SKILLS = BUILDER.defineList("favorite_skills", new ArrayList(), ClientConfig::isValidSkillId);
        FAVORITE_COLOR_HEX = BUILDER.define("favorite_color_hex", (Object)"#42B0FF", ClientConfig::isValidHexColor);
        SPEC = BUILDER.build();
    }
}

