package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

public interface Serializer<T> {
    Object serialize(T obj, SerializationContext ctx, String profile);
}
