package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.network.SpecialSlot;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpecialSlotDefinitionsSerializer implements Serializer<Belongings> {

    @Override
    public Object serialize(Belongings belongings, SerializationContext ctx, String profile) {
        JSONArray slotsArr = new JSONArray();
        try {
            for (SpecialSlot slot : belongings.getSpecialSlots()) {
                JSONObject slotObj = new JSONObject();
                slotObj.put("id", slot.id);
                slotObj.put("sprite", slot.sprite);
                slotObj.put("image_id", slot.image_id);
                slotsArr.put(slotObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
        return slotsArr;
    }
}
