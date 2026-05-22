package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class SerializationContext {
    private final SerializerRegistry registry;
    @Nullable
    public final Hero observer;

    public SerializationContext(SerializerRegistry registry, @Nullable Hero observer) {
        this.registry = registry;
        this.observer = observer;
    }

    public Object serialize(Object obj) {
        return serialize(obj, "default");
    }

    public Object serialize(Object obj, String profile) {
        if (obj == null) return JSONObject.NULL;
        return serializeAs(obj, obj.getClass(), profile);
    }

    public Object serializeAsParent(Object obj) {
        return serializeAsParent(obj, "default");
    }

    public Object serializeAsParent(Object obj, String profile) {
        if (obj == null) return JSONObject.NULL;
        Class<?> parent = obj.getClass().getSuperclass();
        if (parent == null) {
            throw new IllegalArgumentException("Cannot serialize as parent: " + obj.getClass() + " has no superclass.");
        }
        return serializeAs(obj, parent, profile);
    }

    public Object serializeAs(Object obj, Class<?> asClass) {
        return serializeAs(obj, asClass, "default");
    }

    public Object serializeAs(Object obj, Class<?> asClass, String profile) {
        if (obj == null) return JSONObject.NULL;

        @SuppressWarnings("unchecked")
        Serializer<Object> serializer = (Serializer<Object>) registry.get(asClass, profile);

        if (serializer == null) {
            throw new IllegalArgumentException("No serializer found for " + asClass + " with profile '" + profile + "'");
        }

        return serializer.serialize(obj, this, profile);
    }
}
