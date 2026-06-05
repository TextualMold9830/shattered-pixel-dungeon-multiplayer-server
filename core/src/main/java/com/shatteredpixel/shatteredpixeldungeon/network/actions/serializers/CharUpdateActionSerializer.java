package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.CharUpdateAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class CharUpdateActionSerializer extends NetworkActionSerializer<CharUpdateAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull CharUpdateAction action, SerializationContext ctx, String profile) {
        Object serialized = ctx.serialize(action.character, "default");
        if (serialized instanceof JSONObject) {
            return (JSONObject) serialized;
        }
        return new JSONObject();
    }
}
