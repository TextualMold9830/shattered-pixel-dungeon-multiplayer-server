package com.shatteredpixel.shatteredpixeldungeon.network;

import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.utils.SparseArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.nikita22007.multiplayer.utils.Utils.putToJSONArray;
import static com.watabou.pixeldungeon.utils.Utils.toSnakeCase;

public class NetworkPacket {
    public static final String CELLS = "cells";
    public static final String MAP = "map";
    public static final String ACTORS = "actors";
    public static final String PLANTS = "plants";
    public static final String BUFFS = "buffs";

    enum CellState {
        VISITED,
        UNVISITED,
        MAPPED;

        public String toString() {
            return this.name().toLowerCase();
        }
    }

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

    public void addServerType(@NotNull String serverType){
        synchronized (dataRef) {
            try {
                JSONObject data = dataRef.get();
                data.put("server_type", serverType);
            } catch (JSONException e) {
                Log.w("NetworkPacket", "Failed to add serverType. " + e.toString());
            }
        }
    }

    public void addAction(@NotNull JSONObject actionObj) {
        Objects.requireNonNull(actionObj);
        synchronized (dataRef) {
            try {
                JSONObject data = dataRef.get();
                if (!data.has("actions")) {
                    data.put("actions", new JSONArray());
                }
                data.getJSONArray("actions").put(actionObj);
            } catch (JSONException e) {
                Log.w("NetworkPacket", "Failed to add action. " + e.toString());
            }
        }
    }

    public static void addToArray(JSONObject storage, String token, JSONObject data) throws JSONException {
        if (!storage.has(token)) {
            storage.put(token, new JSONArray());
        }
        storage.getJSONArray(token).put(data);
    }

    public void addChatMessage(JSONObject message) {
        final String token = "messages";
        synchronized (dataRef) {
            try {
                JSONObject storage = dataRef.get();
                addToArray(storage, token, message);
            } catch (JSONException e) {
                Log.w("NetworkPacket", "Failed to add message. " + e.toString());
            }
        }
    }

    public void synchronizedPut(String key, JSONObject data) throws JSONException {
        synchronized (dataRef) {
            dataRef.get().put(key, data);
        }
    }

    protected CellState getCellState(boolean visited, boolean mapped) {
        if (visited)
            return CellState.VISITED;
        if (mapped)
            return CellState.MAPPED;
        return CellState.UNVISITED;
    }

    protected void addActor(JSONObject actor) {
        if (actor.length() == 0) {
            return;
        }
        try {
            synchronized (dataRef) {
                JSONObject data = dataRef.get();
                if (!data.has(ACTORS)) {
                    data.put(ACTORS, new JSONArray());
                }
                data.accumulate(ACTORS, actor);
            }
        } catch (JSONException e) {
        }
    }

