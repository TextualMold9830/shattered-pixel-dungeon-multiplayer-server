package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.network.SpecialSlot;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BelongingsSerializer implements Serializer<Belongings> {

    @Override
    public Object serialize(Belongings belongings, SerializationContext ctx, String profile) {

        try {
            // Default or "rebuild" profile
            JSONObject payload = new JSONObject();
            
            // Use context to serialize backpack and items
            payload.put("backpack", ctx.serialize(belongings.backpack));
            
            return payload;

        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}
