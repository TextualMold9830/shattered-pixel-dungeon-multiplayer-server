package com.shatteredpixel.shatteredpixeldungeon.network;

import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PlatformSupport;
import com.watabou.utils.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.heroes;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;
import static com.watabou.utils.PathFinder.NEIGHBOURS8;


class ClientThread implements Callable<String> {

    public static final String CHARSET = "UTF-8";

    protected OutputStreamWriter writeStream;
    protected BufferedWriter writer;
    protected InputStreamReader readStream;
    private BufferedReader reader;

    protected int threadID;

    protected final Socket clientSocket;

    protected Hero clientHero;

    protected final NetworkPacket packet = new NetworkPacket();

    @NotNull
    private FutureTask<String> jsonCall;

    public ClientThread(int ThreadID, Socket clientSocket, @Nullable Hero hero) {
        clientHero = hero;
        if (hero != null){
            hero.networkID = threadID;
        }
        this.clientSocket = clientSocket;
        try {
            writeStream = new OutputStreamWriter(
                    clientSocket.getOutputStream(),
                    Charset.forName(CHARSET).newEncoder()
            );
            readStream = new InputStreamReader(
                    clientSocket.getInputStream(),
                    Charset.forName(CHARSET).newDecoder()
            );
            this.threadID = ThreadID;
            reader = new BufferedReader(readStream);
            writer = new BufferedWriter(writeStream, 16384);
        } catch (IOException e) {
            GLog.n(e.getMessage());
            disconnect();
            return;
        }
        if (clientHero != null){
            sendInitData();
        }
        updateTask();
    }

    protected void updateTask() {
        if ((jsonCall == null) || (jsonCall.isDone())) {
            jsonCall = new FutureTask<String>(this);
            new Thread(jsonCall).start();
        }
    }
    @Override
    public String call() {
        if (clientSocket.isClosed()) {
            return null;
        }
        try {
            return reader.readLine();
        } catch (IOException e) {
            Log.e("ParseThread", e.getMessage());
            return null;
        }
    }

