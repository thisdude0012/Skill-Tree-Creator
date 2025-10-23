/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 */
package daripher.skilltree.skill.bonus.event;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.SkillBonus;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public interface SkillEventListener {
    default public String getDescriptionId() {
        ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey((Object)this.getSerializer());
        Objects.requireNonNull(id);
        return "event_listener.%s.%s".formatted(new Object[]{id.m_135827_(), id.m_135815_()});
    }

    default public MutableComponent getTooltip(Component bonusTooltip) {
        return Component.m_237110_((String)this.getDescriptionId(), (Object[])new Object[]{bonusTooltip});
    }

    public SkillBonus.Target getTarget();

    public Serializer getSerializer();

    public void addEditorWidgets(SkillTreeEditor var1, Consumer<SkillEventListener> var2);

    public static interface Serializer
    extends daripher.skilltree.data.serializers.Serializer<SkillEventListener> {
        public SkillEventListener createDefaultInstance();
    }
}

