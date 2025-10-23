/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.LivingEntity
 */
package daripher.skilltree.skill.bonus.condition.living.numeric;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface NumericValueProvider<T> {
    public float getValue(LivingEntity var1);

    default public String getDescriptionId() {
        ResourceLocation id = PSTRegistries.NUMERIC_VALUE_PROVIDERS.get().getKey((Object)this.getSerializer());
        if (!1.$assertionsDisabled && id == null) {
            throw new AssertionError();
        }
        return "value_provider.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
    }

    default public String formatNumber(float number) {
        return TooltipHelper.formatNumber(number);
    }

    public MutableComponent getMultiplierTooltip(SkillBonus.Target var1, float var2, Component var3);

    public MutableComponent getConditionTooltip(SkillBonus.Target var1, NumericValueCondition.Logic var2, Component var3, float var4);

    public MutableComponent getRequirementTooltip(NumericValueCondition.Logic var1, float var2);

    public Serializer getSerializer();

    default public T createDefaultInstance() {
        return (T)this.getSerializer().createDefaultInstance();
    }

    public void addEditorWidgets(SkillTreeEditor var1, Consumer<NumericValueProvider<?>> var2);

    static {
        if (1.$assertionsDisabled) {
            // empty if block
        }
    }

    public static interface Serializer
    extends daripher.skilltree.data.serializers.Serializer<NumericValueProvider<?>> {
        public NumericValueProvider<?> createDefaultInstance();
    }
}

