package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.RippleVisualAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class RippleVisualActionSerializer extends NetworkActionSerializer<RippleVisualAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull RippleVisualAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("pos", obj.pos);
        return actionObj;
    }
}
