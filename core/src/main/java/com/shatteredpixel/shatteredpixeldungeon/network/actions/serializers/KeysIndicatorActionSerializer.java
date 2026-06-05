package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.KeysIndicatorAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class KeysIndicatorActionSerializer extends NetworkActionSerializer<KeysIndicatorAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull KeysIndicatorAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        JSONArray keysCount = new JSONArray();
        for (Integer count : obj.keysCount) {
            keysCount.put(count);
        }
        actionObj.put("keys_count", keysCount);
        return actionObj;
    }
}
