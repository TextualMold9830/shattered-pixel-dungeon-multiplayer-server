package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.EmitterStartAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters.BaseEmitterSerializer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class EmitterStartActionSerializer extends NetworkActionSerializer<EmitterStartAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull EmitterStartAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = BaseEmitterSerializer.baseObject("emitter_start", obj.emitter, ctx);
        return actionObj != null ? actionObj : new JSONObject();
    }
}
