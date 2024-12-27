package com.nikita22007.multiplayer.server.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import org.json.JSONException;
import org.json.JSONObject;

public class Surprise {

    private static final float TIME_TO_FADE = 1f;

    private float time;

    @Deprecated
    public Surprise() {
        throw new RuntimeException();
    }

    public static void reset(int p, float angle) {
        JSONObject actionObj = new JSONObject();
        try {
            actionObj.put("action_type", "surprise_visual");
            actionObj.put("pos", p);
            actionObj.put("angle", angle);
            actionObj.put("time_to_fade", TIME_TO_FADE);
        } catch (JSONException ignore) {
        }
        SendData.sendCustomActionForAll(actionObj);
    }
    public static void hit(Char ch) {
        hit(ch, 0);
    }

    public static void hit(Char ch, float angle) {
        reset(ch.pos, angle);
    }

    public static void hit(int pos) {
        reset(pos, 0);
    }
}
