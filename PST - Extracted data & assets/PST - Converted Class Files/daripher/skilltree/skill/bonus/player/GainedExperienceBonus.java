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
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
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
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class GainedExperienceBonus
implements SkillBonus<GainedExperienceBonus> {
    private ExperienceSource experienceSource;
    private float multiplier;
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;

    public GainedExperienceBonus(float multiplier, ExperienceSource source) {
        this.multiplier = multiplier;
        this.experienceSource = source;
    }

    public GainedExperienceBonus(float multiplier, ExperienceSource source, LivingMultiplier playerMultiplier) {
        this.multiplier = multiplier;
        this.experienceSource = source;
        this.playerMultiplier = playerMultiplier;
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.GAINED_EXPERIENCE.get();
    }

    public GainedExperienceBonus copy() {
        return new GainedExperienceBonus(this.multiplier, this.experienceSource, this.playerMultiplier);
    }

    @Override
    public GainedExperienceBonus multiply(double multiplier) {
        this.multiplier = (float)((double)this.multiplier * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof GainedExperienceBonus)) {
            return false;
        }
        GainedExperienceBonus otherBonus = (GainedExperienceBonus)other;
        if (!Objects.equals((Object)otherBonus.experienceSource, (Object)this.experienceSource)) {
            return false;
        }
        return Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier);
    }

    @Override
    public SkillBonus<GainedExperienceBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof GainedExperienceBonus)) {
            throw new IllegalArgumentException();
        }
        GainedExperienceBonus otherBonus = (GainedExperienceBonus)other;
        return new GainedExperienceBonus(otherBonus.multiplier + this.multiplier, this.experienceSource, this.playerMultiplier);
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent sourceDescription = Component.m_237115_((String)this.experienceSource.getDescriptionId());
        MutableComponent tooltip = Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{sourceDescription});
        tooltip = TooltipHelper.getSkillBonusTooltip((Component)tooltip, (double)this.multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
        tooltip = this.playerMultiplier.getTooltip(tooltip, SkillBonus.Target.PLAYER);
        return tooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.multiplier > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int row, Consumer<GainedExperienceBonus> consumer) {
        editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
        editor.addLabel(110, 0, "Source", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 90, 14, this.multiplier).setNumericResponder(value -> this.selectMultiplier(consumer, (Double)value));
        editor.addSelection(110, 0, 90, 1, this.experienceSource).setNameGetter(ExperienceSource::getFormattedName).setResponder(experienceSource -> this.selectExperienceSource(consumer, (ExperienceSource)((Object)experienceSource)));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerMultiplier).setResponder(multiplier -> this.selectPlayerMultiplier(editor, consumer, (LivingMultiplier)multiplier)).setMenuInitFunc(() -> this.addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<GainedExperienceBonus> consumer, LivingMultiplier playerMultiplier) {
        this.setPlayerMultiplier(playerMultiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectExperienceSource(Consumer<GainedExperienceBonus> consumer, ExperienceSource experienceSource) {
        this.setExpericenSource(experienceSource);
        consumer.accept(this.copy());
    }

    private void selectMultiplier(Consumer<GainedExperienceBonus> consumer, Double value) {
        this.setMultiplier(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<GainedExperienceBonus> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, m -> {
            this.setPlayerMultiplier((LivingMultiplier)m);
            consumer.accept(this.copy());
        });
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public void setExpericenSource(ExperienceSource experienceSource) {
        this.experienceSource = experienceSource;
    }

    public SkillBonus<?> setPlayerMultiplier(LivingMultiplier playerMultiplier) {
        this.playerMultiplier = playerMultiplier;
        return this;
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public ExperienceSource getSource() {
        return this.experienceSource;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GainedExperienceBonus that = (GainedExperienceBonus)o;
        if (Float.compare(this.multiplier, that.multiplier) != 0) {
            return false;
        }
        return this.experienceSource == that.experienceSource;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.experienceSource, Float.valueOf(this.multiplier)});
    }

    public static enum ExperienceSource {
        MOBS("mobs"),
        FISHING("fishing"),
        ORE("ore");

        final String name;

        private ExperienceSource(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public Component getFormattedName() {
            return Component.m_237113_((String)(this.getName().substring(0, 1).toUpperCase() + this.getName().substring(1)));
        }

        public static ExperienceSource byName(String name) {
            for (ExperienceSource type : ExperienceSource.values()) {
                if (!type.name.equals(name)) continue;
                return type;
            }
            return MOBS;
        }

        public String getDescriptionId() {
            return "experience.source." + this.getName();
        }
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public GainedExperienceBonus deserialize(JsonObject json) throws JsonParseException {
            float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
            ExperienceSource experienceSource = ExperienceSource.byName(json.get("experience_source").getAsString());
            LivingMultiplier playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            return new GainedExperienceBonus(multiplier, experienceSource, playerMultiplier);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof GainedExperienceBonus)) {
                throw new IllegalArgumentException();
            }
            GainedExperienceBonus aBonus = (GainedExperienceBonus)bonus;
            json.addProperty("multiplier", (Number)Float.valueOf(aBonus.multiplier));
            json.addProperty("experience_source", aBonus.experienceSource.name);
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
        }

        @Override
        public GainedExperienceBonus deserialize(CompoundTag tag) {
            float multiplier = tag.m_128457_("multiplier");
            ExperienceSource experienceSource = ExperienceSource.byName(tag.m_128461_("experience_source"));
            LivingMultiplier playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            return new GainedExperienceBonus(multiplier, experienceSource, playerMultiplier);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof GainedExperienceBonus)) {
                throw new IllegalArgumentException();
            }
            GainedExperienceBonus aBonus = (GainedExperienceBonus)bonus;
            CompoundTag tag = new CompoundTag();
            tag.m_128350_("multiplier", aBonus.multiplier);
            tag.m_128359_("experience_source", aBonus.experienceSource.name);
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            return tag;
        }

        @Override
        public GainedExperienceBonus deserialize(FriendlyByteBuf buf) {
            float multiplier = buf.readFloat();
            ExperienceSource experienceSource = ExperienceSource.values()[buf.readInt()];
            LivingMultiplier playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            return new GainedExperienceBonus(multiplier, experienceSource, playerMultiplier);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof GainedExperienceBonus)) {
                throw new IllegalArgumentException();
            }
            GainedExperienceBonus aBonus = (GainedExperienceBonus)bonus;
            buf.writeFloat(aBonus.multiplier);
            buf.writeInt(aBonus.experienceSource.ordinal());
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new GainedExperienceBonus(0.25f, ExperienceSource.MOBS);
        }
    }
}

