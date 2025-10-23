/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier
 *  net.minecraft.world.entity.ai.attributes.RangedAttribute
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.event.entity.EntityAttributeModificationEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package daripher.skilltree.init;

import java.util.Collection;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid="skilltree", bus=Mod.EventBusSubscriber.Bus.MOD)
public class PSTAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ATTRIBUTES, (String)"skilltree");
    public static final RegistryObject<Attribute> EXP_PER_MINUTE = PSTAttributes.create("exp_per_minute", 1000.0);
    public static final RegistryObject<Attribute> REGENERATION = PSTAttributes.create("regeneration", 1000.0);

    private static RegistryObject<Attribute> create(String name, double maxValue) {
        return PSTAttributes.create(name, 0.0, maxValue);
    }

    private static RegistryObject<Attribute> create(String name, double minValue, double maxValue) {
        String descriptionId = "attribute.name.%s.%s".formatted(new Object[]{"skilltree", name});
        return REGISTRY.register(name, () -> new RangedAttribute(descriptionId, minValue, minValue, maxValue).m_22084_(true));
    }

    @SubscribeEvent
    public static void attachAttributes(EntityAttributeModificationEvent event) {
        REGISTRY.getEntries().stream().map(RegistryObject::get).forEach(attribute -> event.add(EntityType.f_20532_, attribute));
    }

    public static Collection<Attribute> attributeList() {
        return ForgeRegistries.ATTRIBUTES.getValues().stream().filter(arg_0 -> ((AttributeSupplier)((AttributeSupplier)ForgeHooks.getAttributesView().get(EntityType.f_20532_))).m_22258_(arg_0)).toList();
    }

    public static String getName(Attribute attribute) {
        ResourceLocation id = ForgeRegistries.ATTRIBUTES.getKey((Object)attribute);
        Objects.requireNonNull(id);
        return id.toString();
    }
}

