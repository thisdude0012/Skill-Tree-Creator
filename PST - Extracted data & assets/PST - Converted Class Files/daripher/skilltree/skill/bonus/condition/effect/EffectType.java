/*
 * Decompiled with CFR 0.152.
 */
package daripher.skilltree.skill.bonus.condition.effect;

public enum EffectType {
    NEUTRAL,
    HARMFUL,
    BENEFICIAL,
    ANY;


    public String getName() {
        return this.name().toLowerCase();
    }

    public static EffectType fromName(String name) {
        return EffectType.valueOf(name.toUpperCase());
    }

    public String getDescriptionId() {
        return "effect_type." + this.getName();
    }
}

