package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.WindowDTO;
import org.json.JSONException;
import org.json.JSONObject;

public class WindowSerializer implements Serializer<WindowDTO> {

    @Override
    public Object serialize(WindowDTO window, SerializationContext ctx, String profile) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", window.windowID);
            obj.put("type", window.type);
            obj.put("args", window.args);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
