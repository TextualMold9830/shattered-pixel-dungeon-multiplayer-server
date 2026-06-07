package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.CharSpriteAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class CharSpriteActionSerializer extends NetworkActionSerializer<CharSpriteAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull CharSpriteAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("action", obj.action);
        actionObj.put("from", obj.from);
        actionObj.put("to", obj.to);
        actionObj.put("actor_id", obj.actorId);
        return actionObj;
    }
}
