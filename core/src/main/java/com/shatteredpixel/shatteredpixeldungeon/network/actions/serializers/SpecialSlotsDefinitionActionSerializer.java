package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.SpecialSlotsDefinitionAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class SpecialSlotsDefinitionActionSerializer extends NetworkActionSerializer<SpecialSlotsDefinitionAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull SpecialSlotsDefinitionAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        SerializationContext innerCtx = new SerializationContext(Server.SERIALIZERS, obj.hero);
        Object payload = innerCtx.serialize(obj.hero.belongings, "special_slot_definitions");

        JSONObject actionObj = new JSONObject();
        actionObj.put("slots", payload);
        return actionObj;
    }
}
