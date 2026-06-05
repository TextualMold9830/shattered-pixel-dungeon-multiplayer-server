package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeroActorIdAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class HeroActorIdActionSerializer extends NetworkActionSerializer<HeroActorIdAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull HeroActorIdAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("actor_id", obj.actorId);
        return actionObj;
    }
}