    protected JSONObject packActorRemoving(@NotNull Actor actor) {

        JSONObject object = new JSONObject();
        try {
            if ((actor instanceof Char) || (actor instanceof Blob)) {
                int id = actor.id();
                if (id <= 0) {
                    return new JSONObject();
                }
                object.put("id", id);
                object.put("type", "removed");
            } else {
                Log.w("NetworkPacket:", "pack actor. Actor class: " + actor.getClass().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    protected JSONObject packActor(@NotNull Actor actor, boolean heroAsHero) {

        JSONObject object = new JSONObject();
        try {
            if (actor instanceof Char) {
                Char character = (Char) actor;
                int id = actor.id();
                if (id <= 0) {
                    return new JSONObject();
                }
                object.put("id", id);
                if (heroAsHero && (actor instanceof Hero)) {
                    object.put("type", "hero");
                } else {
                    object.put("type", "character");
                    if (character.sprite != null) {
                        String spriteAsset = character.sprite.getSpriteAsset();
                        if (spriteAsset != null) {
                            object.put("sprite_asset",spriteAsset);
                        } else
                        {
                            object.put("sprite_name", ((Char) actor).sprite.spriteName());
                        }
                    }
                }
                String name = character.name();
                int hp = character.HP;
                int ht = character.HT;
                int pos = character.pos;
                object.put("hp", hp);
                object.put("max_hp", ht);
                object.put("position", pos);
                object.put("name", name);

                object.put("emo", character.getEmoJsonObject());
                CharSprite sprite = character.sprite;
                if (sprite != null) {
                    JSONArray states = putToJSONArray(((Char) actor).sprite.states().toArray());
                    object.put("states", states);
                }
                if (actor instanceof Mob) {
                    String desc = ((Mob) actor).description();
                    object.put("description", desc);
                }
            } else if (actor instanceof Blob) {
                int id = actor.id();
                object.put("id", id);
                object.put("type", "blob");
                object.put("blob_type", toSnakeCase(actor.getClass().getSimpleName()));
                JSONArray positions = new JSONArray();
                for (int i = 0; i < ((Blob) actor).cur.length; i++) {
                    if (((Blob) actor).cur[i] > 0) {
                        positions.put(i);
                    }
                }
                object.put("positions", positions);
            } else if (actor instanceof Buff) {
                //no warning
            } else {
                Log.w("NetworkPacket", "remove actor. Actor class: " + actor.getClass().toString());
            }
        } catch (JSONException e) {

        }

        return object;
    }

    public void packAndAddActor(Actor actor, boolean heroAsHero) {
        addActor(packActor(actor, heroAsHero));
    }

    public void packAndAddActorRemoving(Actor actor) {
        addActor(packActorRemoving(actor));
    }

    protected void addHero(JSONObject hero) {
        try {
            synchronized (dataRef) {

                JSONObject data = dataRef.get();
                data.put("hero", hero);
            }
        } catch (JSONException e) {
        }
    }

    public void addNewHeroID(int id) {
        try {
            synchronized (dataRef) {

                JSONObject data = dataRef.get();
                data.put("hero", packNewHeroID(id));
            }
        } catch (JSONException e) {
        }
    }

    protected JSONObject packNewHeroID(int id) {
        JSONObject object = new JSONObject();
        try {
            object.put("actor_id", id);
        } catch (JSONException e) {

        }

        return object;
    }

    protected JSONObject packHero(@NotNull Hero hero) {
        int id = hero.id();
        if (id <= 0) {
            return new JSONObject();
        }
        JSONObject object = new JSONObject();
        String class_name = hero.heroClass.name();
        int subclass_id = 0;
        int strength = hero.STR();
        int lvl = hero.lvl;
        int exp = hero.exp;
        try {
            object.put("actor_id", id);
            object.put("class", class_name);
            object.put("subclass_id", subclass_id);
            object.put("strength", strength);
            object.put("lvl", lvl);
            object.put("exp", exp);
        } catch (JSONException e) {

        }

        return object;
    }

    public void packAndAddHero(@NotNull Hero hero) {
        addActor(packActor(hero, true));
        addHero(packHero(hero));
    }

    public void packAndAddHeroLevel(@NotNull int lvl, int exp) {
        synchronized (dataRef) {
            JSONObject data = dataRef.get();
            @NotNull
            JSONObject heroObj = data.optJSONObject("hero");
            if (heroObj == null) {
                heroObj = new JSONObject();
            }
            try {
                heroObj.put("lvl", lvl);
                heroObj.put("exp", exp);
                data.put("hero", heroObj);
            } catch (JSONException e) {
                ShatteredPixelDungeon.reportException(e);
            }
        }
    }

    public void packAndAddHeroStrength(@NotNull int str) {
        synchronized (dataRef) {
            JSONObject data = dataRef.get();
            @NotNull
            JSONObject heroObj = data.optJSONObject("hero");
            if (heroObj == null) {
                heroObj = new JSONObject();
            }
            try {
                heroObj.put("strength", str);
                data.put("hero", heroObj);
            } catch (JSONException e) {
                ShatteredPixelDungeon.reportException(e);
            }
        }
    }

    public void packAndAddLevelParams(Level level)
    {
        JSONObject params = new JSONObject();
        params.put("width", level.width());
        params.put("height", level.height());
        params.put("tiles_texture", level.tilesTex());
        params.put("water_texture", level.waterTex());
        synchronized (dataRef) {
            dataRef.get().put("level_params", params);
        }
    }

    public void packAndAddLevelEntrance(int pos) {
        try {
            synchronized (dataRef) {
                JSONObject data = dataRef.get();
                if (!data.has(MAP)) {
                    data.put(MAP, new JSONObject());
                }
                data.getJSONObject(MAP).put("entrance", pos);
            }
        } catch (JSONException ignored) {
        }
    }

    public void packAndAddLevelExit(int pos) {
        try {
            synchronized (dataRef) {
                JSONObject data = dataRef.get();
                if (!data.has(MAP)) {
                    data.put(MAP, new JSONObject());
                }
                data.getJSONObject(MAP).put("exit", pos);
            }
        } catch (JSONException ignored) {
        }
    }

    protected void addCell(JSONObject cell) {
        try {
            synchronized (dataRef) {
                JSONObject data = dataRef.get();
                if (!data.has(MAP)) {
                    data.put(MAP, new JSONObject());
                }
                JSONObject map = data.getJSONObject(MAP);
                if (!map.has(CELLS)) {
                    map.put(CELLS, new JSONArray());
                }
                map.accumulate(CELLS, cell);
            }
        } catch (JSONException ignored) {
        }
    }

    protected JSONObject packCell(int pos, int id, CellState state) {
        JSONObject cell = new JSONObject();
        try {
            cell.put("position", pos);
            cell.put("id", id);
            cell.put("state", state.toString());
        } catch (JSONException ignored) {
        }
        return cell;
    }

    protected void packAndAddCell(int pos, int id, CellState state) {
        addCell(packCell(pos, id, state));
    }

    public void packAndAddLevelCell(Level level, int cell) {
        packAndAddCell(
                cell,
                level.map[cell],
                getCellState(level.visited[cell], level.mapped[cell])
        );
    }

    public void packAndAddLevelCells(Level level) {
        for (int i = 0; i < level.length(); i++) {
            packAndAddCell(
                    i,
                    level.map[i],
                    getCellState(level.visited[i], level.mapped[i])
            );
        }
    }

    public void packAndAddLevelHeaps(SparseArray <Heap> heaps, Hero observer) {
        for (Heap heap : heaps.values()) {
            addHeap(heap, observer);
        }
    }

    public void packAndAddLevel(Level level, Hero observer) {
        packAndAddLevelParams(level);
        packAndAddLevelEntrance(level.entrance);
        packAndAddLevelExit(level.exit);
        packAndAddLevelCells(level);
        packAndAddLevelHeaps(level.heaps, observer);
        packAndAddPlants(level);
    }

    protected void addVisiblePositions(@NotNull JSONArray visiblePositionsArray) {
        try {
            synchronized (dataRef) {
                JSONObject data = dataRef.get();
                if (!data.has(MAP)) {
                    data.put(MAP, new JSONObject());
                }
                data.getJSONObject(MAP).put("visible_positions", visiblePositionsArray);
            }
        } catch (JSONException ignore) {
        }
    }

    public void packAndAddVisiblePositions(boolean[] visible) {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < visible.length; i++) {
            if (visible[i]) {
                arr.put(i);
            }
        }
        addVisiblePositions(arr);
    }

    public void packAndAddBadge(String badgeName, int badgeLevel) {
        JSONObject badge = new JSONObject();
        try {
            badge.put("name", badgeName);
            badge.put("level", badgeLevel);
        } catch (Exception ignored) {
        }
        synchronized (dataRef) {
            try {
                JSONObject data = dataRef.get();
                data.put("badge", badge);
            } catch (Exception ignored) {
            }
        }
    }
    public void addInterlevelSceneObject(JSONObject interlevelSceneParams) {
        try {
            synchronized (dataRef) {
                JSONObject data = dataRef.get();
                data.put("interlevel_scene", interlevelSceneParams);
            }
        } catch (JSONException ignored) {

        }
    }

    public void packAndAddInterlevelSceneState(String state, String customMessage) {
        try {
            JSONObject stateObj = new JSONObject();
            stateObj.put("state", state);
            if (customMessage != null) {
                stateObj.put("custom_message", customMessage);
            }
            synchronized (dataRef) {
                JSONObject data = dataRef.get();
                data.put("interlevel_scene", stateObj);
            }
        } catch (JSONException ignored) {

        }
    }

    public void packAndAddInterlevelSceneState(String state) {
        packAndAddInterlevelSceneState(state, null);
    }

    @NotNull
    public static JSONArray packActions(@NotNull Item item, @NotNull Hero hero) {
        JSONArray actionsArr = new JSONArray();
        for (String action : item.actions(hero)) {
            actionsArr.put(action);
        }
        return actionsArr;
    }

    @NotNull
    public JSONObject packBag(Bag bag) {
        if ((bag.owner != null) && (bag.owner instanceof Hero)) {
            return packBag(bag, (Hero) bag.owner);
        } else {
            return packBag(bag, null);
        }
    }

    @NotNull
    public JSONObject packBag(@NotNull Bag bag, Hero hero) {
        return Item.packItem(bag, hero);
    }

    @NotNull
    public static JSONObject packBag(@NotNull Bag bag, @Nullable Hero hero, @NotNull JSONObject itemObj) {
        if ((bag.owner != null) && (bag.owner != hero)) {
            Log.w("Packet", "bag.owner != gotten_hero");
        }

        JSONArray bagItems = new JSONArray();

        for (Item item : bag.items) {
            JSONObject serializedItem;
            serializedItem = Item.packItem(item, hero);
            if (serializedItem.length() == 0) {
                Log.w("Packet", "item hadn't serialized");
            }
            bagItems.put(serializedItem);
        }

        JSONObject bagObj = itemObj;
        try {
            bagObj.put("size", bag.capacity());
            bagObj.put("items", bagItems);
            bagObj.put("owner", hero != null ? hero.id() : null);
            bagObj.put("icon", bag.icon);
        } catch (JSONException e) {
            Log.e("Packet", "JSONException inside packBag. " + e.toString());
        }

        return bagObj;
    }

    public JSONArray packBags(@NotNull Bag... bags) {
        Objects.requireNonNull(bags);
        JSONArray bagsObj = new JSONArray();
        for (Bag bag : bags) {
            if (bag == null) {
                continue;
            }
            JSONObject bagObj = packBag(bag);
            if (bagObj.length() == 0) {
                Log.w("Packet", "bag hadn't serialized");
            } else {
                bagsObj.put(bagObj);
            }
        }
        return bagsObj;
    }

    @NotNull
    public JSONObject packHeroBags(@NotNull Belongings belongings) {
        Bag backpack = belongings.backpack;
        return packBag(backpack);
    }

    @NotNull
    public JSONObject packHeroBags(@NotNull Hero hero) {
        if (hero.belongings == null) {
            Log.w("Packet", "Hero belongings is null");
            return new JSONObject();
        }
        return packHeroBags(hero.belongings);
    }

    protected static final String INVENTORY = "inventory";

    public void addHeroBags(@NotNull Hero hero) {

        JSONObject bagsObj = packHeroBags(hero);
        try {
            synchronized (dataRef) {
                JSONObject data = dataRef.get();
                JSONObject inv;
                if (data.has(INVENTORY)) {
                    inv = data.getJSONObject(INVENTORY);
                } else {
                    inv = new JSONObject();
                    data.put(INVENTORY, inv);
                }
                inv.put("backpack", bagsObj);
            }
        } catch (JSONException e) {
            Log.e("Packet", "JSONException inside addInventory. " + e.toString());
        }
    }

    public void addSpecialSlots(Hero hero) {

        JSONArray slotsArr = new JSONArray();
        for (SpecialSlot slot : hero.belongings.getSpecialSlots()) {
            JSONObject slotObj = new JSONObject();
            try {
                slotObj.put("id", slot.id);
                slotObj.put("sprite", slot.sprite);
                slotObj.put("image_id", slot.image_id);
                slotObj.put("item", (slot.item != null) ? Item.packItem(slot.item, hero) : JSONObject.NULL);
            } catch (JSONException e) {
                Log.wtf("NetworkPacket", "JsonException while adding special slot" + e.toString());
            }
            slotsArr.put(slotObj);
        }
        try {
            synchronized (dataRef) {
                JSONObject data = dataRef.get();
                JSONObject inv;
                if (data.has(INVENTORY)) {
                    inv = data.getJSONObject(INVENTORY);
                } else {
                    inv = new JSONObject();
                    data.put(INVENTORY, inv);
                }
                inv.put("special_slots", slotsArr);
            }
        } catch (JSONException e) {
            Log.e("Packet", "JSONException inside addSpectialSlots. " + e.toString());
        }
    }

    public void addInventoryFull(@NotNull Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("hero is null");
        }
        addHeroBags(hero);
        addSpecialSlots(hero);
    }

    public JSONObject packHeapRemoving(int pos) {
        JSONObject heapObj;
        heapObj = new JSONObject();
        try {
            heapObj.put("pos", pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return heapObj;
    }

    public JSONObject packHeap(Heap heap, Hero observer) {
        if (heap == null) {
            return null;
        }
        if (heap.isEmpty()) {
            return null;
        }
        JSONObject heapObj;
        heapObj = new JSONObject();
        try {
            heapObj.put("pos", heap.pos);
            heapObj.put("visible_item", Item.packItem(heap.items.getFirst(), observer));
            int heapImage = -1;
            if (heap.showsFirstItem()) {
                heapImage = heap.image();
            }
            heapObj.put("visible_sprite", heapImage);
            heapObj.put("show_item", heap.showsFirstItem());
            heapObj.put("seen", heap.isSeen());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return heapObj;
    }

    public void addHeapRemoving(Heap heap) {
        addHeapRemoving(heap.pos);
    }

    public void addHeapRemoving(int pos) {
        addHeap(packHeapRemoving(pos));
    }

    private void addHeap(JSONObject heapObj) {
        if (heapObj == null) {
            return;
        }
        synchronized (dataRef) {
            try {
                addToArray(dataRef.get(), "heaps", heapObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addHeap(Heap heap, Hero observer) {
        if (heap.isEmpty()) {
            return;
        }
        addHeap(packHeap(heap, observer));
    }

    public void packAndAddServerAction(String action_type) {
        try {
            JSONObject res = new JSONObject();
            res.put("type", action_type);
            synchronized (dataRef) {
                addToArray(dataRef.get(), "server_actions", res);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void packAndAddWindow(String type, int windowID, @Nullable JSONObject args) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", windowID);
            obj.put("type", type);
            obj.put("args", args);
            synchronized (dataRef) {
                dataRef.get().put("window", obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void packAndAddIronKeysCount(int count) {
        try {
            synchronized (dataRef) {
                JSONObject uiObj = dataRef.get().optJSONObject("iu");
                uiObj = uiObj != null ? uiObj : new JSONObject();
                uiObj.put("iron_keys_count", count);
                dataRef.get().put("ui", uiObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void packAndAddDepth(int depth) {
        try {
            synchronized (dataRef) {
                JSONObject uiObj = dataRef.get().optJSONObject("iu");
                uiObj = uiObj != null ? uiObj : new JSONObject();
                uiObj.put("depth", depth);
                dataRef.get().put("ui", uiObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void packAndAddPlants(Level level) {
        for (int pos = 0; pos < level.length(); pos++) {
            packAndAddPlant(pos, level.plants.get(pos, null));
        }
    }

    public void packAndAddPlant(int pos, Plant plant) {
        JSONObject plantObj = new JSONObject();
        try {
            plantObj.put("pos", pos);
            plantObj.put("texture", "plants.png");
            if (plant == null) {
                plantObj.put("plant_info", JSONObject.NULL);
            } else {
                JSONObject plantInfoObj = new JSONObject();
                plantInfoObj.put("sprite_id", plant.image);
                plantInfoObj.put("name", plant.name());
                plantInfoObj.put("desc", plant.desc());
                plantObj.put("plant_info", plantInfoObj);
            }
            synchronized (dataRef) {
                if (!dataRef.get().has(PLANTS)) {
                    dataRef.get().put(PLANTS, new JSONArray());
                }
                dataRef.get().getJSONArray(PLANTS).put(plantObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void packAndAddBuff(Buff buff) {
        JSONObject buffObj = new JSONObject();
        int id = buff.id();
        try {
            buffObj.put("id", id);
            buffObj.put("icon", buff.icon());
            Actor target = buff.target;
            buffObj.put("target_id", target == null ? JSONObject.NULL : target.id());
            buffObj.put("desc", buff.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        try {
            synchronized (dataRef) {
                addToArray(dataRef.get(), BUFFS, buffObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

    }

    public void packAndAddTextures(String path) {

        // Read all bytes from a file and convert to Base64 String
        byte[] byteData = new byte[0];
        try {
            byteData = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String base64String = Base64.getEncoder().encodeToString(byteData);

        synchronized (dataRef)
        {
            dataRef.get().put("texturepack", base64String);
        }
    }
    public void packAndAddRawTextures(String data) {
        dataRef.get().put("texturepack",data);
    }

}
