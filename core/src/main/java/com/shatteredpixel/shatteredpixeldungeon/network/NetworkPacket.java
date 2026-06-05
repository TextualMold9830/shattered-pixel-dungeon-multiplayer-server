package com.shatteredpixel.shatteredpixeldungeon.network;

import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.*;
import com.shatteredpixel.shatteredpixeldungeon.network.packets.RedirectPacket;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.PlantDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ChatMessageAction;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.TrapDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.WindowDTO;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class NetworkPacket {

    public final AtomicReference<JSONObject> dataRef;

    public NetworkPacket() {
        dataRef = new AtomicReference<>();
        dataRef.set(new JSONObject());
    }

    public void clearData() {
        synchronized (dataRef) {
            dataRef.set(new JSONObject());
        }
    }

    @NotNull
    public static NetworkPacket fromChatMessages(@NotNull List<@NotNull ChatMessageAction> messages) {
        final NetworkPacket networkPacket = new NetworkPacket();
        for (NetworkAction action : messages) {
            networkPacket.addAction(action);
        }
        return networkPacket;
    }


    public void addAction(@NotNull JSONObject actionObj) {
        Objects.requireNonNull(actionObj);
        assert(actionObj.has("action_name"));
        synchronized (dataRef) {
            try {
                JSONObject data = dataRef.get();
                data.put(Protocol.FIELD_PACKET_TYPE, Protocol.PACKET_ACTIONS_BATCH);
                if (!data.has("actions")) {
                    data.put("actions", new JSONArray());
                }
                data.getJSONArray("actions").put(actionObj);
            } catch (JSONException e) {
                Log.w("NetworkPacket", "Failed to add action. " + e.toString());
            }
        }
    }

    public void addAction(@NotNull NetworkAction action) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        Object serialized = ctx.serialize(action);
        if (serialized instanceof JSONObject && ((JSONObject) serialized).length() > 0) {
            addAction((JSONObject) serialized);
        }
    }

    public void compress() {
        synchronized (dataRef) {
            try {
                NetworkPacketCompressor.compress(dataRef.get());
            } catch (JSONException e) {
                Log.w("NetworkPacket", "Failed to compress packet. " + e.toString());
            }
        }
    }

    public String toJsonString() {
        synchronized (dataRef) {
            return dataRef.get().toString();
        }
    }

    public String toJsonString(int indentFactor) throws JSONException {
        synchronized (dataRef) {
            return dataRef.get().toString(indentFactor);
        }
    }

    public static void addToArray(JSONObject storage, String token, JSONObject data) throws JSONException {
        if (!storage.has(token)) {
            storage.put(token, new JSONArray());
        }
        storage.getJSONArray(token).put(data);
    }

    public void packAndAddActor(Actor actor) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        Object serialized = ctx.serialize(actor, "default");
        if (serialized instanceof JSONObject && ((JSONObject) serialized).length() > 0) {
            String actionName;
            if (actor instanceof Char) {
                actionName = "char_update";
            } else if (actor instanceof Blob) {
                actionName = "blob_update";
            } else {
                Log.w("NetworkPacket", "Unsupported actor update class: " + actor.getClass().toString());
                return;
            }

            JSONObject event = new JSONObject();
            event.put("action_name", actionName);
            event.put("payload", serialized);
            addAction(event);
        }
    }

    public void packAndAddLevel(Level level, Hero observer) {
        addAction(new ResizeLevelAction(level));
        addAction(new SetLevelVisualsAction(level));
        addAction(new SetLevelEntranceAction(level.entrance()));
        addAction(new SetLevelExitAction(level.exit()));
        addAction(new SetLevelTilesAction(level));
        addAction(new SetLevelStatesAction(level));

        level.heaps.values().forEach(heap -> addHeap(heap, observer));
        for (int pos = 0; pos < level.length(); pos++) {
            packAndAddPlant(pos, level.plants.get(pos, null));
        }
        for (int pos = 0; pos < level.length(); pos++) {
            packAndAddTrap(pos, level.traps.get(pos, null));
        }
    }

    public void packAndAddInventoryRebuild(@NotNull Hero hero) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, hero);
        JSONObject payload = (JSONObject) ctx.serialize(hero.belongings, "rebuild");

        payload.put("action_name", "inventory_rebuild");
        addAction(payload);
    }

    @SuppressWarnings("unused") //keep it for future implementation
    public void packAndAddSpecialSlotsDefinition(@NotNull Hero hero) {
        //todo implement this
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, hero);
        Object payload = ctx.serialize(hero.belongings, "special_slot_definitions");

        JSONObject event = new JSONObject();
        event.put("action_name", "inventory_define_special_slots");
        event.put("slots", payload);
        addAction(event);
    }

    private void packAndAddItemAction(String actionName, List<Integer> path, @Nullable Item item, @Nullable Hero hero) {
        JSONObject event = new JSONObject();
        event.put("action_name", actionName);

        JSONArray pathArr = new JSONArray();
        for (int p : path) pathArr.put(p);
        event.put("path", pathArr);
        
        if (item != null) {
            event.put("item", Item.packItem(item, hero));
        }

        addAction(event);
    }

    public void packAndAddItemAdd(List<Integer> path, @NotNull Item item, Hero hero) {
        packAndAddItemAction("item_add", path, item, hero);
    }

    public void packAndAddItemRemove(List<Integer> path) {
        packAndAddItemAction("item_remove", path, null, null);
    }

    public void packAndAddItemUpdate(List<Integer> path, @NotNull Item item, Hero hero) {
        packAndAddItemAction("item_update", path, item, hero);
    }

    public void packAndAddItemReplace(List<Integer> path, @NotNull Item item, Hero hero) {
        packAndAddItemAction("item_replace", path, item, hero);
    }



    public void addHeap(Heap heap, Hero observer) {
        if (heap.isEmpty()) {
            return;
        }
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, observer);
        Object serialized = ctx.serialize(heap);
        if (serialized instanceof JSONObject && ((JSONObject) serialized).length() > 0) {
            JSONObject event = (JSONObject) serialized;
            event.put("action_name", "heap_update");
            addAction(event);
        }
    }



    public void packAndAddWindow(String type, int windowID, @Nullable JSONObject args) {
        WindowDTO dto = new WindowDTO(type, windowID, args);
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONObject obj = (JSONObject) ctx.serialize(dto);
        obj.put("action_name", "show_window");
        addAction(obj);
    }

    public void packAndAddPlant(int pos, Plant plant) {
        PlantDTO dto = new PlantDTO(pos, plant);
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONObject plantObj = (JSONObject) ctx.serialize(dto);

        if (plantObj != null && plantObj.length() > 0) {
            boolean isRemoval = plantObj.has("plant_info") && plantObj.isNull("plant_info");
            plantObj.put("action_name", isRemoval ? "plant_remove" : "plant_update");
            if (isRemoval) {
                plantObj.remove("plant_info");
                plantObj.remove("texture");
            }
            addAction(plantObj);
        }
    }
    public void packAndAddTrap(int pos, Trap trap){
        TrapDTO dto = new TrapDTO(pos, trap);
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONObject trapObj = (JSONObject) ctx.serialize(dto);

        if (trapObj != null && trapObj.length() > 0) {
            boolean isRemoval = trapObj.has("trap_info") && trapObj.isNull("trap_info");
            trapObj.put("action_name", isRemoval ? "trap_remove" : "trap_update");
            if (isRemoval) {
                trapObj.remove("trap_info");
            }
            addAction(trapObj);
        }
    }
    public void packAndAddBuff(Buff buff, boolean remove) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        Object serialized = ctx.serialize(buff, remove ? "remove" : "default");

        if (serialized instanceof JSONObject && ((JSONObject) serialized).length() > 0) {
            JSONObject event = (JSONObject) serialized;
            event.put("action_name", remove ? "buff_remove" : "buff_update");
            addAction(event);
        }
    }

    public void packAndAddRedirect(RedirectPacket redirectPacket) {
        JSONObject event = redirectPacket.toJSON();
        event.put("action_name", "redirect_server");
        addAction(event);
    }
}
