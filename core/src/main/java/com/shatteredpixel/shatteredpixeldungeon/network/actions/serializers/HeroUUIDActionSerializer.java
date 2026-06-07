package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeroUUIDAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class HeroUUIDActionSerializer extends NetworkActionSerializer<HeroUUIDAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull HeroUUIDAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("uuid", obj.uuid);
        return actionObj;
    }
}
