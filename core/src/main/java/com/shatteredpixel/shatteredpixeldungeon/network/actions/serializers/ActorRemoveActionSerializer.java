package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ActorRemoveAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ActorRemoveActionSerializer extends NetworkActionSerializer<ActorRemoveAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull ActorRemoveAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject object = new JSONObject();
        object.put("id", obj.actorId);
        return object;
    }
}
