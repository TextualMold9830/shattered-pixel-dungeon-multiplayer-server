package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.InventoryRebuildAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.Server;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class InventoryRebuildActionSerializer extends NetworkActionSerializer<InventoryRebuildAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull InventoryRebuildAction obj, SerializationContext ctx, String profile) {
        SerializationContext innerCtx = new SerializationContext(Server.SERIALIZERS, obj.hero);
        Object payload = innerCtx.serialize(obj.hero.belongings, "rebuild");

        if (payload instanceof JSONObject) {
            return (JSONObject) payload;
        }
        return new JSONObject();
    }
}
