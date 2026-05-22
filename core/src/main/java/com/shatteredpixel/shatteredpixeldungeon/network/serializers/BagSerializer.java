package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BagSerializer implements Serializer<Bag> {

    @Override
    public Object serialize(Bag bag, SerializationContext ctx, String profile) {
        // 1. Serialize as base Item first
        JSONObject bagObj = (JSONObject) ctx.serializeAsParent(bag, profile);

        if (bagObj == null) {
            return JSONObject.NULL;
        }

        Hero hero = ctx.observer;
        if ((bag.owner != null) && (bag.owner != hero)) {
            Log.w("Packet", "bag.owner != gotten_hero");
        }

        try {
            JSONArray bagItems = new JSONArray();

            for (Item item : bag.items) {
                // Pass the same profile down (e.g. "inventory") to inner items
                // This will make sure inner items serialize appropriately
                JSONObject serializedItem = (JSONObject) ctx.serialize(item, profile);
                if (serializedItem == null || serializedItem.length() == 0) {
                    Log.w("Packet", "item hadn't serialized");
                } else {
                    bagItems.put(serializedItem);
                }
            }

            bagObj.put("bag_icon", bag.getBagIcon());
            bagObj.put("size", bag.capacity());
            bagObj.put("items", bagItems);
            bagObj.put("owner", bag.owner != null ? bag.owner.id() : JSONObject.NULL);
            
        } catch (JSONException e) {
            Log.e("Packet", "JSONException inside BagSerializer. " + e.toString());
        }

        return bagObj;
    }
}
