package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.DiscoverTileAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class DiscoverTileActionSerializer extends NetworkActionSerializer<DiscoverTileAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull DiscoverTileAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("pos", obj.pos);
        actionObj.put("old_tile", obj.oldValue);
        return actionObj;
    }
}
