package com.shatteredpixel.shatteredpixeldungeon.network;

import com.nikita22007.multiplayer.utils.Log;
import com.nikita22007.multiplayer.utils.Text;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.network.packets.RedirectPacket;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializationContext;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.CellsUpdateDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.InterlevelSceneDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.PlantDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.TrapDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.WindowDTO;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ClassSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TieredSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.KeyDisplay;
import com.watabou.noosa.Image;
import com.watabou.utils.SparseArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.nikita22007.multiplayer.utils.Utils.putToJSONArray;
import static com.watabou.pixeldungeon.utils.Utils.toSnakeCase;

public class NetworkPacket {
    public static final String CELLS = "cells";
    public static final String STATES = "states";
    public static String CELLS_MAP = "cells_map";
    public static final String MAP = "map";
    public static final String ACTORS = "actors";
    public static final String PLANTS = "plants";
    public static final String TRAPS = "traps";
    public static final String BUFFS = "buffs";




    enum CellState {
        VISITED,
        UNVISITED,
        MAPPED;

        public String toString() {
            return this.name().toLowerCase();
        }
        public int toInt(){
            switch (this) {
                case UNVISITED : return 0;
                case VISITED :return  1;
                case MAPPED : return 2;
            };
            //This never happens, screw Java 8
            return -1;
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

    public void packWorldStateAsActions() {
        synchronized (dataRef) {
            JSONObject data = dataRef.get();
            JSONArray actions = data.optJSONArray("actions");
            if (actions == null) {
                actions = new JSONArray();
                data.put("actions", actions);
            }

            String[] orderedTokens = new String[]{
                    "texturepack",
                    "interlevel_scene",
                    "hero",
                    "messages",
                    "window"
            };

            for (String token : orderedTokens) {
                moveTokenToActions(data, actions, token);
            }

            if (actions.length() == 0) {
                data.remove("actions");
            }
        }
    }

    private void moveTokenToActions(JSONObject data, JSONArray actions, String token) {
        if (!data.has(token)) {
            return;
        }
        JSONObject action = new JSONObject();
        action.put("action_name", token);
        action.put("payload", data.get(token));
        actions.put(action);
        data.remove(token);
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
    public void addServerUUID() {
        synchronized (dataRef) {
            try {
                dataRef.get().put("server_uuid", SPDSettings.serverUUID());
            } catch (JSONException e) {
                Log.w("NetworkPacket", "Failed to add action. " + e.toString());
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

    public void packAndAddActor(Actor actor, boolean heroAsHero) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        Object serialized = ctx.serialize(actor, heroAsHero ? "hero" : "character");
        if (serialized instanceof JSONObject && ((JSONObject) serialized).length() > 0) {
            JSONObject event = new JSONObject();
            event.put("action_name", "actor_update");
            event.put("payload", serialized);
            addAction(event);
        }
    }

    public void packAndAddActorRemoving(Actor actor) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        Object serialized = ctx.serialize(actor, "remove");
        if (serialized instanceof JSONObject && ((JSONObject) serialized).length() > 0) {
            JSONObject event = new JSONObject();
            event.put("action_name", "actor_delete");
            event.put("payload", serialized);
            addAction(event);
        }
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
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, hero);
        Object serialized = ctx.serialize(hero, "hero_block");
        return serialized instanceof JSONObject ? (JSONObject) serialized : new JSONObject();
    }

    public void packAndAddHero(@NotNull Hero hero) {
        packAndAddActor(hero, true);
        addHero(packHero(hero));
    }

    public void packAndAddShield(int id, int shielding) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("id", id);
            payload.put("shield", shielding);
            
            JSONObject event = new JSONObject();
            event.put("action_name", "actor_update");
            event.put("payload", payload);
            addAction(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void packAndAddLevelResize(Level level) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONObject payload = (JSONObject) ctx.serialize(level, "resize_level");
        payload.put("action_name", "resize_level");
        addAction(payload);
    }

    public void packAndAddLevelVisuals(Level level) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONObject payload = (JSONObject) ctx.serialize(level, "set_level_visuals");
        payload.put("action_name", "set_level_visuals");
        addAction(payload);
    }

    public void packAndAddLevelEntrance(int pos) {
        JSONObject event = new JSONObject();
        event.put("action_name", "set_level_entrance");
        event.put("pos", pos);
        addAction(event);
    }

    public void packAndAddLevelExit(int pos) {
        JSONObject event = new JSONObject();
        event.put("action_name", "set_level_exit");
        event.put("pos", pos);
        addAction(event);
    }

    public void packAndAddLevelTiles(Level level) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONArray tilesArr = (JSONArray) ctx.serialize(level, "set_level_tiles");
        JSONObject event = new JSONObject();
        event.put("action_name", "set_level_tiles");
        event.put("tiles", tilesArr);
        addAction(event);
    }

    public void packAndAddLevelStates(Level level) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONArray statesArr = (JSONArray) ctx.serialize(level, "set_level_states");
        JSONObject event = new JSONObject();
        event.put("action_name", "set_level_states");
        event.put("states", statesArr);
        addAction(event);
    }

