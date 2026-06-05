package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.EmitterPourAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters.BaseEmitterSerializer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class EmitterPourActionSerializer extends NetworkActionSerializer<EmitterPourAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull EmitterPourAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = BaseEmitterSerializer.baseObject("emitter_pour", obj.emitter, ctx);
        if (actionObj != null) {
            actionObj.put("id", obj.emitter.networkId());
            return actionObj;
        }
        return new JSONObject();
    }
}
