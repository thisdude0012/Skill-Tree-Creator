/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.LivingEntity
 */
package daripher.skilltree.skill.bonus.multiplier;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface LivingMultiplier {
    public float getValue(LivingEntity var1);

    public Serializer getSerializer();

    default public String getDescriptionId() {
        ResourceLocation id = PSTRegistries.LIVING_MULTIPLIERS.get().getKey((Object)this.getSerializer());
        Objects.requireNonNull(id);
        return "skill_bonus_multiplier.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
    }

    public MutableComponent getTooltip(MutableComponent var1, SkillBonus.Target var2);

    default public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingMultiplier> consumer) {
    }

    public static interface Serializer
    extends daripher.skilltree.data.serializers.Serializer<LivingMultiplier> {
        public LivingMultiplier createDefaultInstance();
    }
}

