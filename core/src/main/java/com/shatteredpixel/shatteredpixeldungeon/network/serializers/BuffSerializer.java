package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.nikita22007.multiplayer.utils.Text;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.watabou.noosa.Image;
import org.json.JSONException;
import org.json.JSONObject;

public class BuffSerializer implements Serializer<Buff> {

    @Override
    public Object serialize(Buff buff, SerializationContext ctx, String profile) {
        JSONObject buffObj = new JSONObject();
        int id = buff.id();
        boolean remove = "removed".equals(profile);
        
        try {
            buffObj.put("id", id);
            buffObj.put("icon", buff.icon());
            Actor target = buff.target;
            buffObj.put("target_id", (target == null || remove) ? JSONObject.NULL : target.id());
            buffObj.put("desc", Text.of(buff, "desc").toJSON());
            buffObj.put("name", Text.of(buff, "name").toJSON());
            
            Image temp = new Image();
            buff.tintIcon(temp);
            JSONObject hardlight = new JSONObject();
            hardlight.put("rm", temp.rm);
            hardlight.put("gm", temp.gm);
            hardlight.put("bm", temp.bm);
            buffObj.put("hardlight", hardlight);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return buffObj;
    }
}