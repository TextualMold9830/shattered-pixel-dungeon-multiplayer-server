package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeapUpdateAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class HeapUpdateActionSerializer extends NetworkActionSerializer<HeapUpdateAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull HeapUpdateAction action, @NotNull SerializationContext ctx, @NotNull String profile) {
        Object serialized = ctx.serialize(action.heap, "default");
        if (serialized instanceof JSONObject) {
            return (JSONObject) serialized;
        }
        return new JSONObject();
    }
}
