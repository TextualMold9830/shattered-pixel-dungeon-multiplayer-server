package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeroTalentsAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class HeroTalentsActionSerializer extends NetworkActionSerializer<HeroTalentsAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull HeroTalentsAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("talents", obj.talents);
        return actionObj;
    }
}
