/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 */
package daripher.skilltree.skill.bonus.condition.item;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface ItemCondition {
    public boolean met(ItemStack var1);

    default public String getDescriptionId() {
        ResourceLocation id = PSTRegistries.ITEM_CONDITIONS.get().getKey((Object)this.getSerializer());
        Objects.requireNonNull(id);
        return "item_condition.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
    }

    default public Component getTooltip() {
        return Component.m_237115_((String)this.getDescriptionId());
    }

    default public Component getTooltip(String type) {
        return TooltipHelper.getOptionalTooltip(this.getDescriptionId(), type, new Object[0]);
    }

    public Serializer getSerializer();

    default public void addEditorWidgets(SkillTreeEditor editor, Consumer<ItemCondition> consumer) {
    }

    public static interface Serializer
    extends daripher.skilltree.data.serializers.Serializer<ItemCondition> {
        public ItemCondition createDefaultInstance();
    }
}

