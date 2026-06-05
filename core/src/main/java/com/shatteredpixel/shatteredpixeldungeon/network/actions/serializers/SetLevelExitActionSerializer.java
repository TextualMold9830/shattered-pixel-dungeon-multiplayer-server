package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.SetLevelExitAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class SetLevelExitActionSerializer extends NetworkActionSerializer<SetLevelExitAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull SetLevelExitAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("pos", obj.pos);
        return actionObj;
    }
}
