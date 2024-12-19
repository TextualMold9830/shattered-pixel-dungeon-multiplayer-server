package com.shatteredpixel.shatteredpixeldungeon.server.network;

import com.badlogic.gdx.Gdx;
import com.nikita22007.multiplayer.utils.Log;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import ru.nikita22007.synchronization.annotations.ServerSide;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;
import static com.watabou.utils.PathFinder.NEIGHBOURS8;


@ServerSide
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
    protected void InitPlayerHero(String className) {

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

        }
        Server.clients[threadID] = null;
        readStream = null;
        writeStream = null;
        jsonCall.cancel(true);
        GLog.n("player " + threadID + " disconnected");
    }

    private void sendInitData() {
        for (String texture : Server.textures) {
            sendTexture(texture);
        }

        packet.packAndAddLevel(level, clientHero);
        packet.packAndAddHero(clientHero);
        packet.packAndAddDepth(Dungeon.depth);
        packet.packAndAddIronKeysCount(Dungeon.depth);
        packet.addInventoryFull(clientHero);
        addAllCharsToSend();

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
