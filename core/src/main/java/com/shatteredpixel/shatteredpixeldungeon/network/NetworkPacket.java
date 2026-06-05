package com.shatteredpixel.shatteredpixeldungeon.network;

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
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.TrapDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.WindowDTO;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NetworkPacket {

    private final List<LiveStateNetworkAction> actions;

    public NetworkPacket() {
        actions = new ArrayList<>();
    }

    public synchronized void clearData() {
        actions.clear();
    }

    @NotNull
    public static NetworkPacket fromChatMessages(@NotNull List<@NotNull ChatMessageAction> messages) {
        final NetworkPacket networkPacket = new NetworkPacket();
        for (ImmutableNetworkAction action : messages) {
            networkPacket.addAction(action);
        }
        return networkPacket;
    }

    @Deprecated
    public void addAction(@NotNull JSONObject actionObj) {
        addAction(serializedActionFrom(actionObj));
    }

    public void addAction(@NotNull ImmutableNetworkAction action) {
        addLateLiveStateAction(action);
    }

    public synchronized void addLateLiveStateAction(@NotNull LiveStateNetworkAction action) {
        Objects.requireNonNull(action);
        actions.add(action);
    }

    public synchronized void packAndAdd(@NotNull LiveStateNetworkAction action) {
        JSONObject serialized = serializeAction(action);
        if (serialized.length() > 0) {
            addAction(serializedActionFrom(serialized));
        }
    }

    public void packAndAdd(@NotNull ImmutableNetworkAction action) {
        addAction(action);
    }

    private static SerializedAction serializedActionFrom(@NotNull JSONObject actionObj) {
        if (!actionObj.has("action_name")) {
            throw new IllegalArgumentException("Serialized action must have action_name");
        }
        String actionName = actionObj.getString("action_name");
        actionObj.remove("action_name");
        return new SerializedAction(actionName, actionObj);
    }

    public synchronized JSONObject serialize() {
        JSONObject packet = new JSONObject();
        packet.put(Protocol.FIELD_PACKET_TYPE, Protocol.PACKET_ACTIONS_BATCH);

        JSONArray actionsArr = new JSONArray();
        for (LiveStateNetworkAction action : actions) {
            JSONObject serialized = serializeAction(action);
            if (serialized.length() > 0) {
                actionsArr.put(serialized);
            }
        }
        packet.put("actions", actionsArr);
        return packet;
    }

    private JSONObject serializeAction(@NotNull LiveStateNetworkAction action) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        Object serialized = ctx.serialize(action);
        if (serialized instanceof JSONObject && ((JSONObject) serialized).length() > 0) {
            return (JSONObject) serialized;
        }
        return new JSONObject();
    }

    public synchronized void compress() {
        List<LiveStateNetworkAction> compressed = NetworkPacketCompressor.compress(actions);
        actions.clear();
        actions.addAll(compressed);
    }


    public void packAndAddChar(@NotNull Char actor) {
        if (actor.id() <= 0) {
            return;
        }
        packAndAdd(new CharUpdateAction(actor));
    }

    public void packAndAddBlob(@NotNull Blob actor) {
        SerializationContext ctx = new SerializationContext(Server.SERIALIZERS, null);
        Object serialized = ctx.serialize(actor, "default");
        if (serialized instanceof JSONObject && ((JSONObject) serialized).length() > 0) {
            String actionName;
            actionName = "blob_update";

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
            Plant plant = level.plants.get(pos, null);
            if (plant != null) {
                addLateLiveStateAction(new PlantUpdateAction(pos, plant));
            }
        }
        for (int pos = 0; pos < level.length(); pos++) {
            packAndAddTrap(pos, level.traps.get(pos, null));
        }
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


    public void packAndAddRedirect(RedirectPacket redirectPacket) {
        JSONObject event = redirectPacket.toJSON();
        event.put("action_name", "redirect_server");
        addAction(event);
    }

    public static final class SerializedAction implements ImmutableNetworkAction {
        private final String actionName;
        private final JSONObject actionObj;

        @Contract(pure = true)
        private SerializedAction(@NotNull String actionName, @NotNull JSONObject actionObj) {
            Objects.requireNonNull(actionName);
            Objects.requireNonNull(actionObj);
            this.actionName = actionName;
            this.actionObj = actionObj;
        }

        @Override
        public @NotNull String actionName() {
            return actionName;
        }

        public JSONObject actionObj() {
            return actionObj;
        }
    }
}
