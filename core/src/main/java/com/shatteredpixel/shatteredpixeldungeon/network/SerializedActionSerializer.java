package com.shatteredpixel.shatteredpixeldungeon.network;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers.NetworkActionSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class SerializedActionSerializer extends NetworkActionSerializer<NetworkPacket.SerializedAction> {

    @Override
    protected JSONObject serializeInternal(NetworkPacket.@NotNull SerializedAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        return obj.actionObj();
    }
}
