package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import org.json.JSONException;
import org.json.JSONObject;

public class BuffRemovalSerializer implements Serializer<Buff> {

    @Override
    public Object serialize(Buff buff, SerializationContext ctx, String profile) {
        JSONObject buffObj = new JSONObject();
        try {
            buffObj.put("id", buff.id());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return buffObj;
    }
}
