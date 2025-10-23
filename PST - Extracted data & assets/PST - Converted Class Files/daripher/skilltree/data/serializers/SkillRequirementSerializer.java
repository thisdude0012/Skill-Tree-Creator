/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  net.minecraft.resources.ResourceLocation
 */
package daripher.skilltree.data.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.init.PSTSkillRequirements;
import daripher.skilltree.skill.requirement.SkillRequirement;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public class SkillRequirementSerializer
implements JsonSerializer<SkillRequirement<?>>,
JsonDeserializer<SkillRequirement<?>> {
    public SkillRequirement<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String type;
        JsonObject jsonObj = (JsonObject)json;
        if (!jsonObj.has("type")) {
            ResourceLocation defaultRequirementType = PSTRegistries.SKILL_REQUIREMENTS.get().getKey((Object)((SkillRequirement.Serializer)PSTSkillRequirements.STAT_VALUE.get()));
            type = Objects.requireNonNull(defaultRequirementType).toString();
        } else {
            type = jsonObj.get("type").getAsString();
        }
        ResourceLocation serializerId = new ResourceLocation(type);
        SkillRequirement.Serializer serializer = (SkillRequirement.Serializer)PSTRegistries.SKILL_REQUIREMENTS.get().getValue(serializerId);
        Objects.requireNonNull(serializer, "Unknown skill bonus: " + serializerId);
        return (SkillRequirement)serializer.deserialize(jsonObj);
    }

    public JsonElement serialize(SkillRequirement<?> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        ResourceLocation serializerId = PSTRegistries.SKILL_REQUIREMENTS.get().getKey((Object)src.getSerializer());
        Objects.requireNonNull(serializerId);
        json.addProperty("type", serializerId.toString());
        src.getSerializer().serialize(json, src);
        return json;
    }
}

