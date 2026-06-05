package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeroExperienceAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class HeroExperienceActionSerializer extends NetworkActionSerializer<HeroExperienceAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull HeroExperienceAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject actionObj = new JSONObject();
        actionObj.put("lvl", obj.lvl);
        actionObj.put("exp", obj.exp);
        return actionObj;
    }
}
