package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.SetLevelTilesAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

public class SetLevelTilesActionSerializer extends NetworkActionSerializer<SetLevelTilesAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull SetLevelTilesAction action, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        for (int tile : action.tiles) {
            arr.put(tile);
        }
        obj.put("tiles", arr);
        return obj;
    }
}
