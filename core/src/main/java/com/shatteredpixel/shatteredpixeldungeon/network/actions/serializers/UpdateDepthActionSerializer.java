package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.UpdateDepthAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class UpdateDepthActionSerializer extends NetworkActionSerializer<UpdateDepthAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull UpdateDepthAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("depth", obj.depth);
        actionObj.put("branch", obj.branch);
        actionObj.put("feeling", obj.feeling.name());
        return actionObj;
    }
}
