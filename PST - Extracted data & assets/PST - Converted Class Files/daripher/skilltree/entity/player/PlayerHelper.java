/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 *  top.theillusivec4.curios.api.CuriosApi
 *  top.theillusivec4.curios.api.type.capability.ICuriosItemHandler
 */
package daripher.skilltree.entity.player;

import com.google.common.collect.Streams;
import daripher.skilltree.skill.bonus.condition.item.EquipmentCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class PlayerHelper {
    public static Stream<ItemStack> getAllEquipment(LivingEntity living) {
        return Streams.concat((Stream[])new Stream[]{PlayerHelper.getVanillaEquipment(living), PlayerHelper.getCurios(living)});
    }

    public static Stream<ItemStack> getItemsInHands(LivingEntity living) {
        return Stream.of(living.m_21205_(), living.m_21206_());
    }

    public static Stream<ItemStack> getVanillaEquipment(LivingEntity living) {
        return Arrays.stream(EquipmentSlot.values()).map(slot -> PlayerHelper.getEquipmentInSlot(living, slot));
    }

    public static Stream<ItemStack> getArmor(LivingEntity living) {
        return Arrays.stream(EquipmentSlot.values()).filter(EquipmentSlot::m_254934_).map(slot -> PlayerHelper.getEquipmentInSlot(living, slot));
    }

    @NotNull
    private static ItemStack getEquipmentInSlot(LivingEntity living, EquipmentSlot slot) {
        ItemStack stack = living.m_6844_(slot);
        if (!(slot != EquipmentSlot.MAINHAND || EquipmentCondition.isWeapon(stack) || EquipmentCondition.isTool(stack) || EquipmentCondition.isPotion(stack))) {
            return ItemStack.f_41583_;
        }
        if (slot == EquipmentSlot.OFFHAND && EquipmentCondition.isPotion(stack)) {
            return ItemStack.f_41583_;
        }
        return stack;
    }

    public static Stream<ItemStack> getCurios(LivingEntity living) {
        ArrayList curios = new ArrayList();
        CuriosApi.getCuriosInventory((LivingEntity)living).map(ICuriosItemHandler::getEquippedCurios).ifPresent(inv -> {
            for (int i = 0; i < inv.getSlots(); ++i) {
                curios.add(inv.getStackInSlot(i));
            }
        });
        return curios.stream();
    }
}

