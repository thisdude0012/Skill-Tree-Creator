/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 */
package daripher.skilltree.skill;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class PassiveSkillTree {
    private final List<ResourceLocation> skillIds = new ArrayList<ResourceLocation>();
    private final ResourceLocation id;
    @Nullable
    private Map<String, Integer> skillLimitations;

    public PassiveSkillTree(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public List<ResourceLocation> getSkillIds() {
        return this.skillIds;
    }

    public Map<String, Integer> getSkillLimitations() {
        if (this.skillLimitations == null) {
            this.skillLimitations = new LinkedHashMap<String, Integer>();
            return this.skillLimitations;
        }
        return this.skillLimitations;
    }
}

