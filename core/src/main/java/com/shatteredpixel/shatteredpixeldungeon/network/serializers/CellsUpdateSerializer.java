package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.CellsUpdateDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CellsUpdateSerializer implements Serializer<CellsUpdateDTO> {

    @Override
    public Object serialize(CellsUpdateDTO dto, SerializationContext ctx, String profile) {
        JSONObject obj = new JSONObject();
        try {
            JSONArray posArr = new JSONArray();
            for (int p : dto.positions) posArr.put(p);
            obj.put("positions", posArr);

            if (dto.tiles != null) {
                JSONArray tilesArr = new JSONArray();
                for (int t : dto.tiles) tilesArr.put(t);
                obj.put("tiles", tilesArr);
            }

            if (dto.states != null) {
                JSONArray statesArr = new JSONArray();
                for (int s : dto.states) statesArr.put(s);
                obj.put("states", statesArr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
