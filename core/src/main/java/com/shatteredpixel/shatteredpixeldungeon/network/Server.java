package com.shatteredpixel.shatteredpixeldungeon.network;
// based on https://developer.android.com/training/connect-devices-wirelessly/nsd.html#java


import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.plugins.PluginLoader;
import com.shatteredpixel.shatteredpixeldungeon.plugins.PluginManager;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.texturepack.TexturePackManager;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.ActorSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.ActorRemovalSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.HeroSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BagSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.HeapSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.HeapRemovalSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.ItemSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializerRegistry;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.WindowSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.InterlevelSceneSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.PlantSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.TrapSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BuffSerializer;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BelongingsSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BuffSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BuffRemovalSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.LevelSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.CellsUpdateSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.KeyIndicatorSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.CellsUpdateDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.KeyIndicatorDTO;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.WindowDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.InterlevelSceneDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.PlantDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.TrapDTO;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;

public class Server extends Thread {
    public static final SerializerRegistry SERIALIZERS = new SerializerRegistry();

    static {
        SERIALIZERS.register(Item.class, "default", new ItemSerializer());
        SERIALIZERS.register(Heap.class, "default", new HeapSerializer());
        SERIALIZERS.register(Heap.class, "remove", new HeapRemovalSerializer());
        SERIALIZERS.register(Bag.class, "default", new BagSerializer());
        SERIALIZERS.register(Belongings.class, "default", new BelongingsSerializer());
        SERIALIZERS.register(Actor.class, "default", new ActorSerializer());
        SERIALIZERS.register(Actor.class, "hero", new ActorSerializer());
        SERIALIZERS.register(Actor.class, "character", new ActorSerializer());
        SERIALIZERS.register(Actor.class, "remove", new ActorRemovalSerializer());
        SERIALIZERS.register(Hero.class, "hero_block", new HeroSerializer());
        SERIALIZERS.register(Level.class, "resize_level", new LevelSerializer());
        SERIALIZERS.register(Level.class, "set_level_visuals", new LevelSerializer());
        SERIALIZERS.register(Level.class, "set_level_tiles", new LevelSerializer());
        SERIALIZERS.register(Level.class, "set_level_states", new LevelSerializer());
        SERIALIZERS.register(CellsUpdateDTO.class, "default", new CellsUpdateSerializer());
        SERIALIZERS.register(KeyIndicatorDTO.class, "default", new KeyIndicatorSerializer());
        SERIALIZERS.register(WindowDTO.class, "default", new WindowSerializer());
        SERIALIZERS.register(InterlevelSceneDTO.class, "default", new InterlevelSceneSerializer());
        SERIALIZERS.register(PlantDTO.class, "default", new PlantSerializer());
        SERIALIZERS.register(TrapDTO.class, "default", new TrapSerializer());
        SERIALIZERS.register(Buff.class, "default", new BuffSerializer());
        SERIALIZERS.register(Buff.class, "remove", new BuffRemovalSerializer());
    }

    public static ArrayList<String> textures = new ArrayList<>();
    // will return in the future
    public static PluginManager pluginManager = new PluginManager(new PluginLoader(ShatteredPixelDungeon.platform.loadPlugins()));

    //primitive vars
    public static String serviceName;
    protected static int localPort;
    public static boolean started = false;

    //network
    protected static ServerSocket serverSocket;
    protected static Server serverThread;
    protected static ClientThread[] clients = new ClientThread[0];
    protected static RelayThread relay;

    //NSD
    public static volatile RegListenerState regListenerState = RegListenerState.NONE;
    protected static final int TIME_TO_STOP = 3000; //ms
    protected static final int SLEEP_TIME = 100; // ms

    protected static Thread serverStepThread;

    public static boolean startServerStepLoop() {
        if ((serverStepThread != null) && (serverStepThread.isAlive())) {
            return false;
        }
        {
            serverStepThread = new Thread() {
                @Override
                public void run() {
                    //
                    try {
                        while (!interrupted()) {
                            if (Game.instance != null) {
                                if ((Game.scene() instanceof GameScene)) {
                                    Game.instance.server_step();
                                    sleep(0);
                                } else {
                                    sleep(500);
                                }
                            } else {
                                sleep(500);
                            }
                        }
                    } catch (InterruptedException ignored) {

                    }
                }
            };
            serverStepThread.setDaemon(true);
        }
        serverStepThread.start();
        return true;
    }

