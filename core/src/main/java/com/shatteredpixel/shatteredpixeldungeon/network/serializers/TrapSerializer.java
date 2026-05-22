package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.TrapCache;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.TrapDTO;
import org.json.JSONException;
import org.json.JSONObject;

public class TrapSerializer implements Serializer<TrapDTO> {

    @Override
    public Object serialize(TrapDTO dto, SerializationContext ctx, String profile) {
        JSONObject trapObj = new JSONObject();
        try {
            trapObj.put("pos", dto.pos);
            if (dto.trap == null || !dto.trap.visible) {
                trapObj.put("trap_info", JSONObject.NULL);
            } else {
                TrapCache.add(dto.pos);
                JSONObject trapInfoObj = new JSONObject();
                trapInfoObj.put("shape", dto.trap.shape);
                trapInfoObj.put("color", dto.trap.color);
                trapInfoObj.put("active", dto.trap.active);
                trapInfoObj.put("name", dto.trap.getClass().getSimpleName());
                trapObj.put("trap_info", trapInfoObj);
            }
            
            if (!TrapCache.contains(dto.pos)) {
                return null;
            }
            if (dto.trap == null) {
                TrapCache.remove(dto.pos);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trapObj;
    }
}