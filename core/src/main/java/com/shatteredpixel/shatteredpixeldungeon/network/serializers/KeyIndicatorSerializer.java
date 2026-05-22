package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.KeyIndicatorDTO;
import org.json.JSONArray;
import org.json.JSONObject;

public class KeyIndicatorSerializer implements Serializer<KeyIndicatorDTO> {

    @Override
    public Object serialize(KeyIndicatorDTO dto, SerializationContext ctx, String profile) {
        JSONArray arr = new JSONArray();
        if (dto.keys != null) {
            for (int key : dto.keys) {
                arr.put(key);
            }
        }
        return arr;
    }
}
