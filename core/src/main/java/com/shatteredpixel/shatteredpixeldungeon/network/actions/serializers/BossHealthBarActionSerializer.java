package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.BossHealthBarAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class BossHealthBarActionSerializer extends NetworkActionSerializer<BossHealthBarAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull BossHealthBarAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("id", obj.id);
        actionObj.put("bleeding", obj.bleeding);
        return actionObj;
    }
}
