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
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParam
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 */
package daripher.skilltree.skill.bonus.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.SelectionList;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.init.PSTSkillBonuses;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class LootDuplicationBonus
implements SkillBonus<LootDuplicationBonus> {
    private LootType lootType;
    private float multiplier;
    private float chance;

    public LootDuplicationBonus(float chance, float multiplier, LootType lootType) {
        this.chance = chance;
        this.multiplier = multiplier;
        this.lootType = lootType;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.LOOT_DUPLICATION.get();
    }

    public LootDuplicationBonus copy() {
        return new LootDuplicationBonus(this.chance, this.multiplier, this.lootType);
    }

    @Override
    public LootDuplicationBonus multiply(double multiplier) {
        this.chance = (float)((double)this.chance * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof LootDuplicationBonus)) {
            return false;
        }
        LootDuplicationBonus otherBonus = (LootDuplicationBonus)other;
        if (otherBonus.multiplier != this.multiplier) {
            return false;
        }
        return Objects.equals((Object)otherBonus.lootType, (Object)this.lootType);
    }

    @Override
    public SkillBonus<LootDuplicationBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof LootDuplicationBonus)) {
            throw new IllegalArgumentException();
        }
        LootDuplicationBonus otherBonus = (LootDuplicationBonus)other;
        return new LootDuplicationBonus(otherBonus.chance + this.chance, this.multiplier, this.lootType);
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent bonusDescription;
        MutableComponent multiplierDescription;
        MutableComponent lootDescription = Component.m_237115_((String)this.lootType.getDescriptionId());
        String descriptionId = this.getDescriptionId();
        if (this.multiplier == 1.0f) {
            multiplierDescription = Component.m_237115_((String)(descriptionId + ".double"));
        } else if (this.multiplier == 2.0f) {
            multiplierDescription = Component.m_237115_((String)(descriptionId + ".triple"));
        } else {
            String formattedMultiplier = ItemStack.f_41584_.format(this.multiplier * 100.0f);
            multiplierDescription = Component.m_237110_((String)(descriptionId + ".multiplier"), (Object[])new Object[]{formattedMultiplier});
        }
        if (this.chance < 1.0f) {
            bonusDescription = Component.m_237110_((String)descriptionId, (Object[])new Object[]{multiplierDescription, lootDescription});
            bonusDescription = TooltipHelper.getSkillBonusTooltip((Component)bonusDescription, (double)this.chance, AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            bonusDescription = Component.m_237110_((String)(descriptionId + ".guaranteed"), (Object[])new Object[]{multiplierDescription, lootDescription});
        }
        return bonusDescription.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.chance > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<LootDuplicationBonus> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.addLabel(110, 0, "Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, this.chance).setNumericResponder(value -> this.selectChance(consumer, (Double)value));
        editor.addNumericTextField(110, 0, 90, 14, this.multiplier).setNumericResponder(value -> this.selectMultiplier(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Loot Type", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        SelectionList<LootType> lootTypeSelection = editor.addSelection(0, 0, 200, 3, this.lootType).setNameGetter(LootType::getFormattedName).setResponder(lootType -> this.selectLootType(consumer, (LootType)((Object)lootType)));
        editor.increaseHeight(lootTypeSelection.getMaxDisplayed() * 14 + 5);
    }

    private void selectLootType(Consumer<LootDuplicationBonus> consumer, LootType lootType) {
        this.setLootType(lootType);
        consumer.accept(this.copy());
    }

    private void selectMultiplier(Consumer<LootDuplicationBonus> consumer, Double value) {
        this.setMultiplier(value.floatValue());
        consumer.accept(this.copy());
    }

    private void selectChance(Consumer<LootDuplicationBonus> consumer, Double value) {
        this.setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public void setLootType(LootType lootType) {
        this.lootType = lootType;
    }

    public float getChance() {
        return this.chance;
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public LootType getLootType() {
        return this.lootType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LootDuplicationBonus that = (LootDuplicationBonus)o;
        if (Float.compare(this.multiplier, that.multiplier) != 0) {
            return false;
        }
        if (Float.compare(this.chance, that.chance) != 0) {
            return false;
        }
        return this.lootType == that.lootType;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.lootType, Float.valueOf(this.multiplier), Float.valueOf(this.chance)});
    }

    public static enum LootType {
        MOBS("mobs"),
        FISHING("fishing"),
        GEMS("gems"),
        CHESTS("chests"),
        ORE("ore"),
        ARCHAEOLOGY("archaeology");

        final String name;

        public boolean canAffect(LootContext lootContext) {
            LootContextParam<Entity> playerLootContextParam = this.getPlayerLootContextParam();
            if (!lootContext.m_78936_(playerLootContextParam)) {
                return false;
            }
            if (!(lootContext.m_165124_(playerLootContextParam) instanceof Player)) {
                return false;
            }
            ResourceLocation lootTableId = lootContext.getQueriedLootTableId();
            String lootTableName = lootTableId.toString();
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case MOBS -> lootTableName.contains("entities/");
                case FISHING -> lootTableName.contains("fishing");
                case GEMS -> lootTableName.contains("gems");
                case CHESTS -> lootTableName.contains("chests/");
                case ORE -> {
                    if (lootTableName.contains("blocks/") && lootTableName.contains("_ore")) {
                        yield true;
                    }
                    yield false;
                }
                case ARCHAEOLOGY -> lootTableName.contains("archaeology/");
            };
        }

        public LootContextParam<Entity> getPlayerLootContextParam() {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case MOBS, FISHING -> LootContextParams.f_81458_;
                case GEMS, CHESTS, ORE, ARCHAEOLOGY -> LootContextParams.f_81455_;
            };
        }

        private LootType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public Component getFormattedName() {
            String firstLetter = this.getName().substring(0, 1);
            return Component.m_237113_((String)(firstLetter.toUpperCase() + this.getName().substring(1)));
        }

        public static LootType byName(String name) {
            for (LootType type : LootType.values()) {
                if (!type.name.equals(name)) continue;
                return type;
            }
            return MOBS;
        }

        public String getDescriptionId() {
            return "loot.type." + this.getName();
        }
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public LootDuplicationBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
            LootType lootType = LootType.byName(json.get("loot_type").getAsString());
            return new LootDuplicationBonus(chance, multiplier, lootType);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof LootDuplicationBonus)) {
                throw new IllegalArgumentException();
            }
            LootDuplicationBonus aBonus = (LootDuplicationBonus)bonus;
            json.addProperty("chance", (Number)Float.valueOf(aBonus.chance));
            json.addProperty("multiplier", (Number)Float.valueOf(aBonus.multiplier));
            json.addProperty("loot_type", aBonus.lootType.name);
        }

        @Override
        public LootDuplicationBonus deserialize(CompoundTag tag) {
            float chance = tag.m_128457_("chance");
            float multiplier = tag.m_128457_("multiplier");
            LootType lootType = LootType.byName(tag.m_128461_("loot_type"));
            return new LootDuplicationBonus(chance, multiplier, lootType);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof LootDuplicationBonus)) {
                throw new IllegalArgumentException();
            }
            LootDuplicationBonus aBonus = (LootDuplicationBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("chance", aBonus.chance);
            tag.m_128350_("multiplier", aBonus.multiplier);
            tag.m_128359_("loot_type", aBonus.lootType.name);
            return tag;
        }

        @Override
        public LootDuplicationBonus deserialize(FriendlyByteBuf buf) {
            return new LootDuplicationBonus(buf.readFloat(), buf.readFloat(), LootType.byName(buf.m_130277_()));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof LootDuplicationBonus)) {
                throw new IllegalArgumentException();
            }
            LootDuplicationBonus aBonus = (LootDuplicationBonus)bonus;
            buf.writeFloat(aBonus.chance);
            buf.writeFloat(aBonus.multiplier);
            buf.m_130070_(aBonus.lootType.name);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new LootDuplicationBonus(0.05f, 1.0f, LootType.MOBS);
        }
    }
}

