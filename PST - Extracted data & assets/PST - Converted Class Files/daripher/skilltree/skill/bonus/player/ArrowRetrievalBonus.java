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

public final class ArrowRetrievalBonus
implements SkillBonus<ArrowRetrievalBonus> {
    private float chance;

    public ArrowRetrievalBonus(float chance) {
        this.chance = chance;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.ARROW_RETRIEVAL.get();
    }

    public ArrowRetrievalBonus copy() {
        return new ArrowRetrievalBonus(this.chance);
    }

    @Override
    public ArrowRetrievalBonus multiply(double multiplier) {
        return new ArrowRetrievalBonus((float)((double)this.getChance() * multiplier));
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        return other instanceof ArrowRetrievalBonus;
    }

    @Override
    public SkillBonus<ArrowRetrievalBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ArrowRetrievalBonus)) {
            throw new IllegalArgumentException();
        }
        ArrowRetrievalBonus otherBonus = (ArrowRetrievalBonus)other;
        return new ArrowRetrievalBonus(otherBonus.chance + this.chance);
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
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<ArrowRetrievalBonus> consumer) {
        editor.addLabel(0, 0, "Chance", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.chance).setNumericResponder(value -> this.selectChance(consumer, (Double)value));
        editor.increaseHeight(19);
    }

    private void selectChance(Consumer<ArrowRetrievalBonus> consumer, Double value) {
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
        ArrowRetrievalBonus that = (ArrowRetrievalBonus)obj;
        return Float.floatToIntBits(this.chance) == Float.floatToIntBits(that.chance);
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.chance));
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public ArrowRetrievalBonus deserialize(JsonObject json) throws JsonParseException {
            float chance = SerializationHelper.getElement(json, "chance").getAsFloat();
            return new ArrowRetrievalBonus(chance);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ArrowRetrievalBonus)) {
                throw new IllegalArgumentException();
            }
            ArrowRetrievalBonus aBonus = (ArrowRetrievalBonus)bonus;
            json.addProperty("chance", (Number)Float.valueOf(aBonus.chance));
        }

        @Override
        public ArrowRetrievalBonus deserialize(CompoundTag tag) {
            float chance = tag.m_128457_("chance");
            return new ArrowRetrievalBonus(chance);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ArrowRetrievalBonus)) {
                throw new IllegalArgumentException();
            }
            ArrowRetrievalBonus aBonus = (ArrowRetrievalBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("chance", aBonus.chance);
            return tag;
        }

        @Override
        public ArrowRetrievalBonus deserialize(FriendlyByteBuf buf) {
            return new ArrowRetrievalBonus(buf.readFloat());
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ArrowRetrievalBonus)) {
                throw new IllegalArgumentException();
            }
            ArrowRetrievalBonus aBonus = (ArrowRetrievalBonus)bonus;
            buf.writeFloat(aBonus.chance);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ArrowRetrievalBonus(0.05f);
        }
    }
}

