/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nonnull
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.player.Player
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
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import daripher.skilltree.skill.bonus.condition.living.NoneLivingCondition;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.multiplier.NoneLivingMultiplier;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public final class ProjectileSpeedBonus
implements SkillBonus<ProjectileSpeedBonus> {
    private float multiplier;
    @Nonnull
    private LivingCondition playerCondition = NoneLivingCondition.INSTANCE;
    @Nonnull
    private LivingMultiplier playerMultiplier = NoneLivingMultiplier.INSTANCE;

    public ProjectileSpeedBonus(float multiplier) {
        this.multiplier = multiplier;
    }

    public float getMultiplier(Player player) {
        if (!this.playerCondition.isConditionMet((LivingEntity)player)) {
            return 0.0f;
        }
        return this.multiplier * this.playerMultiplier.getValue((LivingEntity)player);
    }

    @Override
    public SkillBonus.Serializer getSerializer() {
        return (SkillBonus.Serializer)PSTSkillBonuses.PROJECTILE_SPEED.get();
    }

    public ProjectileSpeedBonus copy() {
        return new ProjectileSpeedBonus(this.multiplier).setPlayerCondition(this.playerCondition).setPlayerMultiplier(this.playerMultiplier);
    }

    @Override
    public ProjectileSpeedBonus multiply(double multiplier) {
        this.multiplier = (float)((double)this.multiplier * multiplier);
        return this;
    }

    @Override
    public boolean canMerge(SkillBonus<?> other) {
        if (!(other instanceof ProjectileSpeedBonus)) {
            return false;
        }
        ProjectileSpeedBonus otherBonus = (ProjectileSpeedBonus)other;
        if (!Objects.equals(otherBonus.playerCondition, this.playerCondition)) {
            return false;
        }
        return Objects.equals(otherBonus.playerMultiplier, this.playerMultiplier);
    }

    @Override
    public SkillBonus<ProjectileSpeedBonus> merge(SkillBonus<?> other) {
        if (!(other instanceof ProjectileSpeedBonus)) {
            throw new IllegalArgumentException();
        }
        ProjectileSpeedBonus otherBonus = (ProjectileSpeedBonus)other;
        return new ProjectileSpeedBonus(otherBonus.multiplier + this.multiplier).setPlayerCondition(this.playerCondition).setPlayerMultiplier(this.playerMultiplier);
    }

    @Override
    public MutableComponent getTooltip() {
        MutableComponent bonusTooltip = TooltipHelper.getSkillBonusTooltip(this.getDescriptionId(), (double)this.multiplier, AttributeModifier.Operation.MULTIPLY_BASE);
        bonusTooltip = this.playerCondition.getTooltip(bonusTooltip, SkillBonus.Target.PLAYER);
        bonusTooltip = this.playerMultiplier.getTooltip(bonusTooltip, SkillBonus.Target.PLAYER);
        return bonusTooltip.m_130948_(TooltipHelper.getSkillBonusStyle(this.isPositive()));
    }

    @Override
    public boolean isPositive() {
        return this.multiplier > 0.0f;
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, int index, Consumer<ProjectileSpeedBonus> consumer) {
        editor.addLabel(0, 0, "Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addNumericTextField(0, 0, 50, 14, this.multiplier).setNumericResponder(value -> this.selectMultiplier(consumer, (Double)value));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Condition", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerCondition).setResponder(condition -> this.selectPlayerCondition(editor, consumer, (LivingCondition)condition)).setMenuInitFunc(() -> this.addPlayerConditionWidgets(editor, consumer));
        editor.increaseHeight(19);
        editor.addLabel(0, 0, "Player Multiplier", ChatFormatting.GOLD);
        editor.increaseHeight(19);
        editor.addSelectionMenu(0, 0, 200, this.playerMultiplier).setResponder(multiplier -> this.selectPlayerMultiplier(editor, consumer, (LivingMultiplier)multiplier)).setMenuInitFunc(() -> this.addPlayerMultiplierWidgets(editor, consumer));
        editor.increaseHeight(19);
    }

    private void selectPlayerCondition(SkillTreeEditor editor, Consumer<ProjectileSpeedBonus> consumer, LivingCondition condition) {
        this.setPlayerCondition(condition);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectPlayerMultiplier(SkillTreeEditor editor, Consumer<ProjectileSpeedBonus> consumer, LivingMultiplier multiplier) {
        this.setPlayerMultiplier(multiplier);
        consumer.accept(this.copy());
        editor.rebuildWidgets();
    }

    private void selectMultiplier(Consumer<ProjectileSpeedBonus> consumer, Double value) {
        this.setMultiplier(value.floatValue());
        consumer.accept(this.copy());
    }

    private void addPlayerConditionWidgets(SkillTreeEditor editor, Consumer<ProjectileSpeedBonus> consumer) {
        this.playerCondition.addEditorWidgets(editor, c -> {
            this.setPlayerCondition((LivingCondition)c);
            consumer.accept(this.copy());
        });
    }

    private void addPlayerMultiplierWidgets(SkillTreeEditor editor, Consumer<ProjectileSpeedBonus> consumer) {
        this.playerMultiplier.addEditorWidgets(editor, m -> {
            this.setPlayerMultiplier((LivingMultiplier)m);
            consumer.accept(this.copy());
        });
    }

    public ProjectileSpeedBonus setPlayerCondition(@Nonnull LivingCondition playerCondition) {
        this.playerCondition = playerCondition;
        return this;
    }

    public ProjectileSpeedBonus setPlayerMultiplier(@Nonnull LivingMultiplier playerMultiplier) {
        this.playerMultiplier = playerMultiplier;
        return this;
    }

    public ProjectileSpeedBonus setMultiplier(float multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    @Nonnull
    public LivingCondition getPlayerCondition() {
        return this.playerCondition;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProjectileSpeedBonus that = (ProjectileSpeedBonus)o;
        return Float.compare(this.multiplier, that.multiplier) == 0 && Objects.equals(this.playerCondition, that.playerCondition) && Objects.equals(this.playerMultiplier, that.playerMultiplier);
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.multiplier), this.playerCondition, this.playerMultiplier);
    }

    public static class Serializer
    implements SkillBonus.Serializer {
        @Override
        public ProjectileSpeedBonus deserialize(JsonObject json) throws JsonParseException {
            float multiplier = SerializationHelper.getElement(json, "multiplier").getAsFloat();
            LivingCondition playerCondition = SerializationHelper.deserializeLivingCondition(json, "player_condition");
            LivingMultiplier playerMultiplier = SerializationHelper.deserializeLivingMultiplier(json, "player_multiplier");
            return new ProjectileSpeedBonus(multiplier).setPlayerCondition(playerCondition).setPlayerMultiplier(playerMultiplier);
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileSpeedBonus)) {
                throw new IllegalArgumentException();
            }
            ProjectileSpeedBonus aBonus = (ProjectileSpeedBonus)bonus;
            json.addProperty("multiplier", (Number)Float.valueOf(aBonus.multiplier));
            SerializationHelper.serializeLivingCondition(json, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(json, aBonus.playerMultiplier, "player_multiplier");
        }

        @Override
        public ProjectileSpeedBonus deserialize(CompoundTag tag) {
            float multiplier = tag.m_128457_("multiplier");
            LivingCondition playerCondition = SerializationHelper.deserializeLivingCondition(tag, "player_condition");
            LivingMultiplier playerMultiplier = SerializationHelper.deserializeLivingMultiplier(tag, "player_multiplier");
            return new ProjectileSpeedBonus(multiplier).setPlayerCondition(playerCondition).setPlayerMultiplier(playerMultiplier);
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileSpeedBonus)) {
                throw new IllegalArgumentException();
            }
            ProjectileSpeedBonus aBonus = (ProjectileSpeedBonus)bonus;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeLivingCondition(tag, aBonus.playerCondition, "player_condition");
            SerializationHelper.serializeLivingMultiplier(tag, aBonus.playerMultiplier, "player_multiplier");
            tag.m_128350_("multiplier", aBonus.multiplier);
            return tag;
        }

        @Override
        public ProjectileSpeedBonus deserialize(FriendlyByteBuf buf) {
            LivingCondition playerCondition = NetworkHelper.readLivingCondition(buf);
            LivingMultiplier playerMultiplier = NetworkHelper.readLivingMultiplier(buf);
            return new ProjectileSpeedBonus(buf.readFloat()).setPlayerCondition(playerCondition).setPlayerMultiplier(playerMultiplier);
        }

        @Override
        public void serialize(FriendlyByteBuf buf, SkillBonus<?> bonus) {
            if (!(bonus instanceof ProjectileSpeedBonus)) {
                throw new IllegalArgumentException();
            }
            ProjectileSpeedBonus aBonus = (ProjectileSpeedBonus)bonus;
            NetworkHelper.writeLivingCondition(buf, aBonus.playerCondition);
            NetworkHelper.writeLivingMultiplier(buf, aBonus.playerMultiplier);
            buf.writeFloat(aBonus.multiplier);
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return new ProjectileSpeedBonus(0.1f);
        }
    }
}

