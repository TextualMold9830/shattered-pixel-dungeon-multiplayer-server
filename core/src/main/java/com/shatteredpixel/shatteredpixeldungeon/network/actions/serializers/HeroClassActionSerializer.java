package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeroClassAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class HeroClassActionSerializer extends NetworkActionSerializer<HeroClassAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull HeroClassAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("class", obj.className);
        return actionObj;
    }
}
