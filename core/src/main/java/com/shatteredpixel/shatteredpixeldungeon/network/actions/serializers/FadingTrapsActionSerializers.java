package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.network.actions.FadingTrapsAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class FadingTrapsActionSerializers {

    @Contract(value = "-> fail", pure = true)
    private FadingTrapsActionSerializers() {
        throw new RuntimeException();
    }

    public static class Update extends NetworkActionSerializer<FadingTrapsAction.Update> {
        @Override
        protected JSONObject serializeInternal(@NotNull FadingTrapsAction.Update obj, @NotNull SerializationContext ctx, @NotNull String profile) {
            JSONObject object = new JSONObject();
            object.put("tileX", obj.tileX);
            object.put("tileY", obj.tileY);
            object.put("tileH", obj.tileH);
            object.put("tileW", obj.tileW);
            object.put("alpha", obj.alpha);
            object.put("new", obj.isNew);
            
            JSONArray trapData = new JSONArray();
            try {
                for (int i = 0; i < obj.data.length; i++) {
                    JSONObject item = new JSONObject();
                    item.put("pos", i);
                    item.put("data", obj.data[i]);
                    trapData.put(item);
                }
            } catch (JSONException ignored) {}
            
            object.put("data", trapData);
            return object;
        }
    }

    public static class Kill extends NetworkActionSerializer<FadingTrapsAction.Kill> {
        @Override
        protected JSONObject serializeInternal(@NotNull FadingTrapsAction.Kill obj, @NotNull SerializationContext ctx, @NotNull String profile) {
            JSONObject object = new JSONObject();
            object.put("kill", true);
            return object;
        }
    }
}
