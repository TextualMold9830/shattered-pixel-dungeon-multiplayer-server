package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import org.json.JSONException;
import org.json.JSONObject;

public class ActorRemovalSerializer implements Serializer<Actor> {

    @Override
    public Object serialize(Actor actor, SerializationContext ctx, String profile) {
        JSONObject object = new JSONObject();
        try {
            if ((actor instanceof Char) || (actor instanceof Blob)) {
                int id = actor.id();
                if (id <= 0) return JSONObject.NULL;
                object.put("id", id);
            } else {
                Log.w("ActorRemovalSerializer", "pack actor removing. Actor class: " + actor.getClass().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
