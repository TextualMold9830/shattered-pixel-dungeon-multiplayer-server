package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeroStrengthAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class HeroStrengthActionSerializer extends NetworkActionSerializer<HeroStrengthAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull HeroStrengthAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("strength", obj.strength);
        return actionObj;
    }
}
