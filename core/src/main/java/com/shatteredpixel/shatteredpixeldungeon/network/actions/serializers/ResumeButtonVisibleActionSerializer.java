package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ResumeButtonVisibleAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ResumeButtonVisibleActionSerializer extends NetworkActionSerializer<ResumeButtonVisibleAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull ResumeButtonVisibleAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("visible", obj.visible);
        return actionObj;
    }
}
