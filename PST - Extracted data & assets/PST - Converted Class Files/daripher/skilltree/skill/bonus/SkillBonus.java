/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.skill.bonus;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public interface SkillBonus<T extends SkillBonus<T>>
extends Comparable<SkillBonus<?>> {
    default public void onSkillLearned(ServerPlayer player, boolean firstTime) {
    }

    default public void onSkillRemoved(ServerPlayer player) {
    }

    public boolean canMerge(SkillBonus<?> var1);

    default public boolean sameBonus(SkillBonus<?> other) {
        return this.canMerge(other);
    }

    public SkillBonus<T> merge(SkillBonus<?> var1);

    public SkillBonus<T> copy();

    public T multiply(double var1);

    public Serializer getSerializer();

    default public String getDescriptionId() {
        ResourceLocation id = PSTRegistries.SKILL_BONUSES.get().getKey((Object)this.getSerializer());
        Objects.requireNonNull(id);
        return "skill_bonus.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
    }

    public MutableComponent getTooltip();

    default public void gatherInfo(Consumer<MutableComponent> consumer) {
        TooltipHelper.consumeTranslated(this.getDescriptionId() + ".info", consumer);
    }

    public boolean isPositive();

    public void addEditorWidgets(SkillTreeEditor var1, int var2, Consumer<T> var3);

    @Override
    default public int compareTo(@NotNull SkillBonus<?> o) {
        if (this.isPositive() != o.isPositive()) {
            return this.isPositive() ? -1 : 1;
        }
        String regex = "\\+?-?[0-9]+\\.?[0-9]?%? ";
        String as = this.getTooltip().getString().replaceAll(regex, "");
        String bs = o.getTooltip().getString().replaceAll(regex, "");
        return as.compareTo(bs);
    }

    public static interface Serializer
    extends daripher.skilltree.data.serializers.Serializer<SkillBonus<?>> {
        public SkillBonus<?> createDefaultInstance();
    }

    public static enum Target {
        PLAYER,
        ENEMY;


        public String getName() {
            return this.name().toLowerCase();
        }

        public static Target fromName(String name) {
            return Target.valueOf(name.toUpperCase());
        }
    }
}

