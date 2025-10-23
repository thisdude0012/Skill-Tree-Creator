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
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.registries.ForgeRegistries
 */
package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.data.SkillTreeClientData;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public final class GrantItemBonus
implements SkillBonus<GrantItemBonus> {
    private ResourceLocation itemId;
    private int amount;

    public GrantItemBonus(ResourceLocation itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    @Override
    public void onSkillLearned(ServerPlayer player, boolean firstTime) {
        if (firstTime) {
            int amountLeft;
            Item item = (Item)ForgeRegistries.ITEMS.getValue(this.itemId);
            if (item == null) {
                SkillTreeClientData.printMessage("Unknown item: " + this.itemId, ChatFormatting.DARK_RED);
                return;
            }
            for (amountLeft = this.amount; amountLeft > 64; amountLeft -= 64) {
                player.m_36356_(new ItemStack((ItemLike)item, 64));
            }
            if (amountLeft > 0) {
                player.m_36356_(new ItemStack((ItemLike)item, amountLeft));
            }
        }
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.GRANT_ITEM.get();
    }

    public GrantItemBonus copy() {
        return new GrantItemBonus(this.itemId, this.amount);
    }

    @Override
    public GrantItemBonus multiply(double multiplier) {
        this.amount = (int)((double)this.amount * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof GrantItemBonus)) {
            return false;
        }
        GrantItemBonus otherBonus = (GrantItemBonus)other;
        return Objects.equals(otherBonus.itemId, this.itemId);
    }

    public GrantItemBonus merge(SkillBonus<?> other) {
        if (!(other instanceof GrantItemBonus)) {
            throw new IllegalArgumentException();
        }
        GrantItemBonus otherBonus = (GrantItemBonus)other;
        return new GrantItemBonus(this.itemId, this.amount + otherBonus.amount);
    }

    @Override
    public MutableComponent getTooltip() {
        Item item = (Item)ForgeRegistries.ITEMS.getValue(this.itemId);
        if (item == null) {
            return Component.m_237113_((String)("Unknown item: " + this.itemId)).m_130940_(ChatFormatting.DARK_RED);
        }
        Style style = TooltipHelper.getSkillBonusStyle(this.isPositive());
        Component itemDescription = item.m_41466_();
        if (this.amount > 1) {
            String amountDescription = TooltipHelper.formatNumber(this.amount);
            return Component.m_237110_((String)(this.getDescriptionId() + ".amount"), (Object[])new Object[]{amountDescription, itemDescription}).m_130948_(style);
        }
        return Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{itemDescription}).m_130948_(style);
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<GrantItemBonus> consumer) {
        editor.addLabel(0, 0, "Amount", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, this.amount).setNumericFilter(v -> v > 0.0 && v % 1.0 == 0.0).setNumericResponder(value -> this.selectAmount(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Item", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        List items = ForgeRegistries.ITEMS.getEntries().stream().map(Map.Entry::getKey).map(ResourceKey::m_135782_).toList();
        editor.addSelectionMenu(0, 0, 200, items).setValue(this.itemId).setElementNameGetter(id -> Component.m_237113_((String)id.toString())).setResponder(id -> this.selectItemId((ResourceLocation)id, consumer));
        editor.increaseHeight(19);
    }

    private void selectItemId(ResourceLocation id, Consumer<GrantItemBonus> consumer) {
        this.setItemId(id);
        consumer.accept(this.copy());
    }

    public void setItemId(ResourceLocation itemId) {
        this.itemId = itemId;
    }

    private void selectAmount(Consumer<GrantItemBonus> consumer, Double value) {
        this.setAmount(value.intValue());
        consumer.accept(this.copy());
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public GrantItemBonus deserialize(JsonObject json) throws JsonParseException {
            ResourceLocation itemId = new ResourceLocation(json.get("item_id").getAsString());
            int amount = SerializationHelper.getElement(json, "amount").getAsInt();
            return new GrantItemBonus(itemId, amount);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantItemBonus)) {
                throw new IllegalArgumentException();
            }
            GrantItemBonus aBonus = (GrantItemBonus)bonus;
            json.addProperty("item_id", aBonus.itemId.toString());
            json.addProperty("amount", (Number)aBonus.amount);
        }

        @Override
        public GrantItemBonus deserialize(CompoundTag tag) {
            ResourceLocation itemId = new ResourceLocation(tag.m_128461_("item_id"));
            int amount = tag.m_128451_("amount");
            return new GrantItemBonus(itemId, amount);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantItemBonus)) {
                throw new IllegalArgumentException();
            }
            GrantItemBonus aBonus = (GrantItemBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128359_("item_id", aBonus.itemId.toString());
            tag.m_128405_("amount", aBonus.amount);
            return tag;
        }

        @Override
        public GrantItemBonus deserialize(FriendlyByteBuf buf) {
            ResourceLocation itemId = buf.m_130281_();
            int duration = buf.readInt();
            return new GrantItemBonus(itemId, duration);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof GrantItemBonus)) {
                throw new IllegalArgumentException();
            }
            GrantItemBonus aBonus = (GrantItemBonus)bonus;
            buf.m_130085_(aBonus.itemId);
            buf.writeInt(aBonus.amount);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new GrantItemBonus(ForgeRegistries.ITEMS.getKey((Object)Items.f_42415_), 64);
        }
    }
}