    public void packAndAddVisiblePositions(boolean[] visible) {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < visible.length; i++) {
            if (visible[i]) {
                arr.put(i);
            }
        }
        JSONObject event = new JSONObject();
        event.put("action_name", "update_fov");
        event.put("visible_pos", arr);
        addAction(event);
    }

    public void packAndAddCellsUpdate(int[] positions, @Nullable int[] tiles, @Nullable int[] states) {
        CellsUpdateDTO dto = new CellsUpdateDTO(positions, tiles, states);
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONObject payload = (JSONObject) ctx.serialize(dto);
        payload.put("action_name", "update_cells");
        addAction(payload);
    }

    public void packAndAddLevel(Level level, Hero observer) {
        packAndAddLevelResize(level);
        packAndAddLevelVisuals(level);
        packAndAddLevelEntrance(level.entrance());
        packAndAddLevelExit(level.exit());
        packAndAddLevelTiles(level);
        packAndAddLevelStates(level);

        level.heaps.values().forEach(heap -> addHeap(heap, observer));
        for (int pos = 0; pos < level.length(); pos++) {
            packAndAddPlant(pos, level.plants.get(pos, null));
        }
        for (int pos = 0; pos < level.length(); pos++) {
            packAndAddTrap(pos, level.traps.get(pos, null));
        }
    }

    public void packAndAddLevelParams(Level level)
    {
        packAndAddLevelResize(level);
        packAndAddLevelVisuals(level);
    }

    public void packAndAddLevelCell(Level level, int cell) {
        int state = 0;
        if (level.visited[cell]) state = 1;
        else if (level.mapped[cell]) state = 2;
        
        packAndAddCellsUpdate(new int[]{cell}, new int[]{level.map[cell]}, new int[]{state});
    }

    public void packAndAddLevelCells(Level level) {
        // Redundant, but kept for legacy proxying if needed. 
        // We'll just call the full tiles/states updates.
        packAndAddLevelTiles(level);
        packAndAddLevelStates(level);
    }

    public void packAndAddLevelCellsSeparateState(Level level){
        packAndAddLevelTiles(level);
        packAndAddLevelStates(level);
    }

    public void packAndAddLevelHeaps(com.watabou.utils.SparseArray<Heap> heaps, Hero observer) {
        for (Heap heap : heaps.values()) {
            addHeap(heap, observer);
        }
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
        InterlevelSceneDTO dto = new InterlevelSceneDTO(state, customMessage);
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONObject stateObj = (JSONObject) ctx.serialize(dto);

        synchronized (dataRef) {
            try {
                JSONObject data = dataRef.get();
                data.put("interlevel_scene", stateObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, hero);
        Object serialized = ctx.serialize(bag, "inventory");
        return serialized instanceof JSONObject ? (JSONObject) serialized : new JSONObject();
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

    public void addInventoryFull(@NotNull Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("hero is null");
        }
        packAndAddInventoryRebuild(hero);
    }

    public void packAndAddInventoryRebuild(@NotNull Hero hero) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, hero);
        JSONObject payload = (JSONObject) ctx.serialize(hero.belongings, "rebuild");

        payload.put("action_name", "inventory_rebuild");
        addAction(payload);
    }

    public void packAndAddSpecialSlotsDefinition(@NotNull Hero hero) {
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

    public void addHeapRemoving(int pos) {
        JSONObject event = new JSONObject();
        event.put("action_name", "heap_remove");
        event.put("pos", pos);
        addAction(event);
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

    public void packAndAddResetLevel() {
        JSONObject event = new JSONObject();
        event.put("action_name", "reset_level");
        addAction(event);
    }

    public void packAndAddShowBanner(@NotNull BannerSprites.Type banner, int color, float fadeTime, float showTime) {
        JSONObject event = new JSONObject();
        event.put("action_name", "show_banner");
        event.put("banner", banner.toString().toLowerCase(Locale.ROOT));
        event.put("color", color);
        event.put("fade_time", fadeTime);
        event.put("show_time", showTime);
        addAction(event);
    }

    public void packAndAddWindow(String type, int windowID, @Nullable JSONObject args) {
        WindowDTO dto = new WindowDTO(type, windowID, args);
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        JSONObject obj = (JSONObject) ctx.serialize(dto);

        synchronized (dataRef) {
            dataRef.get().put("window", obj);
        }
    }

    public void packAndAddIronKeysCount() {
        try {
            synchronized (dataRef) {
                JSONObject uiObj = dataRef.get().optJSONObject("ui");
                uiObj = uiObj != null ? uiObj : new JSONObject();
                JSONArray keyArray = new JSONArray();
                for (int key: KeyDisplay.keys) {
                    keyArray.put(key);
                }
                uiObj.put("iron_keys_count", keyArray);
                dataRef.get().put("ui", uiObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void packAndAddDepth(int depth) {
        try {
            synchronized (dataRef) {
                JSONObject uiObj = dataRef.get().optJSONObject("ui");
                uiObj = uiObj != null ? uiObj : new JSONObject();
                uiObj.put("depth", depth);
                dataRef.get().put("ui", uiObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void packAndAddCounter(float portion) {
        try {
            synchronized (dataRef) {
                JSONObject uiObj = dataRef.get().optJSONObject("ui");
                uiObj = uiObj != null ? uiObj : new JSONObject();
                uiObj.put("counter", portion);
                dataRef.get().put("ui", uiObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void packAndAddRedirect(RedirectPacket redirectPacket) {
        JSONObject event = redirectPacket.toJSON();
        event.put("action_name", "redirect_server");
        addAction(event);
    }
}
