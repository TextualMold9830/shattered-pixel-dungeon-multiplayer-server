package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.LockedFloorStateAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class LockedFloorStateActionSerializer extends NetworkActionSerializer<LockedFloorStateAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull LockedFloorStateAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("locked", obj.locked);
        return actionObj;
    }
}
