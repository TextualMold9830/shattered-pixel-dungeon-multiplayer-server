package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ShakeCameraAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ShakeCameraActionSerializer extends NetworkActionSerializer<ShakeCameraAction> {
    @Override
    public JSONObject serializeInternal(@NotNull ShakeCameraAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject object = new JSONObject();
        object.put("magnitude", obj.magnitude);
        object.put("duration", obj.duration);
        return object;
    }
}
