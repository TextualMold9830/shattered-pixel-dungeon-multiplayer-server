package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.NetworkAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.Serializer;
import org.jetbrains.annotations.CheckReturnValue;
import org.json.JSONObject;

public abstract class NetworkActionSerializer<T extends NetworkAction> implements Serializer<T> {

    @Override
    public final Object serialize(T obj, SerializationContext ctx, String profile) {
        JSONObject object =  this.serializeInternal(obj, ctx, profile);
        object.put("action_name", obj.actionName());
        return object;
    }

    @CheckReturnValue
    protected abstract JSONObject serializeInternal(T obj, SerializationContext ctx, String profile);

}
