package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.TrapDTO;
import org.json.JSONException;
import org.json.JSONObject;

public class TrapRemovalSerializer implements Serializer<TrapDTO> {

    @Override
    public Object serialize(TrapDTO dto, SerializationContext ctx, String profile) {
        JSONObject trapObj = new JSONObject();
        try {
            trapObj.put("pos", dto.pos);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return trapObj;
    }
}
