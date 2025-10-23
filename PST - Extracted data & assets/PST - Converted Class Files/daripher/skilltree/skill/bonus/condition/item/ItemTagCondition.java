/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.common.Tags$Items
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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

public class ItemTagCondition
implements ItemCondition {
    private ResourceLocation tagId;

    public ItemTagCondition(ResourceLocation tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean met(ItemStack stack) {
        return stack.m_204117_(ItemTags.create((ResourceLocation)this.tagId));
    }

    @Override
    public String getDescriptionId() {
        return "item_tag.%s".formatted(new Object[]{this.tagId.toString()});
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ItemTagCondition that = (ItemTagCondition)o;
        return Objects.equals(this.tagId, that.tagId);
    }

    public int hashCode() {
        return Objects.hash(this.tagId);
    }

    @Override
    public ItemCondition.Serializer getSerializer() {
        return (ItemCondition.Serializer)PSTItemConditions.TAG.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemCondition> consumer) {
        editor.addLabel(0, 0, "Tag", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addTextField(0, 0, 200, 14, this.tagId.toString()).setSoftFilter(ResourceLocation::m_135830_).m_94151_(text -> this.selectTagId(consumer, (String)text));
        editor.increaseHeight(19);
    }

    private void selectTagId(Consumer<ItemCondition> consumer, String text) {
        this.setTagId(new ResourceLocation(text));
        consumer.accept(this);
    }

    public void setTagId(ResourceLocation tagId) {
        this.tagId = tagId;
    }

    public static class Serializer
    implements ItemCondition.Serializer {
        @Override
        public ItemCondition deserialize(JsonObject json) throws JsonParseException {
            ResourceLocation tagId = new ResourceLocation(json.get("tag_id").getAsString());
            return new ItemTagCondition(tagId);
        }

        @Override
        public void serialize(JsonObject json, ItemCondition condition) {
            if (!(condition instanceof ItemTagCondition)) {
                throw new IllegalArgumentException();
            }
            ItemTagCondition aCondition = (ItemTagCondition)condition;
            json.addProperty("tag_id", aCondition.tagId.toString());
        }

        @Override
        public ItemCondition deserialize(CompoundTag tag) {
            ResourceLocation tagId = new ResourceLocation(tag.m_128461_("tag_id"));
            return new ItemTagCondition(tagId);
        }

        @Override
        public CompoundTag serialize(ItemCondition condition) {
            if (!(condition instanceof ItemTagCondition)) {
                throw new IllegalArgumentException();
            }
            ItemTagCondition aCondition = (ItemTagCondition)condition;
            CompoundTag tag = new CompoundTag();
            tag.m_128359_("tag_id", aCondition.tagId.toString());
            return tag;
        }

        @Override
        public ItemCondition deserialize(FriendlyByteBuf buf) {
            ResourceLocation tagId = new ResourceLocation(buf.m_130277_());
            return new ItemTagCondition(tagId);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
            if (!(condition instanceof ItemTagCondition)) {
                throw new IllegalArgumentException();
            }
            ItemTagCondition aCondition = (ItemTagCondition)condition;
            buf.m_130070_(aCondition.tagId.toString());
        }

        @Override
        public ItemCondition createDefaultInstance() {
            return new ItemTagCondition(Tags.Items.ARMORS.f_203868_());
        }
    }
}

