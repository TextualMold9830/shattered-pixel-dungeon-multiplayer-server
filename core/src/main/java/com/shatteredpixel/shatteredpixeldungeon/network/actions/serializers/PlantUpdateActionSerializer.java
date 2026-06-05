package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.PlantUpdateAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class PlantUpdateActionSerializer extends NetworkActionSerializer<PlantUpdateAction> {
    @Override
    protected JSONObject serializeInternal(@NotNull PlantUpdateAction obj, SerializationContext ctx, String profile) {
        JSONObject plantObj = new JSONObject();
        plantObj.put("pos", obj.pos);
        plantObj.put("texture", "plants.png");

        JSONObject plantInfoObj = new JSONObject();
        plantInfoObj.put("sprite_id", obj.plant.image);
        plantInfoObj.put("name", obj.plant.name());
        plantInfoObj.put("desc", obj.plant.desc());
        plantObj.put("plant_info", plantInfoObj);

        return plantObj;
    }
}
