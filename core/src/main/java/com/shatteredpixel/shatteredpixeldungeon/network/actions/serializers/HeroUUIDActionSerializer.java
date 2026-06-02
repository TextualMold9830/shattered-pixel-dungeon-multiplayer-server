package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeroUUIDAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class HeroUUIDActionSerializer extends NetworkActionSerializer<HeroUUIDAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull HeroUUIDAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("uuid", obj.uuid);
        return actionObj;
    }
}
