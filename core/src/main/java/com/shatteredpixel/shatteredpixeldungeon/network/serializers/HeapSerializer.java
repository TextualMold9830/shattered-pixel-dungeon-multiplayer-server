package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class HeapSerializer implements Serializer<Heap> {

    @Override
    public Object serialize(@NotNull Heap heap, @NotNull SerializationContext ctx, @NotNull String profile) {
        if (heap == null || heap.isEmpty()) {
            return JSONObject.NULL;
        }
        
        JSONObject heapObj = new JSONObject();
        
        try {
            heapObj.put("pos", heap.pos);

            Object serializedItem = ctx.serialize(heap.peekVisual(), "ground");
            heapObj.put("visible_item", serializedItem);
            heapObj.put("visible_sprite", heap.showsFirstItem() ? heap.image() : -1);
            heapObj.put("show_item", heap.showsFirstItem());
            heapObj.put("seen", heap.isSeen());
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return heapObj;
    }
}
