package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.PlantDTO;
import org.json.JSONException;
import org.json.JSONObject;

public class PlantRemovalSerializer implements Serializer<PlantDTO> {

    @Override
    public Object serialize(PlantDTO dto, SerializationContext ctx, String profile) {
        JSONObject plantObj = new JSONObject();
        try {
            plantObj.put("pos", dto.pos);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return plantObj;
    }
}
