package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.PlantRemoveAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class PlantRemoveActionSerializer extends NetworkActionSerializer<PlantRemoveAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull PlantRemoveAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject plantObj = new JSONObject();
        plantObj.put("pos", obj.pos);
        return plantObj;
    }
}
