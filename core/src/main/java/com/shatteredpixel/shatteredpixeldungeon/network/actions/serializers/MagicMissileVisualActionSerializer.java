package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.MagicMissileVisualAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class MagicMissileVisualActionSerializer extends NetworkActionSerializer<MagicMissileVisualAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull MagicMissileVisualAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("type", obj.type);
        actionObj.put("from", obj.from);
        actionObj.put("to", obj.to);
        return actionObj;
    }
}
