/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.LivingEntity
 */
package daripher.skilltree.skill.bonus.condition.living;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.function.Consumer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface LivingCondition {
    public boolean isConditionMet(LivingEntity var1);

    default public String getDescriptionId() {
        ResourceLocation id = PSTRegistries.LIVING_CONDITIONS.get().getKey((Object)this.getSerializer());
        if (!1.$assertionsDisabled && id == null) {
            throw new AssertionError();
        }
        return "living_condition.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
    }

    public MutableComponent getTooltip(MutableComponent var1, SkillBonus.Target var2);

    public Serializer getSerializer();

    default public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
    }

    static {
        if (1.$assertionsDisabled) {
            // empty if block
        }
    }

    public static interface Serializer
    extends daripher.skilltree.data.serializers.Serializer<LivingCondition> {
        public LivingCondition createDefaultInstance();
    }
}

