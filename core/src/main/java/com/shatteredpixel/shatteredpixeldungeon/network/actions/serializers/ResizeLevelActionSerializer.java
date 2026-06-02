package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ResizeLevelAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ResizeLevelActionSerializer extends NetworkActionSerializer<ResizeLevelAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull ResizeLevelAction action, SerializationContext ctx, String profile) {
        JSONObject obj = new JSONObject();
        var level = action.level;
        obj.put("width", level.width());
        obj.put("height", level.height());
        return obj;
    }
}
