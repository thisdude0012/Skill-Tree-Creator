/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nonnull
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 */
package daripher.skilltree.skill.bonus.condition.living.numeric.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTNumericValueProviders;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueCondition;
import daripher.skilltree.skill.bonus.condition.living.numeric.NumericValueProvider;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EquipmentDurabilityProvider
implements NumericValueProvider<EquipmentDurabilityProvider> {
    @Nonnull
    private ItemCondition itemCondition;

    public EquipmentDurabilityProvider(@Nonnull ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    @Override
    public float getValue(LivingEntity entity) {
        return PlayerHelper.getAllEquipment(entity).filter(this.itemCondition::met).map(ItemStack::m_41776_).reduce(Integer::sum).orElse(0).intValue();
    }

    @Override
    public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float divisor, Component bonusTooltip) {
        Object key = "%s.multiplier.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        Component itemDescription = this.itemCondition.getTooltip();
        if (divisor != 1.0f) {
            key = (String)key + ".plural";
            return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, this.formatNumber(divisor), itemDescription});
        }
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, itemDescription});
    }

    @Override
    public MutableComponent getConditionTooltip(SkillBonus.Target target, NumericValueCondition.Logic logic, Component bonusTooltip, float requiredValue) {
        String key = "%s.condition.%s".formatted(new Object[]{this.getDescriptionId(), target.getName()});
        Component itemDescription = this.itemCondition.getTooltip();
        String valueDescription = this.formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("equipment_durability", valueDescription);
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, itemDescription, logicDescription});
    }

    @Override
    public MutableComponent getRequirementTooltip(NumericValueCondition.Logic logic, float requiredValue) {
        String key = "%s.requirement".formatted(new Object[]{this.getDescriptionId()});
        Component itemDescription = this.itemCondition.getTooltip();
        String valueDescription = this.formatNumber(requiredValue);
        Component logicDescription = logic.getTooltip("equipment_durability", valueDescription);
        return Component.m_237110_((String)key, (Object[])new Object[]{logicDescription, itemDescription});
    }

    @Override
    public NumericValueProvider.Serializer getSerializer() {
        return (NumericValueProvider.Serializer)PSTNumericValueProviders.EQUIPMENT_DURABILITY.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.itemCondition).setResponder(condition -> this.selectItemCondition(editor, consumer, (ItemCondition)condition)).setMenuInitFunc(() -> this.addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer) {
        this.itemCondition.addEditorWidgets(editor, (ItemCondition condition) -> {
            this.setItemCondition((ItemCondition)condition);
            consumer.accept(this);
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<NumericValueProvider<?>> consumer, ItemCondition condition) {
        this.setItemCondition(condition);
        consumer.accept(this);
        editor.rebuildWidgets();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EquipmentDurabilityProvider that = (EquipmentDurabilityProvider)o;
        return Objects.equals(this.itemCondition, that.itemCondition);
    }

    public int hashCode() {
        return Objects.hash(this.itemCondition);
    }

    public void setItemCondition(@Nonnull ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public static class Serializer
    implements NumericValueProvider.Serializer {
        @Override
        public NumericValueProvider<?> deserialize(JsonObject json) throws JsonParseException {
            ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(json);
            return new EquipmentDurabilityProvider(itemCondition);
        }

        @Override
        public void serialize(JsonObject json, NumericValueProvider<?> provider) {
            if (!(provider instanceof EquipmentDurabilityProvider)) {
                throw new IllegalArgumentException();
            }
            EquipmentDurabilityProvider aProvider = (EquipmentDurabilityProvider)provider;
            SerializationHelper.serializeItemCondition(json, aProvider.itemCondition);
        }

        @Override
        public NumericValueProvider<?> deserialize(CompoundTag tag) {
            ItemCondition itemCondition = SerializationHelper.deserializeItemCondition(tag);
            return new EquipmentDurabilityProvider(itemCondition);
        }

        @Override
        public CompoundTag serialize(NumericValueProvider<?> provider) {
            if (!(provider instanceof EquipmentDurabilityProvider)) {
                throw new IllegalArgumentException();
            }
            EquipmentDurabilityProvider aProvider = (EquipmentDurabilityProvider)provider;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemCondition(tag, aProvider.itemCondition);
            return tag;
        }

        @Override
        public NumericValueProvider<?> deserialize(FriendlyByteBuf buf) {
            ItemCondition itemCondition = NetworkHelper.readItemCondition(buf);
            return new EquipmentDurabilityProvider(itemCondition);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, NumericValueProvider<?> provider) {
            if (!(provider instanceof EquipmentDurabilityProvider)) {
                throw new IllegalArgumentException();
            }
            EquipmentDurabilityProvider aProvider = (EquipmentDurabilityProvider)provider;
            NetworkHelper.writeItemCondition(buf, aProvider.itemCondition);
        }

        @Override
        public NumericValueProvider<?> createDefaultInstance() {
            return new EquipmentDurabilityProvider(new EquipmentCondition(EquipmentCondition.Type.WEAPON));
        }
    }
}

