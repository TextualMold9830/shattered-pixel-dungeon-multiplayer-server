package com.shatteredpixel.shatteredpixeldungeon.network.serializers;

import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.network.NetworkPacket;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemSerializer implements Serializer<Item> {

    @Override
    public Object serialize(Item item, SerializationContext ctx, String profile) {
        JSONObject itemObj = new JSONObject();
        Hero hero = ctx.observer;

        try {
            boolean isGround = "ground".equals(profile);

            // On the ground, we typically don't send actions or UI.
            // When in inventory/window, and if we know the observer, we send them.
            if (hero != null) {
                itemObj.put("actions", NetworkPacket.packActions(item, hero));
                itemObj.put("default_action", item.defaultAction == null ? "null" : item.defaultAction);
                itemObj.put("info", item.info(hero));
                itemObj.put("ui", item.itemUI(hero));
            }

            itemObj.put("sprite_sheet", item.spriteSheet());
            itemObj.put("image", item.image());
            itemObj.put("icon", item.icon);
            itemObj.put("name", item.name());
            itemObj.put("stackable", item.stackable);
            itemObj.put("quantity", item.quantity());
            itemObj.put("known", item.isIdentified());
            itemObj.put("cursed", item.visiblyCursed());
            itemObj.put("identified", item.isIdentified());
            itemObj.put("level_known", item.levelKnown);
            itemObj.put("level", item.visiblyUpgraded());
            itemObj.put("energy_value", item.energyVal());

            ItemSprite.Glowing glowing = item.glowing();
            if (glowing != null) {
                itemObj.put("glowing", glowing.toJsonObject());
            } else {
                itemObj.put("glowing", JSONObject.NULL);
            }

        } catch (JSONException e) {
            Log.e("Packet", "JSONException inside ItemSerializer. " + e.toString());
        }

        return itemObj;
    }
}
