/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
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
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class FreeEnchantmentBonus
implements SkillBonus<FreeEnchantmentBonus> {
    private float chance;

    public FreeEnchantmentBonus(float chance) {
        this.chance = chance;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.FREE_ENCHANTMENT.get();
    }

    public FreeEnchantmentBonus copy() {
        return new FreeEnchantmentBonus(this.chance);
    }

    @Override
    public FreeEnchantmentBonus multiply(double multiplier) {
        return new FreeEnchantmentBonus((float)((double)this.getChance() * multiplier));
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return other instanceof FreeEnchantmentBonus;
    }

    @Override
    public SkillBonus<FreeEnchantmentBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof FreeEnchantmentBonus)) {
            throw new IllegalArgumentException();
        }
        FreeEnchantmentBonus otherBonus = (FreeEnchantmentBonus)other;
        return new FreeEnchantmentBonus(otherBonus.chance + this.chance);
    }

    @Override
    public MutableComponent getTooltip() {
        return TooltipHelper.getSkillBonusTooltip(this.getDescriptionId(), (double)this.chance, AttributeModifier.Operation.MULTIPLY_BASE).m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.chance > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<FreeEnchantmentBonus> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.chance).setNumericResponder(value -> this.selectChance(consumer, (Double)value));
        editor.increaseHeight(19);
    }

    private void selectChance(Consumer<FreeEnchantmentBonus> consumer, Double value) {
        this.setChance(value.floatValue());
        consumer.accept(this.copy());
    }

    public void setChance(float chance) {
        this.chance = chance;
    }

    public float getChance() {
        return this.chance;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        FreeEnchantmentBonus that = (FreeEnchantmentBonus)obj;
        return Float.floatToIntBits(this.chance) == Float.floatToIntBits(that.chance);
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.chance));
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public FreeEnchantmentBonus deserialize(JsonObject json) throws JsonParseException {
            float multiplier = SerializationHelper.getElement(json, "chance").getAsFloat();
            return new FreeEnchantmentBonus(multiplier);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof FreeEnchantmentBonus)) {
                throw new IllegalArgumentException();
            }
            FreeEnchantmentBonus aBonus = (FreeEnchantmentBonus)bonus;
            json.addProperty("chance", (Number)Float.valueOf(aBonus.chance));
        }

        @Override
        public FreeEnchantmentBonus deserialize(CompoundTag tag) {
            float multiplier = tag.m_128457_("chance");
            return new FreeEnchantmentBonus(multiplier);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof FreeEnchantmentBonus)) {
                throw new IllegalArgumentException();
            }
            FreeEnchantmentBonus aBonus = (FreeEnchantmentBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("chance", aBonus.chance);
            return tag;
        }

        @Override
        public FreeEnchantmentBonus deserialize(FriendlyByteBuf buf) {
            return new FreeEnchantmentBonus(buf.readFloat());
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof FreeEnchantmentBonus)) {
                throw new IllegalArgumentException();
            }
            FreeEnchantmentBonus aBonus = (FreeEnchantmentBonus)bonus;
            buf.writeFloat(aBonus.chance);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new FreeEnchantmentBonus(0.05f);
        }
    }
}

