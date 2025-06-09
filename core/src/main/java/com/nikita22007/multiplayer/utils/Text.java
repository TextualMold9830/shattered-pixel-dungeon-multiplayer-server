package com.nikita22007.multiplayer.utils;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import org.json.JSONArray;
import org.json.JSONObject;

public class Text {
    String key;
    Object[] args;
    public static Text of(Object o, String key){
        Text text = new Text();
        Messages.getFirstValidKey(o, key);
        text.key = key;
        return text;
    }
    public JSONObject toJSON(){
        JSONObject object = new JSONObject();
        object.put("key", key);
        if (args != null){
            JSONArray argsArray = new JSONArray(args);
            object.put("args", argsArray);
        }
        return object;
    }
}
