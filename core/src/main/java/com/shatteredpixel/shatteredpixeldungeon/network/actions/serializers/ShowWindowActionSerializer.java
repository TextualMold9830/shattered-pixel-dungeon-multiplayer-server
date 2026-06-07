package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.ShowWindowAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class ShowWindowActionSerializer extends NetworkActionSerializer<ShowWindowAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull ShowWindowAction action, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject object = new JSONObject();
        object.put("id", action.windowID);
        object.put("type", action.type);
        if (action.args != null) {
            object.put("args", action.args);
        }
        return object;
    }
}
