package com.shatteredpixel.shatteredpixeldungeon.network.text;

import com.nikita22007.multiplayer.utils.text.LocalizedKey;

import org.json.JSONObject;

public class LocalizedKeySerializer {

    public JSONObject serialize(LocalizedKey key) {
        JSONObject object = new JSONObject();
        if (key.ownerClass() != null) {
            object.put("owner", key.ownerClass());
        }
        object.put("name", key.name());
        return object;
    }
}
