package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeroReadyAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class HeroReadyActionSerializer extends NetworkActionSerializer<HeroReadyAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull HeroReadyAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("ready", obj.ready);
        return actionObj;
    }
}
