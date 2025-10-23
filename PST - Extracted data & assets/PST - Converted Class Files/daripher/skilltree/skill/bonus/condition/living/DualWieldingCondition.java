/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nonnull
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.LivingEntity
 */
package daripher.skilltree.skill.bonus.condition.living;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.entity.player.PlayerHelper;
import daripher.skilltree.init.PSTLivingConditions;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import daripher.skilltree.skill.bonus.condition.item.ItemCondition;
import daripher.skilltree.skill.bonus.condition.living.LivingCondition;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

public final class DualWieldingCondition
implements LivingCondition {
    @Nonnull
    private ItemCondition weaponCondition;

    public DualWieldingCondition(@Nonnull ItemCondition weaponCondition) {
        this.weaponCondition = weaponCondition;
    }

    @Override
    public boolean isConditionMet(LivingEntity living) {
        return PlayerHelper.getItemsInHands(living).allMatch(this.weaponCondition::met);
    }

    @Override
    public MutableComponent getTooltip(MutableComponent bonusTooltip, SkillBonus.Target target) {
        String key = this.getDescriptionId();
        MutableComponent targetDescription = Component.m_237115_((String)"%s.target.%s".formatted(new Object[]{key, target.getName()}));
        Component itemDescription = this.weaponCondition.getTooltip();
        return Component.m_237110_((String)key, (Object[])new Object[]{bonusTooltip, targetDescription, itemDescription});
    }

    @Override
    public LivingCondition.Serializer getSerializer() {
        return (LivingCondition.Serializer)PSTLivingConditions.DUAL_WIELDING.get();
    }

    @Override
    public void addEditorWidgets(SkillTreeEditor editor, Consumer<LivingCondition> consumer) {
        this.weaponCondition.addEditorWidgets(editor, (ItemCondition c) -> {
            this.setWeaponCondition((ItemCondition)c);
            consumer.accept(this);
        });
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DualWieldingCondition that = (DualWieldingCondition)o;
        return Objects.equals(this.weaponCondition, that.weaponCondition);
    }

    public int hashCode() {
        return Objects.hash(this.weaponCondition);
    }

    public void setWeaponCondition(@Nonnull ItemCondition weaponCondition) {
        this.weaponCondition = weaponCondition;
    }

    public static class Serializer
    implements LivingCondition.Serializer {
        @Override
        public LivingCondition deserialize(JsonObject json) throws JsonParseException {
            return new DualWieldingCondition(SerializationHelper.deserializeItemCondition(json));
        }

        @Override
        public void serialize(JsonObject json, LivingCondition condition) {
            if (!(condition instanceof DualWieldingCondition)) {
                throw new IllegalArgumentException();
            }
            DualWieldingCondition aCondition = (DualWieldingCondition)condition;
            SerializationHelper.serializeItemCondition(json, aCondition.weaponCondition);
        }

        @Override
        public LivingCondition deserialize(CompoundTag tag) {
            return new DualWieldingCondition(SerializationHelper.deserializeItemCondition(tag));
        }

        @Override
        public CompoundTag serialize(LivingCondition condition) {
            if (!(condition instanceof DualWieldingCondition)) {
                throw new IllegalArgumentException();
            }
            DualWieldingCondition aCondition = (DualWieldingCondition)condition;
            CompoundTag tag = new CompoundTag();
            SerializationHelper.serializeItemCondition(tag, aCondition.weaponCondition);
            return tag;
        }

        @Override
        public LivingCondition deserialize(FriendlyByteBuf buf) {
            return new DualWieldingCondition(NetworkHelper.readItemCondition(buf));
        }

        @Override
        public void serialize(FriendlyByteBuf buf, LivingCondition condition) {
            if (!(condition instanceof DualWieldingCondition)) {
                throw new IllegalArgumentException();
            }
            DualWieldingCondition aCondition = (DualWieldingCondition)condition;
            NetworkHelper.writeItemCondition(buf, aCondition.weaponCondition);
        }

        @Override
        public LivingCondition createDefaultInstance() {
            return new DualWieldingCondition(new EquipmentCondition(EquipmentCondition.Type.WEAPON));
        }
    }
}

