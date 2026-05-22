package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LevelSerializer implements Serializer<Level> {

    @Override
    public Object serialize(Level level, SerializationContext ctx, String profile) {
        try {
            switch (profile) {
                case "resize_level": {
                    JSONObject obj = new JSONObject();
                    obj.put("width", level.width());
                    obj.put("height", level.height());
                    return obj;
                }
                case "set_level_visuals": {
                    JSONObject obj = new JSONObject();
                    obj.put("tiles_texture", level.tilesTex());
                    obj.put("water_texture", level.waterTex());
                    obj.put("feeling", level.feeling.name());
                    return obj;
                }
                case "set_level_tiles": {
                    JSONArray arr = new JSONArray();
                    for (int tile : level.map) {
                        arr.put(tile);
                    }
                    return arr;
                }
                case "set_level_states": {
                    JSONArray arr = new JSONArray();
                    // Original logic merged visited and mapped into one int/state
                    for (int i = 0; i < level.length(); i++) {
                        int state = 0; // UNVISITED
                        if (level.visited[i]) state = 1; // VISITED
                        else if (level.mapped[i]) state = 2; // MAPPED
                        arr.put(state);
                    }
                    return arr;
                }
                default:
                    return JSONObject.NULL;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return JSONObject.NULL;
        }
    }
}
