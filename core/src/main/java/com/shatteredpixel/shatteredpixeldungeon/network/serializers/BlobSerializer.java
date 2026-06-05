package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlobSerializer implements Serializer<Blob> {

    @Override
    public Object serialize(Blob blob, SerializationContext ctx, String profile) {
        JSONObject object = new JSONObject();
        if (blob.cur == null) {
            return object;
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
        }

        return object;
    }
}
