package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.WoundVisualAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class WoundVisualActionSerializer extends NetworkActionSerializer<WoundVisualAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull WoundVisualAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("pos", obj.pos);
        actionObj.put("time_to_fade", obj.timeToFade);
        return actionObj;
    }
}
