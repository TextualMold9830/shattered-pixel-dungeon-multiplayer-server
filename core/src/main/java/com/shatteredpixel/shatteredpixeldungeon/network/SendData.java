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
import com.shatteredpixel.shatteredpixeldungeon.network.actions.*;
import com.shatteredpixel.shatteredpixeldungeon.network.packets.RedirectPacket;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.shatteredpixel.shatteredpixeldungeon.network.Server.clients;

public class SendData {
    public static final float CHAT_FLUSH_INTERVAL = 0.1f;
    private static float chatFlushElapsed = 0f;

    //---------------------------Level

    public static void sendLevel(Level level, Hero hero) { //keep because of observer
        int ID = hero.networkID;
        if ((ID != -1) && (clients[ID] != null)) {
            PlantCache.clear();
            TrapCache.clear();
            clients[ID].packet.packAndAddLevel(level, clients[ID].clientHero);
        }
    }

    //-----------------------------Interlevel Scene
    // we keep his section because of double force flash

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

    //----------Actors
    public static void sendActor(Actor actor) {
        if (actor == null) {
            return;
        }
        if (actor instanceof Buff) { //todo fix this
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

    @SuppressWarnings("unused") // todo should we use this?
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

    public static void sendAllChars() {
        for (int ID = 0; ID < clients.length; ID++) {
            if (clients[ID] != null) {
                clients[ID].addAllCharsToSend();
            }
        }
    }

    //---------------------------Sprites
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

    //---------------------------Packet Flush

    @Contract(pure = true)
    @Deprecated
    public static void flush(Hero hero) {
    }

    @Contract(pure = true)
    @Deprecated
    public static void flush(int networkID) {
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

    //---------------------------Messages

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


    //---------------------------Chat
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


    //---------------------------Items
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


    //---------------------------Heaps
    public static void sendHeap(Heap heap) {
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] == null) {
                continue;
            }
            clients[i].packet.addHeap(heap, clients[i].clientHero);
            clients[i].flush();
        }
    }

    //---------------------------Plants
    public static void sendPlant(int pos, Plant plant) {
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] == null) {
                continue;
            }
            clients[i].packet.packAndAddPlant(pos, plant);
            clients[i].flush();
        }
    }

    //---------------------------Buffs
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

    //--------------------------- External Actions
    @Deprecated
    public static void sendCustomActionForAll(@NotNull JSONObject action_obj) {
        for (int i = 0; i < clients.length; i++) {
            sendCustomAction(action_obj, i);
        }
    }

    @Deprecated
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

    @Deprecated
    public static void sendCustomAction(JSONObject action_obj, int networkID) {
        assert action_obj.has("action_name") : "Action object must contains \"action_type\" field";
        if (networkID <= -1) {
            return;
        }
        if (clients[networkID] != null) {
            clients[networkID].packet.addAction(action_obj);
        }
    }

    //--------------------------- More Effects
    public static void sendActionDiscoverTile(int pos, int oldValue) {
        sendActionForAll(new DiscoverTileAction(pos, oldValue));
    }

    //--------------------------- UI
    public static void sendCellListenerPrompt(LocalizedString new_prompt, int networkID) {
        if (networkID < 0){
            return;
        }
        if (clients[networkID] == null) {
            return;
        }
        clients[networkID].packet.addAction(new CellListenerPromptAction(new_prompt));
        clients[networkID].flush();
    }
    public static void sendHeroAttackIndicator(@Nullable Integer target, int networkID) {
        sendHeroAttackIndicator(target == null? -1: target, networkID);
    }


    private static final HashMap<Integer, Integer> attackIndicatorCache = new HashMap<>();

    public static int getHeroAttackIndicatorTarget(int networkID) {
        return attackIndicatorCache.getOrDefault(networkID, -1);
    }

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
        clients[networkID].packet.addAction(new AttackIndicatorTargetAction(target));
        clients[networkID].flush();
    }

    public static void sendLockedFloorState(boolean locked) {
        sendActionForAll(new LockedFloorStateAction(locked));
    }


    //--------------------------- Traps
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

    //--------------------------- UI
    public static void sendCounter(Hero hero, float portion) {
        int ID = hero.networkID;
        if (ID < 0) return;
        ClientThread client = clients[hero.networkID];
        if (client != null) {
            client.packet.addAction(new UpdateCounterAction(portion));
        }
    }

    //--------------------------- Special
    public static void sendRedirect(Hero hero, RedirectPacket redirectPacket)
    {
        clients[hero.networkID].packet.packAndAddRedirect(redirectPacket);
        clients[hero.networkID].flush();
    }

    //--------------------------- Events/Actions
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
        for (int i = 0; i < clients.length; i++) {
            var client = clients[i];
            if (client != null) {
                client.packet.addAction(networkAction);
            }
        }
    }
}
