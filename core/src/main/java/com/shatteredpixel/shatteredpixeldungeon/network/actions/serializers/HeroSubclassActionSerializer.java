package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeroSubclassAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class HeroSubclassActionSerializer extends NetworkActionSerializer<HeroSubclassAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull HeroSubclassAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("subclass_id", obj.subclassId);
        return actionObj;
    }
}
