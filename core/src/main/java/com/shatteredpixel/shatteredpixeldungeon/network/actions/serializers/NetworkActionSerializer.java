package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.LiveStateNetworkAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.Serializer;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public abstract class NetworkActionSerializer<T extends LiveStateNetworkAction> implements Serializer<T> {

    @Override
    public final Object serialize(@NotNull T obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject object =  this.serializeInternal(obj, ctx, profile);
        object.put("action_name", obj.actionName());
        return object;
    }

    @CheckReturnValue
    protected abstract JSONObject serializeInternal(@NotNull T obj, @NotNull SerializationContext ctx, @NotNull String profile);

}
