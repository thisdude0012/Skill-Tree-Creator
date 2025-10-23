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
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 */
package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.item.NoneItemCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class RepairEfficiencyBonus
implements SkillBonus<RepairEfficiencyBonus> {
    @Nonnull
    private ItemCondition itemCondition;
    private float multiplier;

    public RepairEfficiencyBonus(@Nonnull ItemCondition itemCondition, float multiplier) {
        this.itemCondition = itemCondition;
        this.multiplier = multiplier;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.REPAIR_EFFICIENCY.get();
    }

    public RepairEfficiencyBonus copy() {
        return new RepairEfficiencyBonus(this.itemCondition, this.multiplier);
    }

    @Override
    public RepairEfficiencyBonus multiply(double multiplier) {
        return new RepairEfficiencyBonus(this.itemCondition, (float)(multiplier * multiplier));
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof RepairEfficiencyBonus)) {
            return false;
        }
        RepairEfficiencyBonus otherBonus = (RepairEfficiencyBonus)other;
        return Objects.equals(otherBonus.itemCondition, this.itemCondition);
    }

    @Override
    public SkillBonus<RepairEfficiencyBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof RepairEfficiencyBonus)) {
            throw new IllegalArgumentException();
        }
        RepairEfficiencyBonus otherBonus = (RepairEfficiencyBonus)other;
        return new RepairEfficiencyBonus(this.itemCondition, otherBonus.multiplier + this.multiplier);
    }

    @Override
    public MutableComponent getTooltip() {
        Component itemDescription = this.itemCondition.getTooltip("plural.type");
        AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_BASE;
        MutableComponent bonusDescription = Component.m_237115_((String)(this.getDescriptionId() + ".bonus"));
        bonusDescription = TooltipHelper.getSkillBonusTooltip((Component)bonusDescription, (double)this.multiplier, operation).m_130948_(TooltipHelper.getItemBonusStyle(this.isPositive()));
        return Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{itemDescription, bonusDescription}).m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.multiplier > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<RepairEfficiencyBonus> consumer) {
        editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.multiplier).setNumericResponder(value -> this.selectMultiplier(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.itemCondition).setResponder(condition -> this.selectItemCondition(editor, consumer, (ItemCondition)condition)).setMenuInitFunc(() -> this.addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<RepairEfficiencyBonus> consumer) {
        this.itemCondition.addEditorWidgets(editor, condition -> {
            this.setItemCondition((ItemCondition)condition);
            consumer.accept(this.copy());
        });
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<RepairEfficiencyBonus> consumer, ItemCondition condition) {
        this.setItemCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectMultiplier(Consumer<RepairEfficiencyBonus> consumer, Double value) {
        this.setMultiplier(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setItemCondition(@Nonnull ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    @Nonnull
    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        RepairEfficiencyBonus that = (RepairEfficiencyBonus)obj;
        if (!Objects.equals(this.itemCondition, that.itemCondition)) {
            return false;
        }
        return this.multiplier == that.multiplier;
    }

    public int hashCode() {
        return Objects.hash(this.itemCondition, Float.valueOf(this.multiplier));
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public RepairEfficiencyBonus deserialize(JsonObject json) throws JsonParseException {
            ItemCondition condition = SerializationHelper.deserializeItemCondition(json);
            float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
            return new RepairEfficiencyBonus(condition, multiplier);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof RepairEfficiencyBonus)) {
                throw new IllegalArgumentException();
            }
            RepairEfficiencyBonus aBonus = (RepairEfficiencyBonus)bonus;
            SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
            json.addProperty("multiplier", (Number)Float.valueOf(aBonus.multiplier));
        }

        @Override
        public RepairEfficiencyBonus deserialize(CompoundTag tag) {
            ItemCondition condition = SerializationHelper.deserializeItemCondition(tag);
            float multiplier = tag.m_128457_("multiplier");
            return new RepairEfficiencyBonus(condition, multiplier);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof RepairEfficiencyBonus)) {
                throw new IllegalArgumentException();
            }
            RepairEfficiencyBonus aBonus = (RepairEfficiencyBonus)bonus;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
            tag.m_128350_("multiplier", aBonus.multiplier);
            return tag;
        }

        @Override
        public RepairEfficiencyBonus deserialize(FriendlyByteBuf buf) {
            return new RepairEfficiencyBonus(NetworkHelper.readItemCondition(buf), buf.readFloat());
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof RepairEfficiencyBonus)) {
                throw new IllegalArgumentException();
            }
            RepairEfficiencyBonus aBonus = (RepairEfficiencyBonus)bonus;
            NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
            buf.writeFloat(aBonus.multiplier);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new RepairEfficiencyBonus(NoneItemCondition.INSTANCE, 0.1f);
        }
    }
}

