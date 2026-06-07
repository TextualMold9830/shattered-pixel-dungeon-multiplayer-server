package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.TrapUpdateAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class TrapUpdateActionSerializer extends NetworkActionSerializer<TrapUpdateAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull TrapUpdateAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject trapObj = new JSONObject();
        trapObj.put("pos", obj.pos);
        trapObj.put("shape", obj.shape);
        trapObj.put("color", obj.color);
        trapObj.put("active", obj.active);
        return trapObj;
    }
}
