package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.BuffRemoveAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class BuffRemoveActionSerializer extends NetworkActionSerializer<BuffRemoveAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull BuffRemoveAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject buffObj = new JSONObject();
        buffObj.put("id", obj.buffId);
        return buffObj;
    }
}
