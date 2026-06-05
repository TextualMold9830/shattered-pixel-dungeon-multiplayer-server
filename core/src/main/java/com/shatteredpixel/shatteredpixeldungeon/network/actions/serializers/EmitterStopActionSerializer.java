package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.EmitterStopAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class EmitterStopActionSerializer extends NetworkActionSerializer<EmitterStopAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull EmitterStopAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("id", obj.id);
        return actionObj;
    }
}
