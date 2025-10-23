/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 */
package daripher.skilltree.data.serializers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public interface Serializer<T> {
    public T deserialize(JsonObject var1) throws JsonParseException;

    public void serialize(JsonObject var1, T var2);

    public T deserialize(CompoundTag var1);

    public CompoundTag serialize(T var1);

    public T deserialize(FriendlyByteBuf var1);

    public void serialize(FriendlyByteBuf var1, T var2);
}

