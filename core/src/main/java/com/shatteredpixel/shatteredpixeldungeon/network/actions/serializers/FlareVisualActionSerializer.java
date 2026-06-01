package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.FlareVisualAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class FlareVisualActionSerializer extends NetworkActionSerializer<FlareVisualAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull FlareVisualAction obj, SerializationContext ctx, String profile) {
        JSONObject actionObj = new JSONObject();
        if (obj.positionX != null && obj.positionY != null) {
            actionObj.put("position_x", obj.positionX);
            actionObj.put("position_y", obj.positionY);
        } else {
            actionObj.put("pos", obj.pos);
        }
        actionObj.put("color", obj.color);
        actionObj.put("duration", obj.duration);
        actionObj.put("light_mode", obj.lightMode);
        actionObj.put("rays", obj.rays);
        actionObj.put("radius", obj.radius);
        actionObj.put("angle", obj.angle);
        actionObj.put("angular_speed", obj.angularSpeed);
        return actionObj;
    }
}
