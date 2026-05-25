package com.shatteredpixel.shatteredpixeldungeon.network.text;

import com.nikita22007.multiplayer.utils.text.LocalizedString;

import org.json.JSONArray;
import org.json.JSONObject;

public class LocalizedStringSerializer {

    private final LocalizedKeySerializer keySerializer = new LocalizedKeySerializer();

    public JSONObject serialize(LocalizedString text) {
        JSONObject object = new JSONObject();
        object.put("mode", text.mode().name().toLowerCase());
        if (text.mode() == LocalizedString.Mode.KEY) {
            object.put("key", keySerializer.serialize(text.key()));
            object.put("args", serializeArgs(text.args()));
        } else if (text.mode() == LocalizedString.Mode.RAW) {
            object.put("raw", text.raw());
            object.put("args", serializeArgs(text.args()));
        } else if (text.mode() == LocalizedString.Mode.TRANSFORM) {
            object.put("transform", text.transform().name().toLowerCase());
            object.put("text", serialize(text.text()));
        } else if (text.mode() == LocalizedString.Mode.CONCAT) {
            object.put("parts", serializeArgs(text.parts()));
        }
        return object;
    }

    private JSONArray serializeArgs(Object[] args) {
        JSONArray array = new JSONArray();
        for (Object arg : args) {
            if (arg instanceof LocalizedString) {
                array.put(serialize((LocalizedString) arg));
            } else if (arg == null) {
                array.put(JSONObject.NULL);
            } else {
                array.put(arg);
            }
        }
        return array;
    }
}
