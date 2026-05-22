package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import org.json.JSONException;
import org.json.JSONObject;

public class HeroSerializer implements Serializer<Hero> {

    @Override
    public Object serialize(Hero hero, SerializationContext ctx, String profile) {
        // Here we handle the specific "hero" block (which sits alongside the "actor" block in the JSON packet)
        // If we just want the base actor info, we can use the parent serializer.
        // But original code had `packHero` which produced a specific layout for the "hero" JSON key.
        
        int id = hero.id();
        if (id <= 0) {
            return new JSONObject();
        }
        
        JSONObject object = new JSONObject();
        String class_name = hero.heroClass.name();
        int subclass_id = hero.subClass.ordinal();
        int strength = hero.STR();
        int lvl = hero.lvl;
        int exp = hero.exp;
        
        try {
            object.put("actor_id", id);
            object.put("class", class_name);
            object.put("subclass_id", subclass_id);
            object.put("strength", strength);
            object.put("lvl", lvl);
            object.put("exp", exp);
            object.put("uuid", hero.uuid);
            object.put("talents", hero.getTalents());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
}