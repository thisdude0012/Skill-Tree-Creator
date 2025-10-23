/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffectUtil
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.client.tooltip;

import daripher.skilltree.effect.SkillBonusEffect;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TooltipHelper {
    private static final Style SKILL_BONUS_STYLE = Style.f_131099_.m_178520_(8092645);
    private static final Style SKILL_BONUS_STYLE_NEGATIVE = Style.f_131099_.m_178520_(14834266);
    private static final Style ITEM_BONUS_STYLE = Style.f_131099_.m_178520_(8041442);
    private static final Style ITEM_BONUS_STYLE_NEGATIVE = Style.f_131099_.m_178520_(14391186);
    private static final Style SKILL_REQUIREMENT_STYLE = Style.f_131099_.m_178520_(8643194);
    private static final Style SKILL_REQUIREMENT_STYLE_UNFINISHED = Style.f_131099_.m_178520_(14834266);

    public static Component getEffectTooltip(MobEffectInstance effect) {
        Component effectDescription;
        MobEffect mobEffect = effect.m_19544_();
        if (mobEffect instanceof SkillBonusEffect) {
            SkillBonusEffect skillEffect = (SkillBonusEffect)mobEffect;
            effectDescription = skillEffect.getBonus().copy().multiply(effect.m_19564_() + 1).getTooltip().m_6270_(Style.f_131099_);
        } else {
            effectDescription = effect.m_19544_().m_19482_();
            if (effect.m_19564_() == 0) {
                return effectDescription;
            }
            MutableComponent amplifier = Component.m_237115_((String)("potion.potency." + effect.m_19564_()));
            effectDescription = Component.m_237110_((String)"potion.withAmplifier", (Object[])new Object[]{effectDescription, amplifier});
        }
        return effectDescription;
    }

    public static Component getEffectTooltipWithTime(MobEffectInstance effect) {
        Component effectDescription = TooltipHelper.getEffectTooltip(effect);
        Component durationDescription = MobEffectUtil.m_267641_((MobEffectInstance)effect, (float)1.0f);
        ChatFormatting style = effect.m_19544_().m_19483_().m_19497_();
        return Component.m_237110_((String)"potion.withDuration", (Object[])new Object[]{effectDescription, durationDescription}).m_130940_(style);
    }

    public static Component getOperationName(AttributeModifier.Operation operation) {
        return Component.m_237113_((String)(switch (operation) {
            default -> throw new IncompatibleClassChangeError();
            case AttributeModifier.Operation.ADDITION -> "Addition";
            case AttributeModifier.Operation.MULTIPLY_BASE -> "Multiply Base";
            case AttributeModifier.Operation.MULTIPLY_TOTAL -> "Multiply Total";
        }));
    }

    public static MutableComponent getOptionalTooltip(String descriptionId, String subtype, Object ... args) {
        String key = "%s.%s".formatted(new Object[]{descriptionId, subtype});
        MutableComponent tooltip = Component.m_237110_((String)key, (Object[])args);
        if (!tooltip.getString().equals(key)) {
            return tooltip;
        }
        return Component.m_237110_((String)descriptionId, (Object[])args);
    }

    public static void consumeTranslated(String descriptionId, Consumer<MutableComponent> consumer) {
        MutableComponent tooltip = Component.m_237115_((String)descriptionId);
        if (!tooltip.getString().equals(descriptionId)) {
            consumer.accept(tooltip);
        }
    }

    public static MutableComponent getSkillBonusTooltip(Component bonusDescription, double amount, AttributeModifier.Operation operation) {
        float multiplier = 1.0f;
        if (operation != AttributeModifier.Operation.ADDITION) {
            multiplier = 100.0f;
        }
        double visibleAmount = amount * (double)multiplier;
        if (amount < 0.0) {
            visibleAmount *= -1.0;
        }
        Object operationDescription = amount > 0.0 ? "plus" : "take";
        operationDescription = "attribute.modifier." + (String)operationDescription + "." + operation.ordinal();
        String multiplierDescription = TooltipHelper.formatNumber(visibleAmount);
        return Component.m_237110_((String)operationDescription, (Object[])new Object[]{multiplierDescription, bonusDescription});
    }

    public static String formatNumber(double number) {
        String formatted = ItemStack.f_41584_.format(number);
        if (formatted.endsWith(".0")) {
            formatted = formatted.substring(0, formatted.length() - 2);
        }
        return formatted;
    }

    public static MutableComponent getSkillBonusTooltip(String bonus, double amount, AttributeModifier.Operation operation) {
        return TooltipHelper.getSkillBonusTooltip((Component)Component.m_237115_((String)bonus), amount, operation);
    }

    public static Style getSkillBonusStyle(boolean positive) {
        return positive ? SKILL_BONUS_STYLE : SKILL_BONUS_STYLE_NEGATIVE;
    }

    public static Style getSkillRequirementStyle(boolean finished) {
        return finished ? SKILL_REQUIREMENT_STYLE : SKILL_REQUIREMENT_STYLE_UNFINISHED;
    }

    public static Style getItemBonusStyle(boolean positive) {
        return positive ? ITEM_BONUS_STYLE : ITEM_BONUS_STYLE_NEGATIVE;
    }

    public static MutableComponent getTextureName(ResourceLocation location) {
        String texture = location.m_135815_();
        texture = texture.substring(texture.lastIndexOf("/") + 1);
        texture = texture.replace(".png", "");
        texture = TooltipHelper.idToName(texture);
        return Component.m_237113_((String)texture);
    }

    public static MutableComponent getTargetName(SkillBonus.Target target) {
        return Component.m_237113_((String)TooltipHelper.idToName(target.name().toLowerCase()));
    }

    public static String getRecipeDescriptionId(ResourceLocation recipeId) {
        return "recipe.%s.%s".formatted(new Object[]{recipeId.m_135827_(), recipeId.m_135815_()});
    }

    @NotNull
    public static String idToName(String path) {
        String[] words = path.split("_");
        StringBuilder name = new StringBuilder();
        Arrays.stream(words).map(w -> w.substring(0, 1).toUpperCase() + w.substring(1)).forEach(w -> {
            name.append(" ");
            name.append((String)w);
        });
        return name.substring(1);
    }

    public static List<MutableComponent> split(MutableComponent component, Font font, int maxWidth) {
        String[] split = component.getString().split(" ");
        if (split.length < 2) {
            return List.of((Object)component);
        }
        Object line = split[0];
        ArrayList<MutableComponent> components = new ArrayList<MutableComponent>();
        for (int i = 1; i < split.length; ++i) {
            String next = (String)line + " " + split[i];
            if (font.m_92895_(next) > maxWidth) {
                components.add(Component.m_237115_((String)line).m_130948_(component.m_7383_()));
                line = "  " + split[i];
                continue;
            }
            line = next;
        }
        components.add(Component.m_237115_((String)line).m_130948_(component.m_7383_()));
        return components;
    }

    @NotNull
    public static String getTrimmedString(Font font, String message, int maxWidth) {
        if (font.m_92895_((String)message) > maxWidth) {
            while (font.m_92895_((String)message + "...") > maxWidth) {
                message = ((String)message).substring(0, ((String)message).length() - 1);
            }
            message = (String)message + "...";
        }
        return message;
    }

    @NotNull
    public static String getTrimmedString(String message, int maxWidth) {
        return TooltipHelper.getTrimmedString(Minecraft.m_91087_().f_91062_, message, maxWidth);
    }

    public static Component getSlotTooltip(String slotName, String type) {
        return Component.m_237115_((String)"curio.slot.%s.%s".formatted(new Object[]{slotName, type}));
    }

    public static Component getSlotTooltip(String slotName) {
        return Component.m_237115_((String)"curio.slot.%s".formatted(new Object[]{slotName}));
    }
}

