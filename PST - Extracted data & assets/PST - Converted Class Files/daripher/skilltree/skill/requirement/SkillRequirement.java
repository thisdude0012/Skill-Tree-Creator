/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 */
package daripher.skilltree.skill.requirement;

import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import java.util.function.Consumer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public interface SkillRequirement<T extends SkillRequirement<T>> {
    public boolean isRequirementMet(Player var1);

    public MutableComponent getTooltip();

    public void addEditorWidgets(SkillTreeEditor var1, Consumer<T> var2);

    public Serializer getSerializer();

    public T copy();

    public static interface Serializer
    extends daripher.skilltree.data.serializers.Serializer<SkillRequirement<?>> {
        public SkillRequirement<?> createDefaultInstance();
    }
}

