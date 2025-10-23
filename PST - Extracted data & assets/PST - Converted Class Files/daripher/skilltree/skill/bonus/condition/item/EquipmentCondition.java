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
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.AxeItem
 *  net.minecraft.world.item.BowItem
 *  net.minecraft.world.item.CrossbowItem
 *  net.minecraft.world.item.DiggerItem
 *  net.minecraft.world.item.HoeItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.PickaxeItem
 *  net.minecraft.world.item.PotionItem
 *  net.minecraft.world.item.ShieldItem
 *  net.minecraft.world.item.ShovelItem
 *  net.minecraft.world.item.SwordItem
 *  net.minecraft.world.item.TridentItem
 *  net.minecraftforge.common.Tags$Items
 *  net.minecraftforge.registries.ForgeRegistries
 */
package daripher.skilltree.skill.bonus.condition.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTItemConditions;
import daripher.skilltree.init.PSTTags;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class EquipmentCondition
implements ItemCondition {
    public Type type;

    public EquipmentCondition(Type type) {
        this.type = type;
    }

    @Override
    public boolean met(ItemStack stack) {
        return switch (this.type) {
            case Type.ARMOR -> EquipmentCondition.isArmor(stack);
            case Type.AXE -> EquipmentCondition.isAxe(stack);
            case Type.BOOTS -> EquipmentCondition.isBoots(stack);
            case Type.BOW -> EquipmentCondition.isBow(stack);
            case Type.HOE -> EquipmentCondition.isHoe(stack);
            case Type.TOOL -> EquipmentCondition.isTool(stack);
            case Type.SWORD -> EquipmentCondition.isSword(stack);
            case Type.HELMET -> EquipmentCondition.isHelmet(stack);
            case Type.SHIELD -> EquipmentCondition.isShield(stack);
            case Type.SHOVEL -> EquipmentCondition.isShovel(stack);
            case Type.CHESTPLATE -> EquipmentCondition.isChestplate(stack);
            case Type.WEAPON -> EquipmentCondition.isWeapon(stack);
            case Type.CROSSBOW -> EquipmentCondition.isCrossbow(stack);
            case Type.PICKAXE -> EquipmentCondition.isPickaxe(stack);
            case Type.TRIDENT -> EquipmentCondition.isTrident(stack);
            case Type.LEGGINGS -> EquipmentCondition.isLeggings(stack);
            case Type.MELEE_WEAPON -> EquipmentCondition.isMeleeWeapon(stack);
            case Type.RANGED_WEAPON -> EquipmentCondition.isRangedWeapon(stack);
            default -> EquipmentCondition.isEquipment(stack);
        };
    }

    public static boolean isEquipment(ItemStack stack) {
        return EquipmentCondition.isArmor(stack) || EquipmentCondition.isWeapon(stack) || EquipmentCondition.isShield(stack) || EquipmentCondition.isTool(stack);
    }

    public static boolean isRangedWeapon(ItemStack stack) {
        return EquipmentCondition.isCrossbow(stack) || EquipmentCondition.isBow(stack) || stack.m_204117_(PSTTags.Items.RANGED_WEAPON);
    }

    public static boolean isMeleeWeapon(ItemStack stack) {
        return EquipmentCondition.isSword(stack) || EquipmentCondition.isAxe(stack) || EquipmentCondition.isTrident(stack) || stack.m_204117_(PSTTags.Items.MELEE_WEAPON);
    }

    public static boolean isLeggings(ItemStack stack) {
        ArmorItem armor;
        Item item = stack.m_41720_();
        return item instanceof ArmorItem && (armor = (ArmorItem)item).m_40402_() == EquipmentSlot.LEGS || stack.m_204117_(Tags.Items.ARMORS_LEGGINGS);
    }

    public static boolean isTrident(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_single")) {
            return true;
        }
        return stack.m_41720_() instanceof TridentItem || stack.m_204117_(Tags.Items.TOOLS_TRIDENTS);
    }

    public static boolean isPickaxe(ItemStack stack) {
        return stack.m_41720_() instanceof PickaxeItem || stack.m_204117_(ItemTags.f_271360_);
    }

    public static boolean isCrossbow(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_crossbow")) {
            return true;
        }
        return stack.m_41720_() instanceof CrossbowItem || stack.m_204117_(Tags.Items.TOOLS_CROSSBOWS);
    }

    public static boolean isWeapon(ItemStack stack) {
        return EquipmentCondition.isMeleeWeapon(stack) || EquipmentCondition.isRangedWeapon(stack);
    }

    public static boolean isPotion(ItemStack stack) {
        return stack.m_41720_() instanceof PotionItem;
    }

    public static boolean isChestplate(ItemStack stack) {
        ArmorItem armor;
        Item item = stack.m_41720_();
        return item instanceof ArmorItem && (armor = (ArmorItem)item).m_40402_() == EquipmentSlot.CHEST || stack.m_204117_(Tags.Items.ARMORS_CHESTPLATES);
    }

    public static boolean isShovel(ItemStack stack) {
        return stack.m_41720_() instanceof ShovelItem || stack.m_204117_(ItemTags.f_271138_);
    }

    public static boolean isShield(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_shield")) {
            return true;
        }
        return stack.m_41720_() instanceof ShieldItem || stack.m_204117_(Tags.Items.TOOLS_SHIELDS);
    }

    public static boolean isHelmet(ItemStack stack) {
        ArmorItem armor;
        Item item = stack.m_41720_();
        return item instanceof ArmorItem && (armor = (ArmorItem)item).m_40402_() == EquipmentSlot.HEAD || stack.m_204117_(Tags.Items.ARMORS_HELMETS);
    }

    public static boolean isSword(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_sword")) {
            return true;
        }
        return stack.m_41720_() instanceof SwordItem || stack.m_204117_(ItemTags.f_271388_);
    }

    public static boolean isTool(ItemStack stack) {
        return stack.m_41720_() instanceof DiggerItem || stack.m_204117_(Tags.Items.TOOLS);
    }

    public static boolean isHoe(ItemStack stack) {
        return stack.m_41720_() instanceof HoeItem || stack.m_204117_(ItemTags.f_271298_);
    }

    public static boolean isBow(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        if (Objects.requireNonNull(id).toString().equals("tetra:modular_bow")) {
            return true;
        }
        return stack.m_41720_() instanceof BowItem || stack.m_204117_(Tags.Items.TOOLS_BOWS);
    }

    public static boolean isBoots(ItemStack stack) {
        ArmorItem armor;
        Item item = stack.m_41720_();
        return item instanceof ArmorItem && (armor = (ArmorItem)item).m_40402_() == EquipmentSlot.FEET || stack.m_204117_(Tags.Items.ARMORS_BOOTS);
    }

    public static boolean isAxe(ItemStack stack) {
        return stack.m_41720_() instanceof AxeItem || stack.m_204117_(ItemTags.f_271207_);
    }

    public static boolean isArmor(ItemStack stack) {
        return EquipmentCondition.isHelmet(stack) || EquipmentCondition.isBoots(stack) || EquipmentCondition.isChestplate(stack) || EquipmentCondition.isLeggings(stack);
    }

    @Override
    public String getDescriptionId() {
        return ItemCondition.super.getDescriptionId() + "." + this.type.name().toLowerCase();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EquipmentCondition that = (EquipmentCondition)o;
        return Objects.equals((Object)this.type, (Object)that.type);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type});
    }

    @Override
    public ItemCondition.Serializer getSerializer() {
        return (ItemCondition.Serializer)PSTItemConditions.EQUIPMENT_TYPE.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemCondition> consumer) {
        editor.addLabel(0, 0, "Type", ChatFormatting.GREEN);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.type).setResponder(type -> this.selectEquipmentType(consumer, (Type)((Object)type))).setElementNameGetter(Type::getName);
        editor.increaseHeight(19);
    }

    private void selectEquipmentType(Consumer<ItemCondition> consumer, Type type) {
        this.setType(type);
        consumer.accept(this);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        ANY,
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        ARMOR,
        SHIELD,
        WEAPON,
        SWORD,
        AXE,
        TRIDENT,
        MELEE_WEAPON,
        BOW,
        CROSSBOW,
        RANGED_WEAPON,
        PICKAXE,
        HOE,
        SHOVEL,
        TOOL;


        public Component getName() {
            return Component.m_237113_((String)TooltipHelper.idToName(this.name().toLowerCase()));
        }
    }

    public static class Serializer
    implements ItemCondition.Serializer {
        @Override
        public ItemCondition deserialize(JsonObject json) throws JsonParseException {
            Type type = Type.valueOf(json.get("equipment_type").getAsString().toUpperCase());
            return new EquipmentCondition(type);
        }

        @Override
        public void serialize(JsonObject json, ItemCondition condition) {
            if (!(condition instanceof EquipmentCondition)) {
                throw new IllegalArgumentException();
            }
            EquipmentCondition aCondition = (EquipmentCondition)condition;
            json.addProperty("equipment_type", aCondition.type.name().toLowerCase());
        }

        @Override
        public ItemCondition deserialize(CompoundTag tag) {
            Type type = Type.valueOf(tag.m_128461_("equipment_type").toUpperCase());
            return new EquipmentCondition(type);
        }

        @Override
        public CompoundTag serialize(ItemCondition condition) {
            if (!(condition instanceof EquipmentCondition)) {
                throw new IllegalArgumentException();
            }
            EquipmentCondition aCondition = (EquipmentCondition)condition;
            CompoundTag tag = new CompoundTag();
            tag.m_128359_("equipment_type", aCondition.type.name().toLowerCase());
            return tag;
        }

        @Override
        public ItemCondition deserialize(FriendlyByteBuf buf) {
            return new EquipmentCondition(Type.values()[buf.readInt()]);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, ItemCondition condition) {
            if (!(condition instanceof EquipmentCondition)) {
                throw new IllegalArgumentException();
            }
            EquipmentCondition aCondition = (EquipmentCondition)condition;
            buf.writeInt(aCondition.type.ordinal());
        }

        @Override
        public ItemCondition createDefaultInstance() {
            return new EquipmentCondition(Type.ANY);
        }
    }
}

