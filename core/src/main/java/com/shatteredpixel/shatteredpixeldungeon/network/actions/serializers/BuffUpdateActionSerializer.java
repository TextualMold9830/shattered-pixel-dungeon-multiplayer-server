package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.nikita22007.multiplayer.utils.Text;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.BuffUpdateAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.watabou.noosa.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class BuffUpdateActionSerializer extends NetworkActionSerializer<BuffUpdateAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull BuffUpdateAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject buffObj = new JSONObject();
        Buff buff = obj.buff;
        int id = buff.id();
        
        try {
            buffObj.put("id", id);
            buffObj.put("icon", buff.icon());
            Actor target = buff.target;
            buffObj.put("target_id", target == null ? JSONObject.NULL : target.id());
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
            return new JSONObject();
        }

        return buffObj;
    }
}
