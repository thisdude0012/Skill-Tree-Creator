/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$ConfigValue
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.config.ModConfigEvent
 */
package daripher.skilltree.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid="skilltree", bus=Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.Builder BUILDER;
    private static final ForgeConfigSpec.ConfigValue<Integer> MAX_SKILL_POINTS;
    private static final ForgeConfigSpec.ConfigValue<Integer> FIRST_SKILL_COST;
    private static final ForgeConfigSpec.ConfigValue<Integer> LAST_SKILL_COST;
    private static final ForgeConfigSpec.ConfigValue<Double> AMNESIA_SCROLL_PENALTY;
    private static final ForgeConfigSpec.ConfigValue<Double> GRINDSTONE_EXP_MULTIPLIER;
    private static final ForgeConfigSpec.ConfigValue<Boolean> SHOW_CHAT_MESSAGES;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_EXP_EXCHANGE;
    private static final ForgeConfigSpec.ConfigValue<Boolean> DRAGON_DROPS_AMNESIA_SCROLL;
    private static final ForgeConfigSpec.ConfigValue<Boolean> USE_POINTS_COSTS_ARRAY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> SKILL_POINTS_COSTS;
    public static final int DEFAULT_MAX_SKILLS = 100;
    public static int max_skill_points;
    public static int first_skill_cost;
    public static int last_skill_cost;
    public static double amnesia_scroll_penalty;
    public static double grindstone_exp_multiplier;
    public static boolean show_chat_messages;
    public static boolean use_skill_points_array;
    public static boolean enable_exp_exchange;
    public static boolean dragon_drops_amnesia_scroll;
    public static List<? extends Integer> skill_points_costs;

    static List<Integer> generateDefaultPointsCosts() {
        ArrayList<Integer> costs = new ArrayList<Integer>();
        costs.add(15);
        for (int i = 1; i < 100; ++i) {
            int previousCost = (Integer)costs.get(costs.size() - 1);
            int cost = previousCost + 3 + i;
            costs.add(cost);
        }
        return costs;
    }

    @SubscribeEvent
    static void load(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        skill_points_costs = (List)SKILL_POINTS_COSTS.get();
        use_skill_points_array = (Boolean)USE_POINTS_COSTS_ARRAY.get();
        max_skill_points = (Integer)MAX_SKILL_POINTS.get();
        first_skill_cost = (Integer)FIRST_SKILL_COST.get();
        last_skill_cost = (Integer)LAST_SKILL_COST.get();
        amnesia_scroll_penalty = (Double)AMNESIA_SCROLL_PENALTY.get();
        grindstone_exp_multiplier = (Double)GRINDSTONE_EXP_MULTIPLIER.get();
        show_chat_messages = (Boolean)SHOW_CHAT_MESSAGES.get();
        enable_exp_exchange = (Boolean)ENABLE_EXP_EXCHANGE.get();
        dragon_drops_amnesia_scroll = (Boolean)DRAGON_DROPS_AMNESIA_SCROLL.get();
    }

    public static int getSkillPointCost(int level) {
        if (use_skill_points_array) {
            if (level >= skill_points_costs.size()) {
                return skill_points_costs.get(skill_points_costs.size() - 1);
            }
            return skill_points_costs.get(level);
        }
        return first_skill_cost + (last_skill_cost - first_skill_cost) * level / max_skill_points;
    }

    static {
        BUILDER = new ForgeConfigSpec.Builder();
        BUILDER.push("Skill Points");
        MAX_SKILL_POINTS = BUILDER.defineInRange("Maximum skill points", 100, 1, 1000);
        FIRST_SKILL_COST = BUILDER.defineInRange("First skill point cost", 15, 0, Integer.MAX_VALUE);
        LAST_SKILL_COST = BUILDER.defineInRange("Last skill point cost", 1400, 0, Integer.MAX_VALUE);
        BUILDER.comment("You can set cost for each skill point instead");
        USE_POINTS_COSTS_ARRAY = BUILDER.define("Use skill points costs array", false);
        BUILDER.comment("This list's size must be equal to maximum skill points.");
        SKILL_POINTS_COSTS = BUILDER.defineList("Levelup costs", ServerConfig.generateDefaultPointsCosts(), o -> {
            Integer i;
            return o instanceof Integer && (i = (Integer)o) > 0;
        });
        BUILDER.comment("Disabling this will remove chat messages when you gain a skill point.");
        SHOW_CHAT_MESSAGES = BUILDER.define("Show chat messages", true);
        BUILDER.comment("Warning: If you disable this make sure you make alternative way of getting skill points.");
        ENABLE_EXP_EXCHANGE = BUILDER.define("Enable exprerience exchange for skill points", true);
        BUILDER.pop();
        BUILDER.push("Amnesia Scroll");
        BUILDER.comment("How much levels (percentage) player lose using amnesia scroll");
        AMNESIA_SCROLL_PENALTY = BUILDER.defineInRange("Amnesia scroll penalty", 0.2, 0.0, 1.0);
        DRAGON_DROPS_AMNESIA_SCROLL = BUILDER.define("Drop amnesia scrolls from the Ender Dragon", true);
        BUILDER.pop();
        BUILDER.push("Experience");
        GRINDSTONE_EXP_MULTIPLIER = BUILDER.defineInRange("Grindstone experience multiplier", 0.1, 0.0, 1.0);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

