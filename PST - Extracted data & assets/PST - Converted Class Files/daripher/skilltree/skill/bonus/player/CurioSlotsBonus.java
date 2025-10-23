/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  top.theillusivec4.curios.api.CuriosApi
 */
package daripher.skilltree.skill.bonus.player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import top.theillusivec4.curios.api.CuriosApi;

public final class CurioSlotsBonus
implements SkillBonus<CurioSlotsBonus> {
    private String slotName;
    private int amount;
    private final UUID modifierId;

    public CurioSlotsBonus(String slotName, int amount) {
        this.slotName = slotName;
        this.amount = amount;
        this.modifierId = UUID.randomUUID();
    }

    private CurioSlotsBonus(String slotName, int amount, UUID modifierId) {
        this.slotName = slotName;
        this.amount = amount;
        this.modifierId = modifierId;
    }

    @Override
    public void onSkillLearned(ServerPlayer player, boolean firstTime) {
        if (firstTime) {
            CuriosApi.getCuriosInventory((LivingEntity)player).ifPresent(inv -> {
                HashMultimap modifiers = HashMultimap.create();
                AttributeModifier modifier = new AttributeModifier(this.modifierId, "SkillBonus", (double)this.amount, AttributeModifier.Operation.ADDITION);
                modifiers.put((Object)this.slotName, (Object)modifier);
                inv.addPermanentSlotModifiers((Multimap)modifiers);
            });
        }
    }

    @Override
    public void onSkillRemoved(ServerPlayer player) {
        CuriosApi.getCuriosInventory((LivingEntity)player).ifPresent(inv -> {
            HashMultimap modifiers = HashMultimap.create();
            AttributeModifier modifier = new AttributeModifier(this.modifierId, "SkillBonus", (double)this.amount, AttributeModifier.Operation.ADDITION);
            modifiers.put((Object)this.slotName, (Object)modifier);
            inv.removeSlotModifiers((Multimap)modifiers);
        });
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.CURIO_SLOTS.get();
    }

    public CurioSlotsBonus copy() {
        return new CurioSlotsBonus(this.slotName, this.amount, this.modifierId);
    }

    @Override
    public CurioSlotsBonus multiply(double multiplier) {
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return false;
    }

    @Override
    public SkillBonus<CurioSlotsBonus> merge(SkillBonus<?> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableComponent getTooltip() {
        Component slotDescription = Math.abs(this.amount) > 1 ? TooltipHelper.getSlotTooltip(this.slotName, "plural") : TooltipHelper.getSlotTooltip(this.slotName);
        MutableComponent tooltip = TooltipHelper.getSkillBonusTooltip(slotDescription, (double)this.amount, AttributeModifier.Operation.ADDITION);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.amount > 0;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<CurioSlotsBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.amount).setNumericResponder(value -> this.selectAmount(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, CuriosApi.getSlots().keySet()).setValue(this.slotName).setElementNameGetter(TooltipHelper::getSlotTooltip).setResponder(value -> this.selectSlotName(consumer, (String)value));
        editor.increaseHeight(19);
    }

    private void selectSlotName(Consumer<CurioSlotsBonus> consumer, String slotName) {
        this.setSlotName(slotName);
        consumer.accept(this.copy());
    }

    private void selectAmount(Consumer<CurioSlotsBonus> consumer, Double value) {
        this.setAmount(value.intValue());
        consumer.accept(this.copy());
    }

    public SkillBonus<?> setSlotName(String slotName) {
        this.slotName = slotName;
        return this;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public CurioSlotsBonus deserialize(JsonObject json) throws JsonParseException {
            String slotName = SerializationHelper.getElement(json, "slot").getAsString();
            int amount = SerializationHelper.getElement(json, "amount").getAsInt();
            String uuid = SerializationHelper.getElement(json, "modifier_id").getAsString();
            return new CurioSlotsBonus(slotName, amount, UUID.fromString(uuid));
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof CurioSlotsBonus)) {
                throw new IllegalArgumentException();
            }
            CurioSlotsBonus aBonus = (CurioSlotsBonus)bonus;
            json.addProperty("slot", aBonus.slotName);
            json.addProperty("amount", (Number)aBonus.amount);
            json.addProperty("modifier_id", aBonus.modifierId.toString());
        }

        @Override
        public CurioSlotsBonus deserialize(CompoundTag tag) {
            String slotName = tag.m_128461_("slot");
            int amount = tag.m_128451_("amount");
            String uuid = tag.m_128461_("modifier_id");
            return new CurioSlotsBonus(slotName, amount, UUID.fromString(uuid));
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof CurioSlotsBonus)) {
                throw new IllegalArgumentException();
            }
            CurioSlotsBonus aBonus = (CurioSlotsBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128359_("slot", aBonus.slotName);
            tag.m_128405_("amount", aBonus.amount);
            tag.m_128359_("modifier_id", aBonus.modifierId.toString());
            return tag;
        }

        @Override
        public CurioSlotsBonus deserialize(FriendlyByteBuf buf) {
            String slotName = buf.m_130277_();
            int amount = buf.readInt();
            String uuid = buf.m_130277_();
            return new CurioSlotsBonus(slotName, amount, UUID.fromString(uuid));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof CurioSlotsBonus)) {
                throw new IllegalArgumentException();
            }
            CurioSlotsBonus aBonus = (CurioSlotsBonus)bonus;
            buf.m_130070_(aBonus.slotName);
            buf.writeInt(aBonus.amount);
            buf.m_130070_(aBonus.modifierId.toString());
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new CurioSlotsBonus("ring", 1);
        }
    }
}

