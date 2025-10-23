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
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class CantUseItemBonus
implements SkillBonus<CantUseItemBonus> {
    @Nonnull
    private ItemCondition itemCondition;

    public CantUseItemBonus(@Nonnull ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.CANT_USE_ITEM.get();
    }

    public CantUseItemBonus copy() {
        return new CantUseItemBonus(this.itemCondition);
    }

    @Override
    public CantUseItemBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof CantUseItemBonus)) {
            return false;
        }
        CantUseItemBonus otherBonus = (CantUseItemBonus)other;
        return Objects.equals(otherBonus.itemCondition, this.itemCondition);
    }

    @Override
    public SkillBonus<CantUseItemBonus> merge(SkillBonus<?> other) {
        return this;
    }

    @Override
    public MutableComponent getTooltip() {
        Component itemDescription = this.itemCondition.getTooltip("plural");
        return Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{itemDescription}).m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<CantUseItemBonus> consumer) {
        editor.addLabel(0, 0, "Item Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.itemCondition).setResponder(condition -> this.selectItemCondition(editor, consumer, (ItemCondition)condition)).setMenuInitFunc(() -> this.addItemConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectItemCondition(SkillTreeEditor editor, Consumer<CantUseItemBonus> consumer, ItemCondition condition) {
        this.setItemCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void addItemConditionWidgets(SkillTreeEditor editor, Consumer<CantUseItemBonus> consumer) {
        this.itemCondition.addEditorWidgets(editor, c -> {
            this.setItemCondition((ItemCondition)c);
            consumer.accept(this.copy());
        });
    }

    public void setItemCondition(@Nonnull ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    @Nonnull
    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        CantUseItemBonus that = (CantUseItemBonus)obj;
        return Objects.equals(this.itemCondition, that.itemCondition);
    }

    public int hashCode() {
        return Objects.hash(this.itemCondition);
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public CantUseItemBonus deserialize(JsonObject json) throws JsonParseException {
            ItemCondition condition = SerializationHelper.deserializeItemCondition(json);
            return new CantUseItemBonus(condition);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof CantUseItemBonus)) {
                throw new IllegalArgumentException();
            }
            CantUseItemBonus aBonus = (CantUseItemBonus)bonus;
            SerializationHelper.serializeItemCondition(json, aBonus.itemCondition);
        }

        @Override
        public CantUseItemBonus deserialize(CompoundTag tag) {
            ItemCondition condition = SerializationHelper.deserializeItemCondition(tag);
            return new CantUseItemBonus(condition);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof CantUseItemBonus)) {
                throw new IllegalArgumentException();
            }
            CantUseItemBonus aBonus = (CantUseItemBonus)bonus;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemCondition(tag, aBonus.itemCondition);
            return tag;
        }

        @Override
        public CantUseItemBonus deserialize(FriendlyByteBuf buf) {
            return new CantUseItemBonus(NetworkHelper.readItemCondition(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof CantUseItemBonus)) {
                throw new IllegalArgumentException();
            }
            CantUseItemBonus aBonus = (CantUseItemBonus)bonus;
            NetworkHelper.writeItemCondition(buf, aBonus.itemCondition);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new CantUseItemBonus(new EquipmentCondition(EquipmentCondition.Type.BOW));
        }
    }
}

