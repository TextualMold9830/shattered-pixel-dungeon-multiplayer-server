package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ClassSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TieredSprite;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.nikita22007.multiplayer.utils.Utils.putToJSONArray;

public class ActorSerializer implements Serializer<Actor> {

    @Override
    public Object serialize(Actor actor, SerializationContext ctx, String profile) {
        JSONObject object = new JSONObject();
        
        // Handling special "removed" profile
        if ("removed".equals(profile)) {
            try {
                if ((actor instanceof Char) || (actor instanceof Blob)) {
                    int id = actor.id();
                    if (id <= 0) return new JSONObject();
                    object.put("id", id);
                    object.put("type", "removed");
                } else {
                    Log.w("ActorSerializer", "pack actor removing. Actor class: " + actor.getClass().toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object;
        }

        // Standard serialization
        try {
            if (actor instanceof Char) {
                Char character = (Char) actor;
                int id = actor.id();
                if (id <= 0) {
                    return new JSONObject();
                }
                object.put("id", id);
                
                boolean heroAsHero = "hero".equals(profile);
                
                if (actor instanceof Hero) {
                    if (heroAsHero) {
                        object.put("type", "hero");
                    } else {
                        object.put("type", "character");
                    }
                } else {
                    object.put("type", "character");
                }

                if (character.getSprite() != null) {
                    String spriteAsset = character.getSprite().getSpriteAsset();
                    if (spriteAsset != null) {
                        object.put("sprite_asset", spriteAsset);
                    } else {
                        object.put("sprite_name", character.getSprite().spriteName());
                    }
                    if (character.getSprite() instanceof TieredSprite) {
                        object.put("tier", ((TieredSprite) character.getSprite()).tier());
                    }
                    if (character.getSprite() instanceof ClassSprite) {
                        object.put("class", ((ClassSprite) character.getSprite()).heroClass());
                    }
                }
                
                String name = character.name();
                int hp = character.getHP();
                int ht = character.getHT();
                int pos = character.pos;
                int shield = character.shielding();
                
                object.put("hp", hp);
                object.put("max_hp", ht);
                object.put("position", pos);
                object.put("name", name);
                
                if (shield > 0 || character.needsShieldUpdate()) {
                    object.put("shield", shield);
                }
                object.put("emo", character.getEmoJsonObject());
                
                CharSprite sprite = character.getSprite();
                if (sprite != null) {
                    JSONArray states = putToJSONArray(character.getSprite().states().toArray());
                    object.put("states", states);
                }
                if (actor instanceof Mob) {
                    String desc = ((Mob) actor).description();
                    object.put("description", desc);
                }
                
            } else if (actor instanceof Blob) {
                if (((Blob) actor).cur == null) {
                    return new JSONObject();
                }
                int id = actor.id();
                object.put("id", id);
                object.put("type", "blob");
                object.put("blob_type", actor.getClass().getName());
                JSONArray positions = new JSONArray();
                for (int i = 0; i < ((Blob) actor).cur.length; i++) {
                    if (((Blob) actor).cur[i] > 0) {
                        positions.put(i);
                    }
                }
                object.put("positions", positions);
            } else if (actor instanceof Buff) {
                //no warning needed according to original code
            } else {
                Log.w("ActorSerializer", "pack actor. Actor class: " + actor.getClass().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
}