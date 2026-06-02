package com.shatteredpixel.shatteredpixeldungeon.network;

import com.nikita22007.multiplayer.utils.Log;
import com.nikita22007.multiplayer.utils.text.LocalizedString;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.DiscoverTileAction;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.NetworkAction;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.UpdateFovAction;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.CharSpriteStateAction;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ShowBannerAction;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.HeapRemoveAction;
import com.shatteredpixel.shatteredpixeldungeon.network.packets.RedirectPacket;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.InterlevelSceneAction;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.shatteredpixel.shatteredpixeldungeon.network.NetworkPacket.addToArray;
import static com.shatteredpixel.shatteredpixeldungeon.network.Server.clients;

public class SendData {
    public static final float CHAT_FLUSH_INTERVAL = 0.1f;
    private static float chatFlushElapsed = 0f;

    //---------------------------Level

    public static void addToSendLevelVisitedStateFull(Level level, int ID) {
        if ((ID != -1) && (clients[ID] != null)) {
            clients[ID].packet.packAndAddLevelCells(level);
        }
    }

    public static void addToSendLevelVisitedState(Level level, int ID, boolean[] diff) {
        if ((ID != -1) && (clients[ID] != null)) {
            for (int i = 0; i< diff.length; i++) {
                if (diff[i]) {
                    clients[ID].packet.packAndAddLevelCell(level, i); //todo optimize this
                }
            }
        }
    }

    public static void addToSendLevelVisitedState(Level level, boolean[] diff) {
        for (int ID =0; ID < clients.length; ID++)
        {
            addToSendLevelVisitedState(level,ID,diff);
        }
    }

    public static void addToSendLevelMappedState(Level level, int ID) {
        if ((ID != -1) && (clients[ID] != null)) {
            clients[ID].packet.packAndAddLevelCells(level);
        }
    }

    public static void addToSendLevelMappedState(Level level) {
        for (int ID =0; ID < clients.length; ID++){
            addToSendLevelMappedState(level, ID);
        }
    }

    public static void sendLevel(Level level, int ID) {
        if ((ID != -1) && (clients[ID] != null)) {
            PlantCache.clear();
            TrapCache.clear();
            clients[ID].packet.packAndAddLevel(level, clients[ID].clientHero);
            clients[ID].flush();
        }
    }

