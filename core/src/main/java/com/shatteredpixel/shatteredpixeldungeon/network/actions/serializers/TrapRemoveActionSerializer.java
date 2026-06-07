package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.TrapRemoveAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class TrapRemoveActionSerializer extends NetworkActionSerializer<TrapRemoveAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull TrapRemoveAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject trapObj = new JSONObject();
        trapObj.put("pos", obj.pos);
        return trapObj;
    }
}
