/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.shadowsoffire.attributeslib.client.ModifierSourceType
 */
package daripher.skilltree.compat.attributeslib;

import daripher.skilltree.compat.attributeslib.SkillModifierSourceType;
import daripher.skilltree.skill.PassiveSkill;
import dev.shadowsoffire.attributeslib.client.ModifierSourceType;

public enum AttributesLibCompatibility {
    INSTANCE;

    public static final ModifierSourceType<PassiveSkill> SKILL_MODIFIER_TYPE;

    public void register() {
    }

    static {
        SKILL_MODIFIER_TYPE = ModifierSourceType.register((ModifierSourceType)new SkillModifierSourceType());
    }
}

