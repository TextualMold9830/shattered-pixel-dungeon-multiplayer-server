package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ActorRemoveAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.json.JSONObject;

public class ActorRemoveActionSerializer extends NetworkActionSerializer<ActorRemoveAction> {
    @Override
    protected JSONObject serializeInternal(ActorRemoveAction obj, SerializationContext ctx, String profile) {
        JSONObject object = new JSONObject();
        object.put("id", obj.actorId);
        return object;
    }
}
