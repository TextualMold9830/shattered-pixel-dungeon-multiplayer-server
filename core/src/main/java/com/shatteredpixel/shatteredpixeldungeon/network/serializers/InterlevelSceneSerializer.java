package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.InterlevelSceneDTO;
import org.json.JSONException;
import org.json.JSONObject;

public class InterlevelSceneSerializer implements Serializer<InterlevelSceneDTO> {

    @Override
    public Object serialize(InterlevelSceneDTO scene, SerializationContext ctx, String profile) {
        JSONObject sceneObj = new JSONObject();
        try {
            if (scene.state != null) {
                sceneObj.put("state", scene.state);
            }
            if (scene.mode != null) {
                sceneObj.put("type", scene.mode.name().toLowerCase());
            }
            if (scene.customMessage != null) {
                sceneObj.put("custom_message", scene.customMessage.toJsonObject());
            }
            if (scene.scrollSpeed != null) {
                sceneObj.put("scroll_speed", scene.scrollSpeed);
            }
            if (scene.loadingTexture != null) {
                sceneObj.put("loading_texture", scene.loadingTexture);
            }
            if (scene.fadeTime != null) {
                sceneObj.put("fade_time", scene.fadeTime.name().toLowerCase());
            }
            if (scene.resetLevel) {
                sceneObj.put("reset_level", true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sceneObj;
    }
}
