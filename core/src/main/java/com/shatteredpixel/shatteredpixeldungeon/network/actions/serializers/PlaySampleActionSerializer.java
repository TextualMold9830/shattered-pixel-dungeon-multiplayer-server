package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.PlaySampleAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class PlaySampleActionSerializer extends NetworkActionSerializer<PlaySampleAction>{
    @Override
    protected JSONObject serializeInternal(@NotNull PlaySampleAction obj, @NotNull SerializationContext ctx, @NotNull String profile) {
        JSONObject object = new JSONObject();
        object.put("sample", obj.id);
        object.put("left_volume", obj.leftVolume);
        object.put("right_volume", obj.rightVolume);
        object.put("rate", obj.rate);
        if (obj.delay != null) {
            object.put("delay", (float) obj.delay);
        }
        object.put("pitch", obj.pitch);
        return object;
    }
}
