package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.PlantCache;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.PlantDTO;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class PlantSerializer implements Serializer<PlantDTO> {

    @Override
    public Object serialize(@NotNull PlantDTO dto, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject plantObj = new JSONObject();
        try {
            plantObj.put("pos", dto.pos);
            plantObj.put("texture", "plants.png");
            boolean wasCached = PlantCache.contains(dto.pos);
            if (dto.plant == null) {
                if (!wasCached) {
                    return null;
                }
                PlantCache.remove(dto.pos);
                plantObj.put("plant_info", JSONObject.NULL);
            } else {
                PlantCache.add(dto.pos);
                JSONObject plantInfoObj = new JSONObject();
                plantInfoObj.put("sprite_id", dto.plant.image);
                plantInfoObj.put("name", dto.plant.name());
                plantInfoObj.put("desc", dto.plant.desc());
                plantObj.put("plant_info", plantInfoObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return plantObj;
    }
}
