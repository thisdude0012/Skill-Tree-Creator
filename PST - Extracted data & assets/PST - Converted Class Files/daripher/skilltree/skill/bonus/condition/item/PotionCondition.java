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
 *  net.minecraft.world.effect.MobEffectCategory
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.PotionItem
 *  net.minecraft.world.item.alchemy.PotionUtils
 */
package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;

public final class PotionCondition
implements ItemCondition {
    private Type type;

    public PotionCondition(Type type) {
        this.type = type;
    }

    @Override
    public boolean met(ItemStack stack) {
        if (!(stack.m_41720_() instanceof PotionItem)) {
            return false;
        }
        return switch (this.type) {
            default -> throw new IncompatibleClassChangeError();
            case Type.ANY -> true;
            case Type.NEUTRAL -> this.hasEffects(stack, MobEffectCategory.NEUTRAL);
            case Type.HARMFUL -> this.hasEffects(stack, MobEffectCategory.HARMFUL);
            case Type.BENEFICIAL -> this.hasEffects(stack, MobEffectCategory.BENEFICIAL);
        };
    }

    private boolean hasEffects(ItemStack stack, MobEffectCategory category) {
        return PotionUtils.m_43566_((CompoundTag)stack.m_41784_()).stream().map(MobEffectInstance::m_19544_).anyMatch(effect -> effect.m_19483_() == category);
    }

    @Override
    public String getDescriptionId() {
        return "%s.%s".formatted(new Object[]{ItemCondition.super.getDescriptionId(), this.type.getName()});
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PotionCondition that = (PotionCondition)o;
        return this.type == that.type;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type});
    }

    @Override
    public ItemCondition.Serializer getSerializer() {
        return (ItemCondition.Serializer)PSTItemConditions.POTIONS.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemCondition> consumer) {
        editor.addLabel(0, 0, "Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelection(0, 0, 200, 1, this.type).setNameGetter(Type::getFormattedName).setResponder(type -> this.selectPotionType(consumer, (Type)((Object)type)));
        editor.increaseHeight(19);
    }

    private void selectPotionType(Consumer<ItemCondition> consumer, Type type) {
        this.setType(type);
        consumer.accept(this);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        HARMFUL("harmful"),
        NEUTRAL("neutral"),
        BENEFICIAL("beneficial"),
        ANY("any");

        final String name;

        private Type(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public Component getFormattedName() {
            return Component.m_237113_((String)(this.getName().substring(0, 1).toUpperCase() + this.getName().substring(1)));
        }

        public static Type byName(String name) {
            for (Type type : Type.values()) {
                if (!type.name.equals(name)) continue;
                return type;
            }
            return ANY;
        }
    }

    public static class Serializer
    implements ItemCondition.Serializer {
        @Override
        public ItemCondition deserialize(JsonObject json) throws JsonParseException {
            return new PotionCondition(SerializationHelper.deserializePotionType(json));
        }

        @Override
        public void serialize(JsonObject json, ItemCondition condition) {
            if (!(condition instanceof PotionCondition)) {
                throw new IllegalArgumentException();
            }
            PotionCondition aCondition = (PotionCondition)condition;
            SerializationHelper.serializePotionType(json, aCondition.type);
        }

        @Override
        public ItemCondition deserialize(CompoundTag tag) {
            return new PotionCondition(SerializationHelper.deserializePotionType(tag));
        }

        @Override
        public CompoundTag serialize(ItemCondition condition) {
            if (!(condition instanceof PotionCondition)) {
                throw new IllegalArgumentException();
            }
            PotionCondition aCondition = (PotionCondition)condition;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializePotionType(tag, aCondition.type);
            return tag;
        }

        @Override
        public ItemCondition deserialize(FriendlyByteBuf buf) {
            return new PotionCondition(NetworkHelper.readEnum(buf, Type.class));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
            if (!(condition instanceof PotionCondition)) {
                throw new IllegalArgumentException();
            }
            PotionCondition aCondition = (PotionCondition)condition;
            NetworkHelper.writeEnum(buf, aCondition.type);
        }

        @Override
        public ItemCondition createDefaultInstance() {
            return new PotionCondition(Type.ANY);
        }
    }
}

