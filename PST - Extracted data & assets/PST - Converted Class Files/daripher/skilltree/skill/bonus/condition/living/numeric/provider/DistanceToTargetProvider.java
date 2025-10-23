/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 */
package daripher.skilltree.skill.bonus.condition.living.numeric.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DistanceToTargetProvider
implements NumericValueProvider<DistanceToTargetProvider> {
    @Override
    public float getValue(LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return 0.0f;
        }
        Player player = (Player)entity;
        int lastTargetId = player.getPersistentData().m_128451_("LastAttackTarget");
        Entity target = entity.m_9236_().m_6815_(lastTargetId);
        if (target == null) {
            return 0.0f;
        }
        return target.m_20270_((Entity)entity);
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        Object key = "%s.multiplier.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        if (divisor != 1.0f) {
            key = (String)key + ".plural";
            return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, this.formatNumber(divisor)});
        }
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip});
    }

    @Override
    public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip, float requiredValue) {
        String key = "%s.condition.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        String valueDescription = this.formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("distance_to_target", valueDescription);
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, logicDescription});
    }

    @Override
    public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
        return Component.m_237113_((String)"Unsupported").m_130940_(ChatFormatting.RED);
    }

    @Override
    public NumericValueProvider.Serializer getSerializer() {
        return (NumericValueProvider.Serializer)PSTNumericValueProviders.DISTANCE_TO_TARGET.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
    }

    public static class Serializer
    implements NumericValueProvider.Serializer {
        @Override
        public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
            return new DistanceToTargetProvider();
        }

        @Override
        public void serialize(JsonObject json, NumericValueProvider<?> provider) {
            if (!(provider instanceof DistanceToTargetProvider)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public NumericValueProvider<?> deserialize(CompoundTag tag) {
            return new DistanceToTargetProvider();
        }

        @Override
        public CompoundTag serialize(NumericValueProvider<?> provider) {
            if (!(provider instanceof DistanceToTargetProvider)) {
                throw new IllegalArgumentException();
            }
            return new CompoundTag();
        }

        @Override
        public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
            return new DistanceToTargetProvider();
        }

        @Override
        public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
            if (!(provider instanceof DistanceToTargetProvider)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public NumericValueProvider<?> createDefaultInstance() {
            return new DistanceToTargetProvider();
        }
    }
}

