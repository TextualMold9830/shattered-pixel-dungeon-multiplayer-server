package com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.BlobUpdateAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlobUpdateActionSerializer extends NetworkActionSerializer<BlobUpdateAction> {
    @Override
    protected @Nullable JSONObject serializeInternal(@NotNull BlobUpdateAction action, @NotNull SerializationContext ctx, @NotNull String profile) {
        final Blob blob = action.blob;

        JSONObject object = new JSONObject();
        if (blob.cur == null) {
            return null;
        }

        try {
            object.put("id", blob.id());
            object.put("blob_type", blob.getClass().getName());

            JSONArray positions = new JSONArray();
            for (int i = 0; i < blob.cur.length; i++) {
                if (blob.cur[i] > 0) {
                    positions.put(i);
                }
            }
            object.put("positions", positions);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }
}
