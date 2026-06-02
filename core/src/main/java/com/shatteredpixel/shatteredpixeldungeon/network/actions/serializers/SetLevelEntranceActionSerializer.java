package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.SetLevelEntranceAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class SetLevelEntranceActionSerializer extends NetworkActionSerializer<SetLevelEntranceAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull SetLevelEntranceAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("pos", obj.pos);
        return actionObj;
    }
}
