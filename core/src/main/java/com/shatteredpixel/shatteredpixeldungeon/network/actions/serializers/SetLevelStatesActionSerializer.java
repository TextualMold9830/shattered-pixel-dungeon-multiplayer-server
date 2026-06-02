package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.SetLevelStatesAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class SetLevelStatesActionSerializer extends NetworkActionSerializer<SetLevelStatesAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull SetLevelStatesAction action, SerializationContext ctx, String profile) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        var level = action.level;
        for (int i = 0; i < level.length(); i++) {
            int state = 0; // UNVISITED
            if (level.visited[i]) state = 1; // VISITED
            else if (level.mapped[i]) state = 2; // MAPPED
            arr.put(state);
        }
        obj.put("states", arr);
        return obj;
    }
}
