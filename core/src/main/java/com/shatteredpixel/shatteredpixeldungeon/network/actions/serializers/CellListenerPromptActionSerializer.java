package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.CellListenerPromptAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class CellListenerPromptActionSerializer extends NetworkActionSerializer<CellListenerPromptAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull CellListenerPromptAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("prompt", obj.prompt == null ? JSONObject.NULL : obj.prompt);
        return actionObj;
    }
}
