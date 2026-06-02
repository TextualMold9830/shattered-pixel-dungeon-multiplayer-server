package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ShowStatusAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ShowStatusActionSerializer extends NetworkActionSerializer<ShowStatusAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull ShowStatusAction action, SerializationContext ctx, String profile) {
        JSONObject obj = new JSONObject();
        if (action.x != null) {
            obj.put("x", action.x);
        }
        if (action.y != null) {
            obj.put("y", action.y);
        }
        if (action.key != null) {
            obj.put("key", action.key);
        }
        obj.put("text", action.text);
        obj.put("color", action.color);
        obj.put("ignore_position", action.ignorePosition);
        return obj;
    }
}
