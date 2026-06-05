package com.shatteredpixel.shatteredpixeldungeon.network;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Compresses accumulated packet actions without changing non-cell event order.
 *
 * Level cell diffs are allowed to move across other actions, but their own order is preserved:
 * later cell updates override earlier cell updates, and a later level snapshot overrides all
 * earlier pending cell updates for the same data kind.
 *
 * A cell update is absorbed into the latest matching snapshot array when possible:
 * - update_cells.tiles updates the latest set_level_tiles.tiles snapshot;
 * - update_cells.states updates the latest set_level_states.states snapshot;
 * - if only one snapshot array exists, the uncovered part of update_cells remains pending;
 * - if both tiles and states are covered by snapshots, update_cells is not emitted.
 */
class NetworkPacketCompressor {

    static void compress(JSONObject data) throws JSONException {
        JSONArray actions = data.optJSONArray("actions");
        if (actions == null || actions.length() == 0) {
            return;
        }

        LevelActionsCompressor levelActions = new LevelActionsCompressor();
        for (int i = 0; i < actions.length(); i++) {
            Object actionValue = actions.opt(i);
            if (actionValue instanceof JSONObject) {
                levelActions.add((JSONObject) actionValue);
            } else {
                levelActions.add(actionValue);
            }
        }
        data.put("actions", levelActions.toActions());
    }

    private static class LevelActionsCompressor {
        private final ArrayList<Object> actions = new ArrayList<>();
        private final LinkedHashMap<Integer, Integer> pendingTiles = new LinkedHashMap<>();
        private final LinkedHashMap<Integer, Integer> pendingStates = new LinkedHashMap<>();
        private JSONArray currentTiles;
        private JSONArray currentStates;
        private JSONObject currentMessagesAction;

        void add(Object actionValue) {
            actions.add(actionValue);
        }

        void add(JSONObject action) {
            String actionName = action.optString("action_name", "");
            switch (actionName) {
                case "resize_level":
                    currentTiles = null;
                    currentStates = null;
                    clearPendingUpdates();
                    actions.add(action);
                    break;
                case "set_level_tiles":
                    currentTiles = action.optJSONArray("tiles");
                    pendingTiles.clear();
                    actions.add(action);
                    break;
                case "set_level_states":
                    currentStates = action.optJSONArray("states");
                    pendingStates.clear();
                    actions.add(action);
                    break;
                case "update_cells":
                    addCellsUpdate(action);
                    break;
                case "messages":
                    addMessagesUpdate(action);
                    break;
                default:
                    actions.add(action);
                    break;
            }
        }

        private void addMessagesUpdate(JSONObject action) {
            if (currentMessagesAction == null) {
                currentMessagesAction = new JSONObject();
                currentMessagesAction.put("action_name", "messages");
                currentMessagesAction.put("messages", new JSONArray());
                actions.add(currentMessagesAction);
            }
            JSONArray messagesArray = currentMessagesAction.optJSONArray("messages");
            if (messagesArray == null) {
                messagesArray = new JSONArray();
                currentMessagesAction.put("messages", messagesArray);
            }
            if (action.has("messages")) {
                JSONArray incomingMessages = action.optJSONArray("messages");
                if (incomingMessages != null) {
                    for (int i = 0; i < incomingMessages.length(); i++) {
                        messagesArray.put(incomingMessages.opt(i));
                    }
                }
            } else if (action.has("text")) {
                JSONObject messageObj = new JSONObject();
                messageObj.put("text", action.opt("text"));
                messagesArray.put(messageObj);
            }
        }

        private void clearPendingUpdates() {
            pendingTiles.clear();
            pendingStates.clear();
        }

        private void addCellsUpdate(JSONObject action) {
            JSONArray positions = action.optJSONArray("positions");
            if (positions == null) {
                return;
            }
            JSONArray tileUpdates = action.optJSONArray("tiles");
            JSONArray stateUpdates = action.optJSONArray("states");
            for (int i = 0; i < positions.length(); i++) {
                int pos = positions.optInt(i, -1);
                if (pos < 0) {
                    continue;
                }
                if (tileUpdates != null && i < tileUpdates.length()) {
                    int tile = tileUpdates.optInt(i);
                    if (!applyCellUpdate(currentTiles, pos, tile)) {
                        pendingTiles.put(pos, tile);
                    }
                }
                if (stateUpdates != null && i < stateUpdates.length()) {
                    int state = stateUpdates.optInt(i);
                    if (!applyCellUpdate(currentStates, pos, state)) {
                        pendingStates.put(pos, state);
                    }
                }
            }
        }

        private boolean applyCellUpdate(JSONArray values, int pos, int value) {
            if (values == null || pos >= values.length()) {
                return false;
            }
            try {
                values.put(pos, value);
                return true;
            } catch (JSONException e) {
                return false;
            }
        }

        JSONArray toActions() throws JSONException {
            addPendingUpdates();
            JSONArray output = new JSONArray();
            for (Object action : actions) {
                output.put(action);
            }
            return output;
        }

        private void addPendingUpdates() throws JSONException {
            if (pendingTiles.isEmpty() && pendingStates.isEmpty()) {
                return;
            }
            if (!pendingTiles.isEmpty() && pendingTiles.keySet().equals(pendingStates.keySet())) {
                actions.add(createCellsUpdate(pendingTiles, pendingStates));
                return;
            }
            if (!pendingTiles.isEmpty()) {
                actions.add(createCellsUpdate(pendingTiles, null));
            }
            if (!pendingStates.isEmpty()) {
                actions.add(createCellsUpdate(null, pendingStates));
            }
        }

        private JSONObject createCellsUpdate(@Nullable LinkedHashMap<Integer, Integer> tileUpdates,
                                             @Nullable LinkedHashMap<Integer, Integer> stateUpdates) throws JSONException {
            LinkedHashSet<Integer> positions = new LinkedHashSet<>();
            if (tileUpdates != null) positions.addAll(tileUpdates.keySet());
            if (stateUpdates != null) positions.addAll(stateUpdates.keySet());

            JSONArray positionsArray = new JSONArray();
            JSONArray tilesArray = tileUpdates == null ? null : new JSONArray();
            JSONArray statesArray = stateUpdates == null ? null : new JSONArray();
            for (Integer pos : positions) {
                positionsArray.put(pos);
                if (tilesArray != null) tilesArray.put(tileUpdates.get(pos));
                if (statesArray != null) statesArray.put(stateUpdates.get(pos));
            }

            JSONObject update = new JSONObject();
            update.put("action_name", "update_cells");
            update.put("positions", positionsArray);
            if (tilesArray != null) update.put("tiles", tilesArray);
            if (statesArray != null) update.put("states", statesArray);
            return update;
        }
    }
}
