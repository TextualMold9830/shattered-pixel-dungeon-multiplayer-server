package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.UpdateCounterAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class UpdateCounterActionSerializer extends NetworkActionSerializer<UpdateCounterAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull UpdateCounterAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("counter", obj.counter);
        return actionObj;
    }
}