    public static boolean startServer() {
        if (started) {
            GLog.h("start when started: WTF?! WHO AND WHERE USED THIS?!");
            return false;
        }
        clients = new ClientThread[SPDSettings.maxPlayers()];
        serviceName = SPDSettings.serverName();
        regListenerState = RegListenerState.NONE;
        if (!initializeServerSocket()) {
            return false;
        }
        registerService(localPort);
        started = true;
        serverThread = new Server();
        TexturePackManager.loadTextures("textures/");
        serverThread.start();
        pluginManager.initialize();
        return started;
    }

    public static boolean stopServer() {
        if (!started) {
            return true;
        }
        started = false;
        if (relay != null) {
            relay.interrupt();
            relay = null;
        }
        serverStepThread.interrupt();
        //ClientThread.sendAll(Codes.SERVER_CLOSED); //todo
        unregisterService();

        return true;
    }

    public static void parseActions() {
        for (ClientThread client : clients) {
            if (client == null) {
                continue;
            }
            client.parse();
        }
    }

    public static void startClientThread(Socket client) throws IOException {
        synchronized (clients) {
            for (int i = 0; i <= clients.length; i++) {   //search not connected
                if (i == clients.length) { //If we test last and it's connected too
                    //todo use new json
                    new DataOutputStream(client.getOutputStream()).writeInt(Codes.SERVER_FULL);
                    client.close();
                } else if (clients[i] == null) {
                        Hero emptyHero = null;
                        clients[i] = new ClientThread(i, client, emptyHero); //found
                    break;
                }
            }
        }
    }

    //Server thread
    public void run() {
        if (SPDSettings.onlineMode()) {
            relay = new RelayThread();
            relay.start();
        }
        while (started) { //clients  listener
            Socket client;
            try {
                client = serverSocket.accept();  //accept connect
                startClientThread(client);
            } catch (IOException e) {
                if (!(e.getMessage().equals("Socket is closed"))) {  //"Socket is closed" means that client disconnected
                    GLog.h("IO exception:".concat(e.getMessage()));
                }
            }
        }
    }
    //DNS-SD
    protected static void registerService(int port) {
        try {
            ShatteredPixelDungeon.platform.registerService(port);
        } catch (Exception e) {
            Gdx.app.error("DNS", "Failed to start dns-sd service", e);
        }
    }

    public static void unregisterService() {
        try {
            ShatteredPixelDungeon.platform.unregisterService();
        } catch (Exception e) {
            Gdx.app.error("DNS", "Failed to stop dns-sd service", e);
        }
    }

    protected static boolean initializeServerSocket() {
        // Initialize a server socket on the next available port.
        try {
            serverSocket = new ServerSocket(SPDSettings.serverPort());
        } catch (Exception e) {
            return false;
        }
        // Store the chosen port.
        localPort = serverSocket.getLocalPort();
        return true;
    }
    public static int onlinePlayers(){
        int onlineCount = 0;
        for (int i = 0; i < clients.length; i++) {
            if (clients[i] != null){
                onlineCount++;
            }
        }
        return onlineCount;
    }
    public static JSONObject serverInfo(){
            JSONObject serverInfo = new JSONObject();
            serverInfo.put("name", SPDSettings.serverName());
            serverInfo.put("players", Server.onlinePlayers());
            serverInfo.put("max_players", Server.clients.length);
            serverInfo.put("challenges", SPDSettings.challenges());
            serverInfo.put("current_floor", Dungeon.depth);
            serverInfo.put("motd", SPDSettings.motd());
            return serverInfo;
    }
    public static enum RegListenerState {NONE, UNREGISTERED, REGISTERED, REGISTRATION_FAILED, UNREGISTRATION_FAILED}
}
