package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.EnchantingVisualAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class EnchantingVisualActionSerializer extends NetworkActionSerializer<EnchantingVisualAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull EnchantingVisualAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("target", obj.targetId);
        actionObj.put("item", ctx.serialize(obj.item, "inventory"));
        return actionObj;
    }
}
