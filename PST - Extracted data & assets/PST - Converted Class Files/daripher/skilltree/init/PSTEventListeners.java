/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.RegistryObject
 */
package daripher.skilltree.init;

import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.event.AttackEventListener;
import daripher.skilltree.skill.bonus.event.BlockEventListener;
import daripher.skilltree.skill.bonus.event.DamageTakenEventListener;
import daripher.skilltree.skill.bonus.event.EvasionEventListener;
import daripher.skilltree.skill.bonus.event.ItemUsedEventListener;
import daripher.skilltree.skill.bonus.event.KillEventListener;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.event.SkillLearnedEventListener;
import daripher.skilltree.skill.bonus.event.SkillRemovedEventListener;
import daripher.skilltree.skill.bonus.event.TickingEventListener;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PSTEventListeners {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation("skilltree", "event_listeners");
    public static final DeferredRegister<SkillEventListener.Serializer> REGISTRY = DeferredRegister.create((ResourceLocation)REGISTRY_ID, (String)"skilltree");
    public static final RegistryObject<SkillEventListener.Serializer> ATTACK = REGISTRY.register("attack", AttackEventListener.Serializer::new);
    public static final RegistryObject<SkillEventListener.Serializer> BLOCK = REGISTRY.register("block", BlockEventListener.Serializer::new);
    public static final RegistryObject<SkillEventListener.Serializer> EVASION = REGISTRY.register("evasion", EvasionEventListener.Serializer::new);
    public static final RegistryObject<SkillEventListener.Serializer> ITEM_USED = REGISTRY.register("item_used", ItemUsedEventListener.Serializer::new);
    public static final RegistryObject<SkillEventListener.Serializer> DAMAGE_TAKEN = REGISTRY.register("damage_taken", DamageTakenEventListener.Serializer::new);
    public static final RegistryObject<SkillEventListener.Serializer> ON_KILL = REGISTRY.register("on_kill", KillEventListener.Serializer::new);
    public static final RegistryObject<SkillEventListener.Serializer> SKILL_LEARNED = REGISTRY.register("skill_learned", SkillLearnedEventListener.Serializer::new);
    public static final RegistryObject<SkillEventListener.Serializer> SKILL_REMOVED = REGISTRY.register("skill_removed", SkillRemovedEventListener.Serializer::new);
    public static final RegistryObject<SkillEventListener.Serializer> TICKING = REGISTRY.register("ticking", TickingEventListener.Serializer::new);

    public static List<SkillEventListener> eventsList() {
        return PSTRegistries.EVENT_LISTENERS.get().getValues().stream().map(SkillEventListener.Serializer::createDefaultInstance).toList();
    }

    public static String getName(SkillEventListener eventType) {
        ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey((Object)eventType.getSerializer());
        return TooltipHelper.idToName(Objects.requireNonNull(id).m_135815_());
    }
}

