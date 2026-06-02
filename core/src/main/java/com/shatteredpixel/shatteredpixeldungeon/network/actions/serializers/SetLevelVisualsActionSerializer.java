package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.SetLevelVisualsAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class SetLevelVisualsActionSerializer extends NetworkActionSerializer<SetLevelVisualsAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull SetLevelVisualsAction action, SerializationContext ctx, String profile) {
        JSONObject obj = new JSONObject();
        obj.put("tiles_texture", action.tilesTexture);
        obj.put("water_texture", action.waterTexture);
        obj.put("feeling", action.feeling);
        return obj;
    }
}
