package com.shatteredpixel.shatteredpixeldungeon.network;

import com.badlogic.gdx.Gdx;
import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.plugins.events.ChatEvent;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.DeviceCompat;
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
    public static final String SERVER_TYPE = "SPD";

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
        sendServerType();
        sendServerUUID();
        if (clientHero != null){
            sendInitData();
        }
        updateTask();
    }

    private void sendServerUUID() {
        packet.addServerUUID();
        flush();
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
            Gdx.app.error("ParseThread", e.getMessage());
            return null;
        }
    }

    public void parse(@NotNull String json) throws JSONException {
        JSONObject data = new JSONObject(json);
        Gdx.app.log("ClientThread", data.toString(4));
        for (Iterator<String> it = data.keys(); it.hasNext(); ) {
            String token = it.next();
            try {
                switch (token) {
                    //Level block
                    case ("hero_class"): {
                        if (clientHero == null) {
                            InitPlayerHero(data.getString(token), data.getString("uuid"));
                        }
                        break;
                    }
                    case "uuid": {
                        //already parsed
                        break;
                    }
                    case ("talent_upgrade"): {
                        TalentButton.upgradeTalent(clientHero, Talent.valueOf(data.getString("talent_upgrade")));
                        break;
                    }
                    case ("cell_listener"): {
                        Integer cell = data.getInt(token);
                        if (clientHero.cellSelector != null) {
                            if (clientHero.cellSelector.getListener() != null) {
                                if (cell != -1) {
                                    clientHero.cellSelector.getListener().onSelect(cell);
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
                        //FIXME
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
                    case "chat": {
                        if (clientHero == null) {
                            break;
                        }
                        String text = data.getJSONObject(token).optString("message", null);
                        if (text == null) {
                            text = data.getJSONObject(token).optString("text", "");
                        }
                        if (text.isBlank()) {
                            break;
                        }
                        Server.pluginManager.fireEvent(new ChatEvent(text, clientHero));
                        GLog.i("%s: %s", clientHero.name,  text.trim());
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
                            case "EXAMINE": {
                                GameScene.examineCell(actionObj.getInt("cell"), clientHero);
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
        } catch (ExecutionException | InterruptedException e) {
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
    protected void sendServerType(){
        packet.addServerType(SERVER_TYPE);
        flush();
    }
    protected void InitPlayerHero(String className, String uuid) {
        HeroClass curClass;
        try {
            curClass = HeroClass.valueOf(className.toUpperCase());
        } catch (IllegalArgumentException e) {
            if (!className.equals("random")) { //classID==0 is random class, so it  is not error
                GLog.w("Incorrect class:%s; threadID:%s", className, threadID);
            }
            curClass = Random.element(HeroClass.values());
        }
        boolean heroFound = false;
        Hero newHero = new Hero();
        if (uuid != null && !uuid.isEmpty()) {
            Hero hero = Dungeon.loadHero(uuid);
            if (hero != null) {
                newHero = hero;
                heroFound = true;
            }
        }
        newHero.setSprite(new HeroSprite(newHero));
        clientHero = newHero;
        level.linkHero(newHero);
        newHero.live();
        if (!heroFound) {
        curClass.initHero(newHero);
        for (int i : NEIGHBOURS8) {
            if (Actor.findChar(level.entrance() + i) == null && level.passable[level.entrance() + i]) {
                newHero.pos = level.entrance() + i;
                break;
            }
        }
        }
        //newHero.pos = Dungeon.getPosNear(level.entrance);

        newHero.updateSpriteState();
        if (newHero.pos == -1) {
            newHero.pos = level.entrance(); //todo  FIXME
        }
        Actor.add(newHero);
        newHero.spendAndNext(1f);
        Dungeon.level.occupyCell(newHero);
        newHero.getSprite().place(newHero.pos);
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
        newHero.resendReady();
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
    public void send(int code, int Data) {
    }

    @Deprecated
    public static void sendAll(int code, int data) {
        for (int i = 0; i < Server.clients.length; i++) {
            if (Server.clients[i] != null) {
                Server.clients[i].send(code, data);
            }
        }
    }

    //hack
    boolean disconnected = false;
    public synchronized void disconnect() {
        if (!disconnected) {
            disconnected = true;
            try {
                clientSocket.close(); //it creates exception when we will wait client data
            } catch (Exception ignore) {
            }
            if (clientHero != null) {
                clientHero.next();
                Dungeon.removeHero(clientHero);
                clientHero = null;
            }
            Server.clients[threadID] = null;
            readStream = null;
            writeStream = null;
            jsonCall.cancel(true);
            GLog.n("player " + threadID + " disconnected");
            boolean notNullHero = false;
            for (Hero hero: Dungeon.heroes) {
                if (hero != null) {
                    GameScene.shouldProcess = true;
                    notNullHero = true;
                    break;
                }
            }
            if (!notNullHero) {
                GameScene.shouldProcess = false;
            }
        }
    }

    private synchronized void sendInitData() {
        for (String texture : Server.textures) {
            sendTexture(texture);
        }

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
