package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.InterlevelSceneDTO;
import org.json.JSONException;
import org.json.JSONObject;

public class InterlevelSceneSerializer implements Serializer<InterlevelSceneDTO> {

    @Override
    public Object serialize(InterlevelSceneDTO scene, SerializationContext ctx, String profile) {
        JSONObject stateObj = new JSONObject();
        try {
            stateObj.put("state", scene.state);
            if (scene.customMessage != null) {
                stateObj.put("custom_message", scene.customMessage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stateObj;
    }
}
