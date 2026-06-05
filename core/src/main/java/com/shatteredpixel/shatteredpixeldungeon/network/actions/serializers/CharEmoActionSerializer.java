package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.CharEmoAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class CharEmoActionSerializer extends NetworkActionSerializer<CharEmoAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull CharEmoAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("actor_id", obj.actorId);
        actionObj.put("emotion", obj.emotion == null ? JSONObject.NULL : obj.emotion);
        return actionObj;
    }
}
