package com.shatteredpixel.shatteredpixeldungeon.network;
// based on https://developer.android.com/training/connect-devices-wirelessly/nsd.html#java


import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.*;
import com.shatteredpixel.shatteredpixeldungeon.network.actions.serializers.*;
import com.shatteredpixel.shatteredpixeldungeon.plugins.PluginLoader;
import com.shatteredpixel.shatteredpixeldungeon.plugins.PluginManager;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.texturepack.TexturePackManager;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.ActorSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BlobSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.CharSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BagSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.HeapSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.HeapRemovalSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.ItemSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SerializerRegistry;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.WindowSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.PlantSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.PlantRemovalSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.ParticleFactorySerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.TrapSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.TrapRemovalSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BuffSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SpeckFactorySerializer;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BelongingsSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.BuffRemovalSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SpecialSlotDefinitionsSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.SplashFactorySerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.KeyIndicatorSerializer;
import com.nikita22007.multiplayer.noosa.particles.Emitter;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters.EmitterAnchorSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters.EmitterBurstSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters.EmitterPourSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters.EmitterStartSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.emitters.EmitterStopSerializer;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.emitters.EmitterAnchor;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.KeyIndicatorDTO;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.WindowDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.PlantDTO;
import com.shatteredpixel.shatteredpixeldungeon.network.serializers.dtos.TrapDTO;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.watabou.noosa.particles.SerializableParticleFactory;

public class Server extends Thread {
    public static final SerializerRegistry SERIALIZERS = new SerializerRegistry();

    static {
        SERIALIZERS.register(Item.class, "default", new ItemSerializer());
        SERIALIZERS.register(Heap.class, "default", new HeapSerializer());
        SERIALIZERS.register(Heap.class, "remove", new HeapRemovalSerializer());
        SERIALIZERS.register(Bag.class, "default", new BagSerializer());
        SERIALIZERS.register(Belongings.class, "default", new BelongingsSerializer());
        SERIALIZERS.register(Belongings.class, "special_slot_definitions", new SpecialSlotDefinitionsSerializer());
        SERIALIZERS.register(Char.class, "default", new CharSerializer());
        SERIALIZERS.register(Blob.class, "default", new BlobSerializer());
        SERIALIZERS.register(Actor.class, "default", new ActorSerializer());
        SERIALIZERS.register(KeyIndicatorDTO.class, "default", new KeyIndicatorSerializer());
        SERIALIZERS.register(WindowDTO.class, "default", new WindowSerializer());
        SERIALIZERS.register(PlantDTO.class, "default", new PlantSerializer());
        SERIALIZERS.register(PlantDTO.class, "remove", new PlantRemovalSerializer());
        SERIALIZERS.register(TrapDTO.class, "default", new TrapSerializer());
        SERIALIZERS.register(TrapDTO.class, "remove", new TrapRemovalSerializer());
        SERIALIZERS.register(Buff.class, "default", new BuffSerializer());
        SERIALIZERS.register(Buff.class, "remove", new BuffRemovalSerializer());
        SERIALIZERS.register(SerializableParticleFactory.class, "default", new ParticleFactorySerializer());
        SERIALIZERS.register(Speck.SpeckFactory.class, "default", new SpeckFactorySerializer());
        SERIALIZERS.register(Splash.SplashFactory.class, "default", new SplashFactorySerializer());
        SERIALIZERS.register(EmitterAnchor.class, "default", new EmitterAnchorSerializer());
        SERIALIZERS.register(Emitter.class, "burst", new EmitterBurstSerializer());
        SERIALIZERS.register(Emitter.class, "start", new EmitterStartSerializer());
        SERIALIZERS.register(Emitter.class, "pour", new EmitterPourSerializer());
        SERIALIZERS.register(Emitter.class, "stop", new EmitterStopSerializer());

        //actions
        SERIALIZERS.register(ActorRemoveAction.class, new ActorRemoveActionSerializer());
        SERIALIZERS.register(ChatMessageAction.class, new ChatMessageActionSerializer());
        SERIALIZERS.register(GameSceneFlashAction.class, new GameSceneFlashActionSerializer());
        SERIALIZERS.register(SurpriseVisualAction.class, new SurpriseVisualActionSerializer());
        SERIALIZERS.register(FlareVisualAction.class, new FlareVisualActionSerializer());
        SERIALIZERS.register(DiscoverTileAction.class, new DiscoverTileActionSerializer());
        SERIALIZERS.register(UpdateFovAction.class, new UpdateFovActionSerializer());
        SERIALIZERS.register(SetLevelEntranceAction.class, new SetLevelEntranceActionSerializer());
        SERIALIZERS.register(SetLevelExitAction.class, new SetLevelExitActionSerializer());
        SERIALIZERS.register(CharSpriteStateAction.class, new CharSpriteStateActionSerializer());
        SERIALIZERS.register(HeapRemoveAction.class, new HeapRemoveActionSerializer());
        SERIALIZERS.register(ShowBannerAction.class, new ShowBannerActionSerializer());
        SERIALIZERS.register(TexturePackAction.class, new TexturePackActionSerializer());
        SERIALIZERS.register(CharSpriteAction.class, new CharSpriteActionSerializer());
        SERIALIZERS.register(SpriteFlashAction.class, new SpriteFlashActionSerializer());
        SERIALIZERS.register(InterlevelSceneAction.class, new InterlevelSceneActionSerializer());
        SERIALIZERS.register(UpdateCellsAction.class, new UpdateCellsActionSerializer());
        SERIALIZERS.register(SetLevelStatesAction.class, new SetLevelStatesActionSerializer());
        SERIALIZERS.register(SetLevelTilesAction.class, new SetLevelTilesActionSerializer());
        SERIALIZERS.register(SetLevelVisualsAction.class, new SetLevelVisualsActionSerializer());
        SERIALIZERS.register(ResizeLevelAction.class, new ResizeLevelActionSerializer());
        SERIALIZERS.register(ShowStatusAction.class, new ShowStatusActionSerializer());
        SERIALIZERS.register(ShakeCameraAction.class, new ShakeCameraActionSerializer());
        SERIALIZERS.register(HeroReadyAction.class, new HeroReadyActionSerializer());
        SERIALIZERS.register(HeroGoldAction.class, new HeroGoldActionSerializer());
        SERIALIZERS.register(HeroUUIDAction.class, new HeroUUIDActionSerializer());
        SERIALIZERS.register(HeroActorIdAction.class, new HeroActorIdActionSerializer());
        SERIALIZERS.register(HeroExperienceAction.class, new HeroExperienceActionSerializer());
        SERIALIZERS.register(HeroStrengthAction.class, new HeroStrengthActionSerializer());
        SERIALIZERS.register(HeroSubclassAction.class, new HeroSubclassActionSerializer());
        SERIALIZERS.register(HeroTalentsAction.class, new HeroTalentsActionSerializer());
        SERIALIZERS.register(HeroClassAction.class, new HeroClassActionSerializer());
        SERIALIZERS.register(UpdateFloorInfoAction.class, new UpdateFloorInfoActionSerializer());
        SERIALIZERS.register(LockedFloorStateAction.class, new LockedFloorStateActionSerializer());
        SERIALIZERS.register(KeysIndicatorAction.class, new KeysIndicatorActionSerializer());
        SERIALIZERS.register(UpdateCounterAction.class, new UpdateCounterActionSerializer());
        SERIALIZERS.register(CellListenerPromptAction.class, new CellListenerPromptActionSerializer());
        SERIALIZERS.register(AttackIndicatorTargetAction.class, new AttackIndicatorTargetActionSerializer());
        SERIALIZERS.register(ResumeButtonVisibleAction.class, new ResumeButtonVisibleActionSerializer());
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
            serverStepThread.setName("SHPD Server Step Thread");
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
        if (!Actor.processing() && !Game.switchingScene()) {
            SendData.forceFlushAll();
        }
    }

