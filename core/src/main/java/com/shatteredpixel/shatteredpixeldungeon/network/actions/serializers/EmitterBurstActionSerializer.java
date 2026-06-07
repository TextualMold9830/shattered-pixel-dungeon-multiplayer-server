package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.EmitterBurstAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters.BaseEmitterSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class EmitterBurstActionSerializer extends NetworkActionSerializer<EmitterBurstAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull EmitterBurstAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = BaseEmitterSerializer.baseObject("emitter_burst", obj.emitter, ctx);
        return actionObj != null ? actionObj : new JSONObject();
    }
}
