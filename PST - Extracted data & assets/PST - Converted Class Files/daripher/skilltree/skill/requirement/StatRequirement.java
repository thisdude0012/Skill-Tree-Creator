/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nonnull
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.stats.Stat
 *  net.minecraft.stats.StatType
 *  net.minecraft.stats.Stats
 *  net.minecraft.stats.StatsCounter
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.registries.ForgeRegistries
 */
package daripher.skilltree.skill.requirement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public final class StatRequirement
implements SkillRequirement<StatRequirement> {
    private ResourceLocation statTypeId;
    private ResourceLocation statId;
    private int minValue;

    public StatRequirement(ResourceLocation statTypeId, ResourceLocation statId, int minValue) {
        this.statTypeId = statTypeId;
        this.statId = statId;
        this.minValue = minValue;
    }

    @Override
    public boolean isRequirementMet(Player player) {
        StatType statType = (StatType)ForgeRegistries.STAT_TYPES.getValue(this.statTypeId);
        Objects.requireNonNull(statType);
        int statValue = this.getStatValue(player, statType);
        return statValue >= this.minValue;
    }

    @Override
    public MutableComponent getTooltip() {
        StatType statType = (StatType)ForgeRegistries.STAT_TYPES.getValue(this.statTypeId);
        if (statType == null) {
            return Component.m_237113_((String)("Unknown stat type: " + this.statTypeId)).m_130940_(ChatFormatting.RED);
        }
        if (statType == Stats.f_12988_) {
            ResourceLocation originalStatId = (ResourceLocation)Stats.f_12988_.m_12893_().m_7745_(this.statId);
            if (originalStatId == null) {
                return Component.m_237113_((String)("Unknown stat: " + this.statId)).m_130940_(ChatFormatting.RED);
            }
            String statIdString = originalStatId.toString().replace(':', '.');
            MutableComponent statName = Component.m_237115_((String)("stat." + statIdString));
            Stat stat = Stats.f_12988_.m_12902_((Object)originalStatId);
            String formattedMinValue = stat.m_12860_(this.minValue).replace(".00", "");
            return Component.m_237113_((String)(statName.getString() + ": " + formattedMinValue));
        }
        if (statType == Stats.f_12986_) {
            EntityType entityType = (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(this.statId);
            if (entityType == null) {
                return Component.m_237113_((String)("Unknown entity: " + this.statId)).m_130940_(ChatFormatting.RED);
            }
            Component entityName = entityType.m_20676_();
            return Component.m_237110_((String)statType.m_12904_(), (Object[])new Object[]{this.minValue, entityName});
        }
        if (statType == Stats.f_12987_) {
            EntityType entityType = (EntityType)ForgeRegistries.ENTITY_TYPES.getValue(this.statId);
            if (entityType == null) {
                return Component.m_237113_((String)("Unknown entity: " + this.statId)).m_130940_(ChatFormatting.RED);
            }
            Component entityName = entityType.m_20676_();
            return Component.m_237110_((String)statType.m_12904_(), (Object[])new Object[]{entityName, this.minValue});
        }
        Item item = (Item)ForgeRegistries.ITEMS.getValue(this.statId);
        if (item == null) {
            return Component.m_237113_((String)("Unknown item: " + this.statId)).m_130940_(ChatFormatting.RED);
        }
        Component itemName = item.m_41466_();
        return Component.m_237113_((String)(statType.m_12905_().getString() + " " + itemName.getString() + ": " + this.minValue));
    }

    private <T> int getStatValue(Player player, @Nonnull StatType<T> statType) {
        int statValue;
        StatsCounter playerStats = this.getPlayerStats(player);
        if (statType == Stats.f_12988_) {
            ResourceLocation originalStatId = (ResourceLocation)Stats.f_12988_.m_12893_().m_7745_(this.statId);
            if (originalStatId == null) {
                return 0;
            }
            statValue = playerStats.m_13017_(Stats.f_12988_, (Object)originalStatId);
        } else {
            Object stat = statType.m_12893_().m_7745_(this.statId);
            Objects.requireNonNull(stat);
            statValue = playerStats.m_13017_(statType, stat);
        }
        return statValue;
    }

    private StatsCounter getPlayerStats(Player player) {
        if (player.m_9236_().f_46443_) {
            return StatRequirement.getClientPlayerStats(player);
        }
        return ((ServerPlayer)player).m_8951_();
    }

    @OnlyIn(value=Dist.CLIENT)
    private static StatsCounter getClientPlayerStats(Player player) {
        return ((LocalPlayer)player).m_108630_();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<StatRequirement> consumer) {
        editor.addLabel(0, 0, "Stat Type", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        Set statTypeIds = ForgeRegistries.STAT_TYPES.getKeys();
        editor.addSelectionMenu(0, 0, 200, statTypeIds).setValue(this.getStatTypeId()).setElementNameGetter(v -> Component.m_237113_((String)v.toString())).setResponder(v -> this.selectStatType(consumer, (ResourceLocation)v));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Stat", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        StatType statType = (StatType)ForgeRegistries.STAT_TYPES.getValue(this.getStatTypeId());
        Objects.requireNonNull(statType);
        Set statIds = statType.m_12893_().m_6566_();
        editor.addSelectionMenu(0, 0, 200, statIds).setValue(this.getStatId()).setElementNameGetter(v -> Component.m_237113_((String)v.toString())).setResponder(v -> this.selectStat(consumer, (ResourceLocation)v));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Min Value", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.minValue).setNumericFilter(value -> value == (double)value.intValue()).setNumericResponder(value -> this.selectMinValue(consumer, (Double)value));
        editor.increaseHeight(19);
    }

    private void selectMinValue(Consumer<StatRequirement> consumer, Double value) {
        this.setMinValue(value.intValue());
        consumer.accept(this);
    }

    private void selectStat(Consumer<StatRequirement> consumer, ResourceLocation statId) {
        this.setStatId(statId);
        consumer.accept(this);
    }

    private void selectStatType(Consumer<StatRequirement> consumer, ResourceLocation statTypeId) {
        this.setStatTypeId(statTypeId);
        StatType statType = (StatType)ForgeRegistries.STAT_TYPES.getValue(this.getStatTypeId());
        Objects.requireNonNull(statType);
        Set statIds = statType.m_12893_().m_6566_();
        statIds.stream().findFirst().ifPresent(this::setStatId);
        consumer.accept(this);
    }

    public void setStatId(ResourceLocation statId) {
        this.statId = statId;
    }

    public void setStatTypeId(ResourceLocation statTypeId) {
        this.statTypeId = statTypeId;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @Override
    public StatRequirement copy() {
        return new StatRequirement(this.statTypeId, this.statId, this.minValue);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        StatRequirement that = (StatRequirement)o;
        return this.minValue == that.minValue && Objects.equals(this.statTypeId, that.statTypeId) && Objects.equals(this.statId, that.statId);
    }

    public int hashCode() {
        return Objects.hash(this.statTypeId, this.statId, this.minValue);
    }

    public ResourceLocation getStatTypeId() {
        return this.statTypeId;
    }

    public ResourceLocation getStatId() {
        return this.statId;
    }

    @Override
    public SkillRequirement.Serializer getSerializer() {
        return (SkillRequirement.Serializer)PSTSkillRequirements.STAT_VALUE.get();
    }

    public static class Serializer
    implements SkillRequirement.Serializer {
        @Override
        public SkillRequirement<?> deserialize(JsonObject json) throws JsonParseException {
            ResourceLocation statTypeId = new ResourceLocation(json.get("statTypeId").getAsString());
            ResourceLocation statId = new ResourceLocation(json.get("statId").getAsString());
            int minValue = json.get("minValue").getAsInt();
            return new StatRequirement(statTypeId, statId, minValue);
        }

        @Override
        public void serialize(JsonObject json, SkillRequirement<?> requirement) {
            if (requirement instanceof StatRequirement) {
                StatRequirement aRequirement = (StatRequirement)requirement;
                json.addProperty("statTypeId", aRequirement.statTypeId.toString());
                json.addProperty("statId", aRequirement.statId.toString());
                json.addProperty("minValue", (Number)aRequirement.minValue);
            }
        }

        @Override
        public SkillRequirement<?> deserialize(CompoundTag tag) {
            ResourceLocation statTypeId = new ResourceLocation(tag.m_128461_("statTypeId"));
            ResourceLocation statId = new ResourceLocation(tag.m_128461_("statId"));
            int minValue = tag.m_128451_("minValue");
            return new StatRequirement(statTypeId, statId, minValue);
        }

        @Override
        public CompoundTag serialize(SkillRequirement<?> requirement) {
            CompoundTag tag = new CompoundTag();
            if (requirement instanceof StatRequirement) {
                StatRequirement aRequirement = (StatRequirement)requirement;
                tag.m_128359_("statTypeId", aRequirement.statTypeId.toString());
                tag.m_128359_("statId", aRequirement.statId.toString());
                tag.m_128405_("minValue", aRequirement.minValue);
            }
            return tag;
        }

        @Override
        public SkillRequirement<?> deserialize(FriendlyByteBuf buf) {
            ResourceLocation statTypeId = new ResourceLocation(buf.m_130277_());
            ResourceLocation statId = new ResourceLocation(buf.m_130277_());
            int minValue = buf.readInt();
            return new StatRequirement(statTypeId, statId, minValue);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillRequirement<?> requirement) {
            if (requirement instanceof StatRequirement) {
                StatRequirement aRequirement = (StatRequirement)requirement;
                buf.m_130070_(aRequirement.statTypeId.toString());
                buf.m_130070_(aRequirement.statId.toString());
                buf.writeInt(aRequirement.minValue);
            }
        }

        @Override
        public SkillRequirement<?> createDefaultInstance() {
            return new StatRequirement(ForgeRegistries.STAT_TYPES.getKey((Object)Stats.f_12988_), Stats.f_12935_, 1);
        }
    }
}

