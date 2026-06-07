package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeapRemoveAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class HeapRemoveActionSerializer extends NetworkActionSerializer<HeapRemoveAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull HeapRemoveAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("pos", obj.pos);
        return actionObj;
    }
}
