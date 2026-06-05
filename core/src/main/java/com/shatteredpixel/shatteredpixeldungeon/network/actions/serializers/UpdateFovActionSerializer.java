package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.UpdateFovAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class UpdateFovActionSerializer extends NetworkActionSerializer<UpdateFovAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull UpdateFovAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONArray visiblePos = new JSONArray();
        boolean[] visible = obj.visible();
        for (int i = 0; i < visible.length; i++) {
            if (visible[i]) {
                visiblePos.put(i);
            }
        }

        JSONObject actionObj = new JSONObject();
        actionObj.put("visible_pos", visiblePos);
        return actionObj;
    }
}
