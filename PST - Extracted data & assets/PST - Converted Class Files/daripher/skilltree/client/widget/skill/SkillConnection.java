/*
 * Decompiled with CFR 0.152.
 */
package daripher.skilltree.client.widget.skill;

import daripher.skilltree.client.widget.skill.SkillButton;

public class SkillConnection {
    private final Type type;
    private final SkillButton button1;
    private final SkillButton button2;

    public SkillConnection(Type type, SkillButton button1, SkillButton button2) {
        this.type = type;
        this.button1 = button1;
        this.button2 = button2;
    }

    public SkillButton getFirstButton() {
        return this.button1;
    }

    public SkillButton getSecondButton() {
        return this.button2;
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        DIRECT,
        LONG,
        ONE_WAY;

    }
}

