/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 */
package daripher.skilltree.skill.bonus;

import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.TickingSkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.event.SkillLearnedEventListener;
import daripher.skilltree.skill.bonus.event.SkillRemovedEventListener;
import daripher.skilltree.skill.bonus.event.TickingEventListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface EventListenerBonus<T>
extends TickingSkillBonus,
SkillBonus<EventListenerBonus<T>> {
    @Override
    default public void onSkillLearned(ServerPlayer player, boolean firstTime) {
        SkillEventListener skillEventListener;
        if (firstTime && (skillEventListener = this.getEventListener()) instanceof SkillLearnedEventListener) {
            SkillLearnedEventListener listener = (SkillLearnedEventListener)skillEventListener;
            listener.onEvent((Player)player, this);
        }
    }

    @Override
    default public void onSkillRemoved(ServerPlayer player) {
        SkillEventListener skillEventListener = this.getEventListener();
        if (skillEventListener instanceof SkillRemovedEventListener) {
            SkillRemovedEventListener listener = (SkillRemovedEventListener)skillEventListener;
            listener.onEvent((Player)player, this);
        }
    }

    @Override
    default public void tick(ServerPlayer player) {
        if (player.f_19797_ % 10 != 0) {
            return;
        }
        SkillEventListener skillEventListener = this.getEventListener();
        if (skillEventListener instanceof TickingEventListener) {
            TickingEventListener listener = (TickingEventListener)skillEventListener;
            listener.onEvent((Player)player, this);
        }
    }

    public SkillEventListener getEventListener();

    public void applyEffect(LivingEntity var1);
}

