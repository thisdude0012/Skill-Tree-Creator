/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.registries.ForgeRegistries
 */
package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class ItemIdCondition
implements ItemCondition {
    private ResourceLocation id;

    public ItemIdCondition(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean met(ItemStack stack) {
        return ForgeRegistries.ITEMS.getValue(this.id) == stack.m_41720_();
    }

    @Override
    public String getDescriptionId() {
        Item item = (Item)ForgeRegistries.ITEMS.getValue(this.id);
        if (item != null) {
            return item.m_5524_();
        }
        return ItemCondition.super.getDescriptionId();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ItemIdCondition that = (ItemIdCondition)o;
        return this.id.equals((Object)that.id);
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public ItemCondition.Serializer getSerializer() {
        return (ItemCondition.Serializer)PSTItemConditions.ITEM_ID.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemCondition> consumer) {
        editor.addLabel(0, 0, "Item Id", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addTextField(0, 0, 200, 14, this.id.toString()).setSoftFilter(ItemIdCondition::isItemId).m_94151_(text -> this.selectItemId(consumer, (String)text));
        editor.increaseHeight(19);
    }

    private void selectItemId(Consumer<ItemCondition> consumer, String text) {
        this.setId(new ResourceLocation(text));
        consumer.accept(this);
    }

    private static boolean isItemId(String text) {
        if (!ResourceLocation.m_135830_((String)text)) {
            return false;
        }
        return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(text));
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    public static class Serializer
    implements ItemCondition.Serializer {
        @Override
        public ItemCondition deserialize(JsonObject json) throws JsonParseException {
            ResourceLocation id = new ResourceLocation(json.get("id").getAsString());
            return new ItemIdCondition(id);
        }

        @Override
        public void serialize(JsonObject json, ItemCondition condition) {
            if (!(condition instanceof ItemIdCondition)) {
                throw new IllegalArgumentException();
            }
            ItemIdCondition aCondition = (ItemIdCondition)condition;
            json.addProperty("id", aCondition.id.toString());
        }

        @Override
        public ItemCondition deserialize(CompoundTag tag) {
            Tag idTag = tag.m_128423_("id");
            Objects.requireNonNull(idTag);
            ResourceLocation id = new ResourceLocation(idTag.m_7916_());
            return new ItemIdCondition(id);
        }

        @Override
        public CompoundTag serialize(ItemCondition condition) {
            if (!(condition instanceof ItemIdCondition)) {
                throw new IllegalArgumentException();
            }
            ItemIdCondition aCondition = (ItemIdCondition)condition;
            CompoundTag tag = new CompoundTag();
            tag.m_128359_("id", aCondition.id.toString());
            return tag;
        }

        @Override
        public ItemCondition deserialize(FriendlyByteBuf buf) {
            return new ItemIdCondition(new ResourceLocation(buf.m_130277_()));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
            if (!(condition instanceof ItemIdCondition)) {
                throw new IllegalArgumentException();
            }
            ItemIdCondition aCondition = (ItemIdCondition)condition;
            buf.m_130070_(aCondition.id.toString());
        }

        @Override
        public ItemCondition createDefaultInstance() {
            return new ItemIdCondition(new ResourceLocation("minecraft:shield"));
        }
    }
}

