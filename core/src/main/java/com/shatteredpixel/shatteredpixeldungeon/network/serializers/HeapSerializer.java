package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import org.json.JSONException;
import org.json.JSONObject;

public class HeapSerializer implements Serializer<Heap> {

    @Override
    public Object serialize(Heap heap, SerializationContext ctx, String profile) {
        if (heap == null || heap.isEmpty()) {
            return JSONObject.NULL;
        }
        
        JSONObject heapObj = new JSONObject();
        
        try {
            heapObj.put("pos", heap.pos);
            
            // We ask the context to serialize the visual item with the "ground" profile.
            // This means the ItemSerializer will automatically know to skip actions/info.
            Object serializedItem = ctx.serialize(heap.peekVisual(), "ground");
            heapObj.put("visible_item", serializedItem);
            
            heapObj.put("show_item", true);
            heapObj.put("seen", heap.isSeen());
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return heapObj;
    }
}
