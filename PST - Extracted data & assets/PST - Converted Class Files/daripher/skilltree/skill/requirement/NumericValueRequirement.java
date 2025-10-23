/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 */
package daripher.skilltree.skill.requirement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.bonus.condition.effect.EffectType;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.provider.EffectAmountProvider;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class NumericValueRequirement
implements SkillRequirement<NumericValueRequirement> {
    private NumericValueCondition condition;

    public NumericValueRequirement(NumericValueCondition condition) {
        this.condition = condition;
    }

    @Override
    public boolean isRequirementMet(Player player) {
        return this.condition.isConditionMet((LivingEntity)player);
    }

    @Override
    public MutableComponent getTooltip() {
        return this.condition.getValueProvider().getRequirementTooltip(this.condition.getLogic(), this.condition.getRequiredValue());
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueRequirement> consumer) {
        this.condition.addEditorWidgets(editor, (LivingCondition condition) -> this.setCondition((NumericValueCondition)condition));
    }

    public void setCondition(NumericValueCondition condition) {
        this.condition = condition;
    }

    @Override
    public NumericValueRequirement copy() {
        return new NumericValueRequirement(this.condition);
    }

    @Override
    public SkillRequirement.Serializer getSerializer() {
        return (SkillRequirement.Serializer)PSTSkillRequirements.NUMERIC_VALUE.get();
    }

    public static class Serializer
    implements SkillRequirement.Serializer {
        @Override
        public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
            NumericValueCondition condition = (NumericValueCondition)((LivingCondition.Serializer)PSTLivingConditions.NUMERIC_VALUE.get()).deserialize(json);
            return new NumericValueRequirement(condition);
        }

        @Override
        public void serialize(JsonObject json, SkillRequirement<?> requirement) {
            if (requirement instanceof NumericValueRequirement) {
                NumericValueRequirement aRequirement = (NumericValueRequirement)requirement;
                aRequirement.condition.getSerializer().serialize(json, aRequirement.condition);
            }
        }

        @Override
        public SkillRequirement<?> deserialize(CompoundTag tag) {
            NumericValueCondition condition = (NumericValueCondition)((LivingCondition.Serializer)PSTLivingConditions.NUMERIC_VALUE.get()).deserialize(tag);
            return new NumericValueRequirement(condition);
        }

        @Override
        public CompoundTag serialize(SkillRequirement<?> requirement) {
            CompoundTag tag = new CompoundTag();
            if (requirement instanceof NumericValueRequirement) {
                NumericValueRequirement aRequirement = (NumericValueRequirement)requirement;
                return aRequirement.condition.getSerializer().serialize(aRequirement.condition);
            }
            return tag;
        }

        @Override
        public SkillRequirement<?> deserialize(FriendlyByteBuf buf) {
            NumericValueCondition condition = (NumericValueCondition)((LivingCondition.Serializer)PSTLivingConditions.NUMERIC_VALUE.get()).deserialize(buf);
            return new NumericValueRequirement(condition);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof NumericValueRequirement) {
                NumericValueRequirement aRequirement = (NumericValueRequirement)requirement;
                aRequirement.condition.getSerializer().serialize(buf, aRequirement.condition);
            }
        }

        @Override
        public SkillRequirement<?> createDefaultInstance() {
            return new NumericValueRequirement(new NumericValueCondition(new EffectAmountProvider(EffectType.BENEFICIAL), 5.0f, NumericValueCondition.Logic.MORE));
        }
    }
}