    public void parse(@NotNull String json) throws JSONException {

        JSONObject data = new JSONObject(json);
        for (Iterator<String> it = data.keys(); it.hasNext(); ) {
            String token = it.next();
            try {
                switch (token) {
                    //Level block
                    case ("hero_class"): {
                        if (clientHero == null) {
                            InitPlayerHero(data.getString(token));
                        }
                        break;
                    }
                    case ("cell_listener"): {
                        Integer cell = data.getInt(token);
                        if (clientHero.cellSelector != null) {
                            if (clientHero.cellSelector.listener != null) {
                                if (cell != -1) {
                                    clientHero.cellSelector.listener.onSelect(cell);
                                } else {
                                    clientHero.cellSelector.cancel();
                                }
                                GameScene.ready(clientHero);
                            }
                        }
                        break;
                    }
                    case ("action"): {
                        JSONObject actionObj = data.getJSONObject(token);
                        if (actionObj == null) {
                            GLog.n("Empty action object");
                            break;
                        }
                        String action = actionObj.getString("action_name");
                        if ((action == null) || (action.equals(""))) {
                            GLog.n("Empty action");
                            break;
                        }
                        List<Integer> slot = Utils.JsonArrayToListInteger(actionObj.getJSONArray("slot"));
                        if ((slot == null) || slot.isEmpty()) {
                            GLog.n("Empty slot: %s", slot);
                            break;
                        }
                        Item item = clientHero.belongings.getItemInSlot(slot);
                        if (item == null) {
                            GLog.n("No item in this slot. Slot: %s", slot);
                            break;
                        }
                        action = action.toLowerCase(Locale.ROOT);
                        boolean did_something = false;
                        for (String item_action : item.actions(clientHero)) {
                            if (item_action.toLowerCase(Locale.ROOT).equals(action)) {
                                did_something = true;
                                item.execute(clientHero, item_action);
                                break;
                            }
                        }
                        if (!did_something) {
                            GLog.n("No such action in actions list. Action: %s", action);
                            break;
                        }
                        break;
                    }
                    case "window": {
                        JSONObject resObj = data.getJSONObject(token);
                        Window.OnButtonPressed(
                                clientHero,
                                resObj.getInt("id"),
                                resObj.getInt("button"),
                                resObj.optJSONObject("result")
                        );
                        break;
                    }
                    case "toolbar_action": {
                        JSONObject actionObj = data.getJSONObject(token);
                        switch (actionObj.getString("action_name").toUpperCase(Locale.ENGLISH)) {
                            case "SLEEP": {
                                clientHero.rest(true);
                                break;
                            }
                            case "WAIT": {
                                clientHero.rest(false);
                                break;
                            }
                            case "SEARCH": {
                                clientHero.search(true);
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        GLog.n("Server: Bad token: %s", token);
                        break;
                    }
                }
            } catch (JSONException e) {
                assert false;
                GLog.n(String.format("JSONException in ThreadID:%s; Message:%s", threadID, e.getMessage()));
            }
        }
    }


    public void parse() {
        if (!jsonCall.isDone()) {
            return;
        }
        try {
            String json = jsonCall.get();
            if (json == null){
                disconnect();
                return;
            }
            updateTask();
            try {
                parse(json);
            } catch (JSONException e) {
                ShatteredPixelDungeon.reportException(e);
                GLog.n(e.getStackTrace().toString());
                disconnect();
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //network functions
    protected void flush() {
        try {
            synchronized (packet.dataRef) {
                if (packet.dataRef.get().length() == 0) {
                    return;
                }
                if (DeviceCompat.isDebug()) {
                    try {
                        Log.i("flush", "clientID: " + threadID + " data:" + packet.dataRef.get().toString(4));
                    } catch (JSONException ignored) {
                    }
                }
                synchronized (writer) {
                    writer.write(packet.dataRef.get().toString());
                    writer.write('\n');
                    writer.flush();
                }
                packet.clearData();
            }
        } catch (IOException e) {
            Log.e(String.format("ClientThread%d", threadID), String.format("IOException in threadID %s. Message: %s", threadID, e.getMessage()));
            disconnect();
        } catch (StackOverflowError e) {
            Log.e("st", "st", e);
        }
    }

    //some functions
    protected void InitPlayerHero(String className) {
        HeroClass curClass;
        try {
            curClass = HeroClass.valueOf(className.toUpperCase());
        } catch (IllegalArgumentException e) {
            if (!className.equals("random")) { //classID==0 is random class, so it  is not error
                GLog.w("Incorrect class:%s; threadID:%s", className, threadID);
            }
            curClass = Random.element(HeroClass.values());
        }
        Hero newHero = new Hero();
        clientHero = newHero;
        newHero.live();

        curClass.initHero(newHero);
        for (int i : NEIGHBOURS8) {
            if (Actor.findChar(level.entrance + i) == null && level.passable[level.entrance + i]) {
                newHero.pos = level.entrance + i;
                break;
            }
        }
        //newHero.pos = Dungeon.getPosNear(level.entrance);

        newHero.updateSpriteState();
        if (newHero.pos == -1) {
            newHero.pos = level.entrance; //todo  FIXME
        }
        Actor.add(newHero);
        Actor.occupyCell(newHero);
        newHero.sprite.place(newHero.pos);

        synchronized (heroes) { //todo fix it. It is not work
            for (int i = 0; i < heroes.length; i++) {
                if (heroes[i] == null) {
                    heroes[i] = newHero;
                    newHero.networkID = threadID;
                    newHero.name = "Player" + i;
                    break;
                }
            }

            if (newHero.networkID == -1) {
                throw new RuntimeException("Can not find place for hero");
            }
        }
        GameScene.addHeroSprite(newHero);

        sendInitData();
    }

    protected void addCharToSend(@NotNull Char ch) {
        synchronized (packet) {
            packet.packAndAddActor(ch, ch == clientHero);
        }
        //todo SEND TEXTURE
    }

    public void addAllCharsToSend() {
        for (Actor actor : Actor.all()) {
            if (actor instanceof Char) {
                addCharToSend((Char) actor);
            }
        }
    }

    public void addBadgeToSend(String badgeName, int badgeLevel) {
        packet.packAndAddBadge(badgeName, badgeLevel);
    }

    //send primitives
    @Deprecated
    public void sendCode(int code) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, boolean Data) {

        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, byte Data) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, int Data) {
    }

    //send arrays
    @Deprecated
    public void send(int code, boolean[] DataArray) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, byte[] DataArray) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, int[] DataArray) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, int var1, String message) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, String message) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    //send_serelliased_data
    @Deprecated
    public void sendData(int code, byte[] data) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    //send to all
    @Deprecated
    public static <T> void sendAll(int code) {
        for (int i = 0; i < Server.clients.length; i++) {
            Server.clients[i].sendCode(code);
        }
    }

    @Deprecated
    public static void sendAll(int code, int data) {
        for (int i = 0; i < Server.clients.length; i++) {
            if (Server.clients[i] != null) {
                Server.clients[i].send(code, data);
            }
        }
    }

    public void disconnect() {
        try {
            clientSocket.close(); //it creates exception when we will wait client data
        } catch (Exception ignore) {
        }
        if (clientHero != null) {
            clientHero.networkID = -1;
            clientHero.next();
            Dungeon.removeHero(clientHero);
        }
        Server.clients[threadID] = null;
        readStream = null;
        writeStream = null;
        jsonCall.cancel(true);
        GLog.n("player " + threadID + " disconnected");
    }

    private void sendInitData() {
        Server.textures.forEach(this::sendTexture);

        packet.packAndAddLevel(level, clientHero);
        packet.packAndAddHero(clientHero);
        packet.packAndAddDepth(Dungeon.depth);
        packet.packAndAddIronKeysCount(Dungeon.depth);
        packet.addInventoryFull(clientHero);
        addAllCharsToSend();

        Dungeon.observe(clientHero, false);
        packet.packAndAddVisiblePositions(clientHero.fieldOfView);
        //TODO send all  information

        flush();

        packet.packAndAddInterlevelSceneState("fade_out", null);
        flush();
    }
    private void sendTexture(String textureData){
        packet.packAndAddRawTextures(textureData);
        flush();
    }
}
