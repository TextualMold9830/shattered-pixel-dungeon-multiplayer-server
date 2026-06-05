package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.AttackIndicatorTargetAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class AttackIndicatorTargetActionSerializer extends NetworkActionSerializer<AttackIndicatorTargetAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull AttackIndicatorTargetAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("target", obj.target);
        return actionObj;
    }
}
