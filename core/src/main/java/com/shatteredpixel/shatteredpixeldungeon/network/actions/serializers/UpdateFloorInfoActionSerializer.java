package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.UpdateFloorInfoAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class UpdateFloorInfoActionSerializer extends NetworkActionSerializer<UpdateFloorInfoAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull UpdateFloorInfoAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("depth", obj.depth);
        actionObj.put("branch", obj.branch);
        actionObj.put("feeling", obj.feeling.name());
        return actionObj;
    }
}
