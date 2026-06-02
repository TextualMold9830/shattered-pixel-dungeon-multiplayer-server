package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.SpriteFlashAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class SpriteFlashActionSerializer extends NetworkActionSerializer<SpriteFlashAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull SpriteFlashAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("actor_id", obj.actorId);
        actionObj.put("flash_time", obj.flashTime);
        return actionObj;
    }
}
