package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.network.SpecialSlot;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BelongingsSerializer implements Serializer<Belongings> {

    @Override
    public Object serialize(Belongings belongings, SerializationContext ctx, String profile) {
        Hero hero = ctx.observer;
        
        try {
            if ("special_slot_definitions".equals(profile)) {
                JSONArray slotsArr = new JSONArray();
                for (SpecialSlot slot : belongings.getSpecialSlots()) {
                    JSONObject slotObj = new JSONObject();
                    slotObj.put("id", slot.id);
                    slotObj.put("sprite", slot.sprite);
                    slotObj.put("image_id", slot.image_id);
                    slotsArr.put(slotObj);
                }
                return slotsArr;
            }

            // Default or "rebuild" profile
            JSONObject payload = new JSONObject();
            
            // Use context to serialize backpack and items
            payload.put("backpack", ctx.serialize(belongings.backpack));
            
            JSONArray slotsArr = new JSONArray();
            for (SpecialSlot slot : belongings.getSpecialSlots()) {
                JSONObject slotObj = new JSONObject();
                slotObj.put("id", slot.id);
                slotObj.put("sprite", slot.sprite);
                slotObj.put("image_id", slot.image_id);
                
                Object itemObj = ctx.serialize(slot.item);
                slotObj.put("item", itemObj != null ? itemObj : JSONObject.NULL);
                
                slotsArr.put(slotObj);
            }
            payload.put("special_slots", slotsArr);
            
            return payload;

        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}
