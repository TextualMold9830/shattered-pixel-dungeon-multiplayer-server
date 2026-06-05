package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.SetLevelStatesAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class SetLevelStatesActionSerializer extends NetworkActionSerializer<SetLevelStatesAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull SetLevelStatesAction action, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        for (int state : action.states) {
            arr.put(state);
        }
        obj.put("states", arr);
        return obj;
    }
}
