package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.PlantCache;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.PlantDTO;
import org.json.JSONException;
import org.json.JSONObject;

public class PlantSerializer implements Serializer<PlantDTO> {

    @Override
    public Object serialize(PlantDTO dto, SerializationContext ctx, String profile) {
        JSONObject plantObj = new JSONObject();
        try {
            plantObj.put("pos", dto.pos);
            plantObj.put("texture", "plants.png");
            if (dto.plant == null) {
                plantObj.put("plant_info", JSONObject.NULL);
            } else {
                PlantCache.add(dto.pos);
                JSONObject plantInfoObj = new JSONObject();
                plantInfoObj.put("sprite_id", dto.plant.image);
                plantInfoObj.put("name", dto.plant.name());
                plantInfoObj.put("desc", dto.plant.desc());
                plantObj.put("plant_info", plantInfoObj);
            }
            
            // Note: The caching/filtering logic from NetworkPacket 
            // should probably be kept in the caller or here. We replicate
            // the condition here: if not in PlantCache, we don't send it, 
            // but the serializer contract typically returns the object. 
            // We'll return it and let NetworkPacket handle the cache removal 
            // or we do it here. 
            // The original logic checks PlantCache.contains(pos) BEFORE adding.
            if (!PlantCache.contains(dto.pos)) {
                return null; // Return null if it shouldn't be added.
            }
            if (dto.plant == null) {
                PlantCache.remove(dto.pos);
            }
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return plantObj;
    }
}