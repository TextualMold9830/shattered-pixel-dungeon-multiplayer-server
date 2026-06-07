package com.shatteredpixel.shatteredpixeldungeon.network;

import com.nikita22007.multiplayer.utils.Log;
import com.nikita22007.multiplayer.utils.text.LocalizedString;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
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
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.ChatMessageAction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
            clients[ID].packAndAddLevel(level);
            clients[ID].addTraps(level);
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
            clients[ID].packet.packAndAdd(new ShowWindowAction(type, windowID, args), clients[ID].clientHero);
            clients[ID].flush();
        }
    }

    //----------Actors


    @SuppressWarnings("unused") // todo should we use this?
    public static void sendActorRemoving(@NotNull Actor actor) {
        if (actor instanceof Buff) {
            sendActionForAll(new BuffRemoveAction((Buff) actor));
            return;
        }
        SendData.sendActionForAll(new ActorRemoveAction(actor));
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
    

    //---------------------------Chat
    public static void enqueueChatMessageToAll(String message) {
        enqueueChatMessageToAll(LocalizedString.raw(message));
    }

    public static void enqueueChatMessageToAll(LocalizedString message) {
        ChatMessageAction messageAction = packMessage(message);
        for (ClientThread client : clients) {
            if (client != null) {
                client.enqueueChatMessage(messageAction);
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
        ChatMessageAction messageAction = packMessage(message);
        if (ID >= 0 && ID < clients.length && clients[ID] != null) {
            clients[ID].enqueueChatMessage(messageAction);
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
        ChatMessageAction messageAction = packMessage(message);
        for (int i = 0; i < clients.length; i++) {
            if (i == exceptId) {
                continue;
            }
            ClientThread client = clients[i];
            if (client != null) {
                client.enqueueChatMessage(messageAction);
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

    private static ChatMessageAction packMessage(LocalizedString message) {
        return new ChatMessageAction(message);
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
            client.packet.packAndAdd(new ItemAction.Remove(path), hero);
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
            client.packet.packAndAdd(new ItemAction.Update(path, item, hero), hero);
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
            client.packet.packAndAdd(new ItemAction.Add(path, item, hero), hero);
        }
    }


    //---------------------------Heaps
    public static void sendHeap(Heap heap) {
        if (heap.isEmpty()) {
            return;
        }
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] == null) {
                continue;
            }
            clients[i].packet.packAndAdd(new HeapUpdateAction(heap), clients[i].clientHero);
            clients[i].flush();
        }
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
                clients[i].addTraps(level);
                clients[i].flush();
            }
        }
    }


    public static void sendTrap(int cell) {
        //no `Trap trap` overload because you need to make sure that the trap exists on the level.
        @Nullable Trap trap = Dungeon.level == null ? null : Dungeon.level.traps.get(cell, null);
        //use this method instead of raw action to avoid sending invisible trap
        ImmutableNetworkAction action = trap == null || !trap.visible
                ? new TrapRemoveAction(cell)
                : new TrapUpdateAction(cell, trap);
        sendActionForAll(action);
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
        clients[hero.networkID].packet.packAndAdd(new RedirectServerAction(redirectPacket), hero);
        clients[hero.networkID].flush();
    }

    //--------------------------- Events/Actions
    public static void sendAction(@Nullable Hero hero, ImmutableNetworkAction networkAction) {
        sendLateLiveStateAction(hero, networkAction);
    }

    public static void sendLateLiveStateAction(@Nullable Hero hero, LiveStateNetworkAction  networkAction) {
        if (hero == null) return;
        int networkId = hero.networkID;
        if (networkId < 0) {
            return;
        }
        if (networkId >= clients.length) {
            Log.e("SendData","Hero network id is too much");
            return;
        }
        var client = clients[networkId];
        if (client != null) {
            client.packet.addLateLiveStateAction(networkAction);
        }
    }

    public static void sendActionForAll(ImmutableNetworkAction networkAction) {
        for (int i = 0; i < clients.length; i++) {
            var client = clients[i];
            if (client != null) {
                client.packet.addAction(networkAction);
            }
        }
    }

    public static void packAndSendAction(@Nullable Hero hero, LiveStateNetworkAction networkAction) {
        if (hero == null) return;
        int networkId = hero.networkID;
        if (networkId < 0 || networkId >= clients.length) return;
        var client = clients[networkId];
        if (client != null) {
            client.packet.packAndAdd(networkAction, client.clientHero);
        }
    }

    public static void packAndSendActionForAll(LiveStateNetworkAction networkAction) {
        for (int i = 0; i < clients.length; i++) {
            var client = clients[i];
            if (client != null) {
                client.packet.packAndAdd(networkAction, client.clientHero);
            }
        }
    }

    public static void sendLateLiveStateActionForAll(LiveStateNetworkAction networkAction) {
        for (int i = 0; i < clients.length; i++) {
            var client = clients[i];
            if (client != null) {
                client.packet.addLateLiveStateAction(networkAction);
            }
        }
    }
}