    public static void sendLevelCell(Level level, int cell) {
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] == null) {
                continue;
            }
            clients[i].packet.packAndAddLevelCell(level, cell);
            clients[i].flush();
        }
    }

    //---------------------------Hero
    public static void addToSendHeroVisibleCells(Hero hero, boolean allowLateSerialization) {
        sendAction(hero, new UpdateFovAction(hero, allowLateSerialization));
    }

    public static void sendShowBanner(@NotNull Hero hero, @NotNull BannerSprites.Type banner, int color, float fadeTime, float showTime) {
        final int ID = hero.networkID;
        if ((ID != -1) && (clients[ID] != null)) {
            clients[ID].packet.addAction(new ShowBannerAction(banner, color, fadeTime, showTime));
            clients[ID].flush();
        }
    }
    //---------------------------UI  and mechanics
    public static void sendResumeButtonVisible(int ID, boolean visible) {
        if ((ID != -1) && (clients[ID] != null)) {
            //  clients[ID].send(Codes.RESUME_BUTTON, visible);
        }
    }

    public static void sendIronKeysCount() {
        for (ClientThread client: clients) {
            if (client != null) {
                client.packet.packAndAddIronKeysCount();
                client.flush();
            }
        }

    }

    public static void sendDepth(int depth) {
        for (int i = 0; i < clients.length; i++) {
            sendDepth(i, depth);
        }
    }

    public static void sendDepth(int ID, int depth) {
        if ((ID != -1) && (clients[ID] != null)) {
            clients[ID].packet.packAndAddDepth(depth);
            clients[ID].flush();
        }
    }

    //-----------------------------Interlevel Scene

    public static void sendInterLevelSceneForAll(InterlevelSceneAction interlevelSceneParams) {
        for (int i = 0; i < clients.length; i++) {
            sendInterLevelScene(i, interlevelSceneParams);
        }
    }
    public static void sendInterLevelScene(int ID, InterlevelSceneAction interlevelSceneParams) {
        if ((ID != -1) && (clients[ID] != null)) {
            {
                if (clients[ID].clientHero == null) {
                    return;
                }
            }
            clients[ID].forceFlush();
            clients[ID].packet.addAction(interlevelSceneParams);
            clients[ID].forceFlush();
        }
    }

    public static void sendInterLevelSceneFadeOut(int ID) {
        if ((ID != -1)&&  (clients[ID] != null)) {
            clients[ID].forceFlush();
            if (clients[ID].clientHero == null) {
                return;
            }
            clients[ID].packet.addAction(new InterlevelSceneAction("fade_out"));
            clients[ID].forceFlush();
        }
    }

    //-----------------------------Windows
    public static void sendWindow(@NotNull final Window wnd, @NotNull final String type) {
        sendWindow(wnd, type, null);
    }

    //-----------------------------Windows
    public static void sendWindow(@NotNull final Window wnd, @NotNull final String type, @Nullable final JSONObject args) {
        final int ID = wnd.getOwnerHero().networkID;
        final int windowID = wnd.getId();
        sendWindow(ID, type, windowID, args);
    }
    public static void sendWindow(int ID, String type, int windowID, @Nullable JSONObject args) {
        if ((ID != -1) && (clients[ID] != null)) {
            clients[ID].packet.packAndAddWindow(type, windowID, args);
            clients[ID].flush();
        }
    }

    //----------
    public static void sendActor(Actor actor) {
        if (actor == null) {
            return;
        }
        if (actor instanceof Buff) {
            sendBuff((Buff) actor, false);
            return;
        }
        for (ClientThread client : clients) {
            if (client == null) {
                continue;
            }
            client.packet.packAndAddActor(actor, actor == client.clientHero);
            client.flush();
        }
    }

    public static void sendCharShield(int id, int shielding) {
        for (ClientThread client : clients) {
            if (client == null) {
                continue;
            }
            client.packet.packAndAddShield(id, shielding);
            client.flush();
        }
    }
    public static void sendAllChars(int ID) {
        if ((ID != -1) && (clients[ID] != null)) {
            clients[ID].addAllCharsToSend();
            clients[ID].flush();
        }
    }

    public static void sendAddCharSpriteState(Actor actor, CharSprite.State state) {
        sendSpriteStateChange(actor, state, false);
    }

    public static void sendRemoveCharSpriteState(Actor actor, CharSprite.State state) {
        sendSpriteStateChange(actor, state, true);
    }

    private static void sendSpriteStateChange(Actor actor, CharSprite.State state, boolean remove) {
        if (actor == null) {
            return;
        }
        int id = actor.id();
        if (id == Actor.NO_ID) {
            return;
        }
        for (ClientThread client : clients) {
            if (client == null) {
                continue;
            }
            client.packet.addAction(new CharSpriteStateAction(id, state, remove));
            client.flush();
        }
    }

    @Deprecated
    public static void flush(Hero hero) {
        if (hero.networkID >= 0) {
            flush(hero.networkID);
        }
    }

    @Deprecated
    public static void flush(int networkID) {
        if (networkID <= -1) {
            return;
        }
    }

    public static void forceFlush(Hero hero) {
        if (hero.networkID >= 0) {
            forceFlush(hero.networkID);
        }
    }

    public static void forceFlush(int networkID) {
        if (networkID <= -1) {
            return;
        }
        if (clients[networkID] != null) {
            clients[networkID].forceFlush();
        }
    }

    public static void forceFlushAll() {
        for (ClientThread client : clients) {
            if (client != null) {
                client.forceFlush();
            }
        }
    }

    public static void sendMessageToAll(String message) {
        sendMessageToAll(LocalizedString.raw(message));
    }

    public static void sendMessageToAll(LocalizedString message) {
        JSONObject messageObj;
        try {
            messageObj = new JSONObject().put("text", message);
        } catch (JSONException e) {
            return;
        }
        for (int i = 0; i < clients.length; i++) {
            ClientThread client = clients[i];
            if (client == null) {
                continue;
            }
            client.packet.addChatMessage(messageObj);
            client.flush();
        }
    }

    public static void sendMessage(Integer ID, String message) {
        sendMessage(ID, LocalizedString.raw(message));
    }

    public static void sendMessage(Integer ID, LocalizedString message) {
        JSONObject messageObj;
        try {
            messageObj = new JSONObject().put("text", message);
        } catch (JSONException e) {
            return;
        }
        if(ID != null) {
            ClientThread client = clients[ID];
            if (client == null) {
                return;
            }
            client.packet.addChatMessage(messageObj);
            client.flush();
        } else {
            sendMessageToAll(message);
        }
    }

    public static void sendMessageExcept(Integer exceptId, String message) {
        sendMessageExcept(exceptId, LocalizedString.raw(message));
    }

    public static void sendMessageExcept(Integer exceptId, LocalizedString message) {
        if (exceptId == null)
        {
            sendMessageToAll(message);
            return;
        }
        JSONObject messageObj;
        try {
            messageObj = new JSONObject().put("text", message);
        } catch (JSONException e) {
            return;
        }
        for (int i = 0; i < clients.length; i++) {
            if (i == exceptId) continue;
            ClientThread client = clients[i];
            if (client == null) {
                continue;
            }
            client.packet.addChatMessage(messageObj);
            client.flush();
        }
    }

    public static void enqueueChatMessageToAll(String message) {
        enqueueChatMessageToAll(LocalizedString.raw(message));
    }

    public static void enqueueChatMessageToAll(LocalizedString message) {
        JSONObject messageObj = packMessage(message);
        if (messageObj == null) {
            return;
        }
        for (ClientThread client : clients) {
            if (client != null) {
                client.enqueueChatMessage(messageObj);
            }
        }
    }

    public static void enqueueChatMessage(Integer ID, String message) {
        enqueueChatMessage(ID, LocalizedString.raw(message));
    }

    public static void enqueueChatMessage(Integer ID, LocalizedString message) {
        if (ID == null) {
            enqueueChatMessageToAll(message);
            return;
        }
        JSONObject messageObj = packMessage(message);
        if (messageObj == null) {
            return;
        }
        if (ID >= 0 && ID < clients.length && clients[ID] != null) {
            clients[ID].enqueueChatMessage(messageObj);
        }
    }

    public static void enqueueChatMessageExcept(Integer exceptId, String message) {
        enqueueChatMessageExcept(exceptId, LocalizedString.raw(message));
    }

    public static void enqueueChatMessageExcept(Integer exceptId, LocalizedString message) {
        if (exceptId == null) {
            enqueueChatMessageToAll(message);
            return;
        }
        JSONObject messageObj = packMessage(message);
        if (messageObj == null) {
            return;
        }
        for (int i = 0; i < clients.length; i++) {
            if (i == exceptId) {
                continue;
            }
            ClientThread client = clients[i];
            if (client != null) {
                client.enqueueChatMessage(messageObj);
            }
        }
    }

    public static void updatePendingChat(float elapsed) {
        chatFlushElapsed += elapsed;
        if (chatFlushElapsed < CHAT_FLUSH_INTERVAL) {
            return;
        }
        chatFlushElapsed = 0f;
        flushPendingChat();
    }

    public static void flushPendingChat() {
        for (ClientThread client : clients) {
            if (client != null) {
                client.flushPendingChatMessages();
            }
        }
    }

    private static JSONObject packMessage(LocalizedString message) {
        try {
            return new JSONObject().put("text", message);
        } catch (JSONException e) {
            return null;
        }
    }

    public static void addToSendShowStatus(Float x, Float y, Integer key, String text, int color, boolean ignorePosition) {
        JSONObject data = new JSONObject();
        try {
            data.put("action_name", "show_status");
            data.put("x", x);
            data.put("y", y);
            data.put("key", key);
            data.put("text", text);
            data.put("color", color);
            data.put("ignore_position", ignorePosition);
        } catch (JSONException e) {
            Log.wtf("SendData", "Exception while adding showstatus", e);
            return;
        }
        for (ClientThread client : clients) {
            if (client == null) {
                continue;
            }
            AtomicReference<JSONObject> ref = client.packet.dataRef;
            synchronized (ref) {
                try {
                    addToArray(ref.get(), "actions", data);
                } catch (JSONException e) {
                    Log.w("SendData", "failed to send \"Show_status\"");
                    continue;
                }
            }
        }
    }

    public static void sendRemoveItemFromInventory(Char owner, List<Integer> path) {
        if ((owner == null) || !(owner instanceof Hero)) {
            return;
        }
        Hero hero = (Hero) owner;
        if (hero.networkID < 0 || path == null || path.isEmpty()) {
            return;
        }
        ClientThread client = clients[hero.networkID];
        if (client != null) {
            client.packet.packAndAddItemRemove(path);
        }
    }

    public static void sendUpdateItemCount(Char owner, Item item, int count, List<Integer> path) {
        sendUpdateItemFull(owner, item, path);
    }

    public static void sendUpdateItemFull(Item item) {
        for (Hero hero : Dungeon.heroes) {
            if (hero == null) {
                continue;
            }
            List<Integer> path = hero.belongings.pathOfItem(item);
            if ((path == null) || (path.isEmpty())) {

                continue;
            }
            sendUpdateItemFull(hero, item, path);
            break;
        }
    }

    public static void sendUpdateItemFull(Char owner, Item item) {
        if ((owner == null) || !(owner instanceof Hero)) {
            return;
        }
        sendUpdateItemFull(owner, item, ((Hero) owner).belongings.pathOfItem(item));
    }
    public static void sendUpdateItemFull(Char owner, Item item, List<Integer> path) {
        if ((owner == null) || !(owner instanceof Hero)) {
            return;
        }
        Hero hero = (Hero) owner;
        if (hero.networkID < 0 || path == null || path.isEmpty() || item == null) {
            return;
        }
        ClientThread client = clients[hero.networkID];
        if (client != null) {
            client.packet.packAndAddItemUpdate(path, item, hero);
        }
    }

    public static void sendNewInventoryItem(Char owner, Item item, List<Integer> path) {
        if ((owner == null) || !(owner instanceof Hero)) {
            return;
        }
        Hero hero = (Hero) owner;
        if (hero.networkID < 0 || path == null || path.isEmpty() || item == null) {
            return;
        }
        ClientThread client = clients[hero.networkID];
        if (client != null) {
            client.packet.packAndAddItemAdd(path, item, hero);
        }
    }



    public static void sendHeap(Heap heap) {
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] == null) {
                continue;
            }
            clients[i].packet.addHeap(heap, clients[i].clientHero);
            clients[i].flush();
        }
    }

    public static void sendPlant(int pos, Plant plant) {
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] == null) {
                continue;
            }
            clients[i].packet.packAndAddPlant(pos, plant);
            clients[i].flush();
        }
    }

    public static void sendActorRemoving(Actor actor) {
        if (actor instanceof Buff) {
            sendBuff((Buff) actor, true);
            return;
        }
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] == null) {
                continue;
            }
            clients[i].packet.packAndAddActorRemoving(actor);
            clients[i].flush();
        }
    }

    public static void sendBuff(Buff buff, boolean remove) {
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] == null) {
                continue;
            }
            clients[i].packet.packAndAddBuff(buff, remove);
            clients[i].flush();
        }
    }
    public static void sendBuff(Buff buff){
        sendBuff(buff, false);
    }
    public static void sendFlashChar(CharSprite sprite, float flashTime) {

        if (sprite.ch == null){
            ShatteredPixelDungeon.reportException(new RuntimeException("char sprite has not owner. Ignored"));
            return;
        }

        JSONObject actionObj = new JSONObject();
        try {
            actionObj.put("action_name", "sprite_action");
            actionObj.put("action", "flash");
            actionObj.put("actor_id", sprite.ch.id());
            actionObj.put("flash_time", flashTime);
            sendCustomActionForAll(actionObj);
        } catch (JSONException e) {
            ShatteredPixelDungeon.reportException(e);
        }

    }

    public static void sendCustomActionForAll(@NotNull JSONObject action_obj) {
        for (int i = 0; i < clients.length; i++) {
            sendCustomAction(action_obj, i);
        }
    }

    public static void sendCustomAction(@NotNull JSONObject action_obj, @NotNull Hero hero) {
        if (hero.networkID <= -1) {
            return;
        }
        int networkID = hero.networkID;
        if (clients[networkID] != null) {
            clients[networkID].packet.addAction(action_obj);
            clients[networkID].flush();
        }
    }

    public static void sendCustomAction(JSONObject action_obj, int networkID) {
        assert action_obj.has("action_name") : "Action object must contains \"action_type\" field";
        if (networkID <= -1) {
            return;
        }
        if (clients[networkID] != null) {
            clients[networkID].packet.addAction(action_obj);
        }
    }


    public static void sendActionDiscoverTile(int pos, int oldValue) {
        sendActionForAll(new DiscoverTileAction(pos, oldValue));
    }

    public static void sendCellListenerPrompt(LocalizedString new_prompt, int networkID) {
        if (networkID < 0){
            return;
        }
        if (clients[networkID] == null) {
            return;
        }
        try {
            AtomicReference<JSONObject> dataRef = clients[networkID].packet.dataRef;
            synchronized (clients[networkID].packet.dataRef) {
                JSONObject uiObj = dataRef.get().optJSONObject("ui");
                uiObj = uiObj != null ? uiObj : new JSONObject();
                uiObj.put("cell_listener_prompt", new_prompt == null ? "" : new_prompt);
                dataRef.get().put("ui", uiObj);
            }
            clients[networkID].flush();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void sendHeroAttackIndicator(@Nullable Integer target, int networkID) {
        sendHeroAttackIndicator(target == null? -1: target, networkID);
    }


    private static final HashMap<Integer, Integer> attackIndicatorCache = new HashMap<>();
    public static void sendHeroAttackIndicator(int target, int networkID) {
        if (networkID <0)
        {
            return;
        }
        if (clients[networkID] == null) {
            return;
        }
        if (attackIndicatorCache.containsKey(networkID)) {
            if (attackIndicatorCache.get(networkID) == target) {
                return;
            }
        }
        attackIndicatorCache.put(networkID, target);
        try {
            AtomicReference<JSONObject> dataRef = clients[networkID].packet.dataRef;
            synchronized (clients[networkID].packet.dataRef) {
                JSONObject uiObj = dataRef.get().optJSONObject("ui");
                uiObj = uiObj != null ? uiObj : new JSONObject();
                uiObj.put("attack_indicator_target", target);
                dataRef.get().put("ui", uiObj);
            }
            clients[networkID].flush();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void sendLevelSize(Level level) {
        for (int i = 0; i < clients.length; i++){
            sendLevelSize(level, i);
        }
    }
    public static void sendLevelSize(Level level, int ID){
        if (ID < 0) return;
        if (clients[ID] == null) return;
        clients[ID].packet.packAndAddLevelParams(level);
        clients[ID].flush();
    }

    public static void sendTraps(Level level) {
        for (int i = 0; i < clients.length; i++){
            if(clients[i] != null) {
                for (int pos = 0; pos < level.length(); pos++) {
                    clients[i].packet.packAndAddTrap(pos, level.traps.get(pos, null));
                }
                clients[i].flush();
            }
        }
    }

    public static void sendCounter(Hero hero, float portion) {
        ClientThread client = clients[hero.networkID];
        if (client != null) {
            client.packet.packAndAddCounter(portion);
            client.flush();
        }
    }
    public static void sendRedirect(Hero hero, RedirectPacket redirectPacket)
    {
        clients[hero.networkID].packet.packAndAddRedirect(redirectPacket);
        clients[hero.networkID].flush();
    }

    public static void sendAction(@Nullable Hero hero, NetworkAction networkAction) {
        if (hero == null) return;
        int networkId = hero.networkID;
        if (networkId < 0) {
            return;
        }
        if (networkId >= clients.length) {
            Log.e("Hero network id is too much");
            return;
        }
        var client = clients[networkId];
        if (client != null) {
            client.packet.addAction(networkAction);
        }
    }

    public static void sendActionForAll(NetworkAction networkAction) {
        sendActionForAll(networkAction, false);
    }

    public static void sendActionForAll(NetworkAction networkAction, boolean flush) {
        for (int i = 0; i < clients.length; i++) {
            var client = clients[i];
            if (client != null) {
                client.packet.addAction(networkAction);
                if (flush) {
                    client.flush();
                }
            }
        }
    }
}
