package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.PlantDTO;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class PlantRemovalSerializer implements Serializer<PlantDTO> {

    @Override
    public Object serialize(@NotNull PlantDTO dto, @NotNull SerializationContext ctx, @NotNull String profile) {
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
