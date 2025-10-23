/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nonnull
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 *  net.minecraftforge.common.loot.IGlobalLootModifier
 *  net.minecraftforge.common.loot.LootModifier
 *  org.jetbrains.annotations.NotNull
 */
package daripher.skilltree.loot.modifier;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import daripher.skilltree.skill.bonus.player.LootDuplicationBonus;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class SkillBonusesModifier
extends LootModifier {
    public static final Supplier<Codec<SkillBonusesModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> SkillBonusesModifier.codecStart((RecordCodecBuilder.Instance)inst).apply((Applicative)inst, SkillBonusesModifier::new)));

    public SkillBonusesModifier(LootItemCondition ... conditionsIn) {
        super(conditionsIn);
    }

    @NotNull
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext lootContext) {
        for (LootItemCondition condition : this.conditions) {
            if (condition.test((Object)lootContext)) continue;
            return generatedLoot;
        }
        Player player = null;
        float lootMultiplier = 0.0f;
        for (LootDuplicationBonus.LootType lootType : LootDuplicationBonus.LootType.values()) {
            if (!lootType.canAffect(lootContext)) continue;
            player = (Player)lootContext.m_165124_(lootType.getPlayerLootContextParam());
            lootMultiplier = SkillBonusesModifier.getLootMultiplier(player, lootType);
        }
        if (player == null) {
            return generatedLoot;
        }
        if (lootMultiplier == 0.0f) {
            return generatedLoot;
        }
        RandomSource random = lootContext.m_230907_();
        ObjectArrayList newLoot = new ObjectArrayList();
        int copies = (int)lootMultiplier;
        lootMultiplier -= (float)copies;
        ++copies;
        for (ItemStack stack : generatedLoot) {
            int itemCopies = copies;
            if (random.m_188501_() < lootMultiplier) {
                ++itemCopies;
            }
            for (int i = 0; i < itemCopies; ++i) {
                newLoot.add((Object)stack.m_41777_());
            }
        }
        return newLoot;
    }

    private static float getLootMultiplier(Player player, LootDuplicationBonus.LootType lootType) {
        RandomSource random = player.m_217043_();
        Map<Float, Float> multipliers = SkillBonusesModifier.getLootMultipliers(player, lootType);
        float multiplier = 0.0f;
        for (Map.Entry<Float, Float> entry : multipliers.entrySet()) {
            float chance;
            for (chance = entry.getValue().floatValue(); chance > 1.0f; chance -= 1.0f) {
                multiplier += entry.getKey().floatValue();
            }
            if (!(random.m_188501_() < chance)) continue;
            multiplier += entry.getKey().floatValue();
        }
        return multiplier;
    }

    @Nonnull
    private static Map<Float, Float> getLootMultipliers(Player player, LootDuplicationBonus.LootType lootType) {
        HashMap<Float, Float> multipliers = new HashMap<Float, Float>();
        for (LootDuplicationBonus bonus : SkillBonusHandler.getSkillBonuses(player, LootDuplicationBonus.class)) {
            if (bonus.getLootType() != lootType) continue;
            float chance = bonus.getChance() + multipliers.getOrDefault(Float.valueOf(bonus.getMultiplier()), Float.valueOf(0.0f)).floatValue();
            multipliers.put(Float.valueOf(bonus.getMultiplier()), Float.valueOf(chance));
        }
        return multipliers;
    }

    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}

