package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.nikita22007.multiplayer.noosa.audio.Music;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.MusicAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.Serializer;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class MusicActionSerializer extends NetworkActionSerializer<MusicAction> {

    @Override
    protected JSONObject serializeInternal(@NotNull MusicAction obj, SerializationContext ctx, String profile) {
        JSONObject object = new JSONObject();
        if(obj instanceof MusicAction.PlayAction){
            MusicAction.PlayAction action = (MusicAction.PlayAction) obj;
            object.put("asset", action.assetName);
            object.put("looping", action.looping);

        }
        if(obj instanceof MusicAction.PlayTracksAction){
            MusicAction.PlayTracksAction action = (MusicAction.PlayTracksAction) obj;
            object.put("chances", action.chances);
            object.put("tracks", action.tracks);
            object.put("shuffle", action.shuffle);
        }
        if(obj instanceof MusicAction.FadeOutAction){
            MusicAction.FadeOutAction action = (MusicAction.FadeOutAction) obj;
            if (action.callback != null) {
                object.put("callback", ctx.serialize(action.callback));
            }
            object.put("duration", action.duration);
        }
        //EndAction has no extra fields needed for serialization
        return object;
    }
}
