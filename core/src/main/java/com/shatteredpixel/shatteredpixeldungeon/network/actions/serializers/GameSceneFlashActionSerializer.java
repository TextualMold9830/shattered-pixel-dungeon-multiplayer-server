package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.GameSceneFlashAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class GameSceneFlashActionSerializer extends NetworkActionSerializer<GameSceneFlashAction> {
    @Override
    public JSONObject serializeInternal(@NotNull GameSceneFlashAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject object  = new JSONObject();
        object.put("color", obj.color);
        object.put("light", obj.light);
        return object;
    }
}
