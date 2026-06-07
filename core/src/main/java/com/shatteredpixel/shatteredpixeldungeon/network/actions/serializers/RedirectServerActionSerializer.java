package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.RedirectServerAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class RedirectServerActionSerializer extends NetworkActionSerializer<RedirectServerAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull RedirectServerAction action, @NotNull SerializationContext ctx, @NotNull String profile) {
        return action.redirectPacket.toJSON();
    }
}
