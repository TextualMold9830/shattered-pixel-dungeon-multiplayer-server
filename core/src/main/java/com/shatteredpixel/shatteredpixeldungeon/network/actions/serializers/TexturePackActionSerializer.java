package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.TexturePackAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class TexturePackActionSerializer extends NetworkActionSerializer<TexturePackAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull TexturePackAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("texturepack", obj.data);
        return actionObj;
    }
}
