package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import org.json.JSONException;
import org.json.JSONObject;

public class HeapRemovalSerializer implements Serializer<Heap> {

    @Override
    public Object serialize(Heap heap, SerializationContext ctx, String profile) {
        JSONObject heapObj = new JSONObject();
        try {
            heapObj.put("pos", heap.pos);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return heapObj;
    }
}
