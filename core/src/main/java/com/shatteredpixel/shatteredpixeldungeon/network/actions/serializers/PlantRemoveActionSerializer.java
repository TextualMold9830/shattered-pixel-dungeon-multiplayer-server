package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.PlantRemoveAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class PlantRemoveActionSerializer extends NetworkActionSerializer<PlantRemoveAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull PlantRemoveAction obj, SerializationContext ctx, String profile) {
        JSONObject plantObj = new JSONObject();
        plantObj.put("pos", obj.pos);
        return plantObj;
    }
}