    public static void startClientThread(Socket client) throws IOException {
        QueryClientThread queryThread = new QueryClientThread(client);
        queryThread.setDaemon(true);
        queryThread.setName("SPDMP Query Client");
        queryThread.start();
    }

    public static void joinClient(Socket client, String heroClass, String uuid) throws IOException {
        synchronized (clients) {
            for (int i = 0; i <= clients.length; i++) {   //search not connected
                if (i == clients.length) { //If we test last and it's connected too
                    rejectClient(client, "server_full", "Server is full");
                    client.close();
                } else if (clients[i] == null) {
                    client.setSoTimeout(0);
                    ClientThread thread = new ClientThread(i, client, null);
                    clients[i] = thread;
                    thread.InitPlayerHero(heroClass, uuid);
                    break;
                }
            }
        }
    }

    static void rejectClient(Socket client, String reason, String message) throws IOException {
        JSONObject action = new JSONObject();
        action.put("action_name", "connection_rejected");
        action.put("reason", reason);
        action.put("message", message);

        JSONObject packet = new JSONObject();
        packet.put(Protocol.FIELD_PACKET_TYPE, Protocol.PACKET_ACTIONS_BATCH);
        packet.put("actions", new JSONArray().put(action));

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                client.getOutputStream(),
                Charset.forName(ClientThread.CHARSET).newEncoder()
        ));
        writer.write(packet.toString());
        writer.write('\n');
        writer.flush();
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
            ShatteredPixelDungeon.platform.registerService(port, serverInfoProperties());
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
            serverInfo.put("server_version", Game.version);
            serverInfo.put("server_version_code", Game.versionCode);
            serverInfo.put("server_protocol_version", 2);
            return serverInfo;
    }
    private static Map<String, String> serverInfoProperties() {
        JSONObject info = serverInfo();
        Map<String, String> properties = new HashMap<>();
        Iterator<String> keys = info.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = info.opt(key);
            if (value != null) {
                properties.put(key, dnsPropertyValue(String.valueOf(value)));
            }
        }
        return properties;
    }
    private static String dnsPropertyValue(String value) {
        if (value.length() <= 200) {
            return value;
        }
        return value.substring(0, 200);
    }
    public static enum RegListenerState {NONE, UNREGISTERED, REGISTERED, REGISTRATION_FAILED, UNREGISTRATION_FAILED}
}
