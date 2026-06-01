package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.GameSceneFlashAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.json.JSONObject;

public class GameSceneFlashActionSerializer extends NetworkActionSerializer<GameSceneFlashAction> {
    @Override
    public JSONObject serializeInternal(GameSceneFlashAction obj, SerializationContext ctx, String profile) {
        JSONObject object  = new JSONObject();
        object.put("color", obj.color);
        object.put("light", obj.light);
        return object;
    }
}
