package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.SurpriseVisualAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class SurpriseVisualActionSerializer extends NetworkActionSerializer<SurpriseVisualAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull SurpriseVisualAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("action_name", "surprise_visual");
        actionObj.put("pos", obj.pos);
        actionObj.put("angle", obj.angle);
        actionObj.put("time_to_fade", obj.timeToFade);
        return actionObj;
    }
}
