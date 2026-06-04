package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ShakeCameraAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.json.JSONObject;

public class ShakeCameraActionSerializer extends NetworkActionSerializer<ShakeCameraAction> {
    @Override
    public JSONObject serializeInternal(ShakeCameraAction obj, SerializationContext ctx, String profile) {
        JSONObject object = new JSONObject();
        object.put("magnitude", obj.magnitude);
        object.put("duration", obj.duration);
        return object;
    }
}
