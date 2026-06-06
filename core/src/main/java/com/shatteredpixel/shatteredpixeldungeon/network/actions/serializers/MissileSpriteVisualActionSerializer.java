package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.MissileSpriteVisualAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class MissileSpriteVisualActionSerializer extends NetworkActionSerializer<MissileSpriteVisualAction> {

    @Override
    protected JSONObject serializeInternal(@NotNull MissileSpriteVisualAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject action = new JSONObject();

        action.put("from", ctx.serialize(obj.from));
        action.put("to", ctx.serialize(obj.to));
        action.put("speed", obj.speed);
        action.put("angular_speed", obj.angularSpeed);
        action.put("angle", obj.angle);
        action.put("flip_horizontal", obj.flipHorizontal);
        
        if (obj.item != null) {
            action.put("item", ctx.serialize(obj.item));
        } else {
            action.put("item", JSONObject.NULL);
        }

        return action;
    }
}
