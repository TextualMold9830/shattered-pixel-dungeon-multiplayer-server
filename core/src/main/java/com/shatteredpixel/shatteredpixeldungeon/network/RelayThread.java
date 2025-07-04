package com.shatteredpixel.shatteredpixeldungeon.network;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

import static com.shatteredpixel.shatteredpixeldungeon.network.ClientThread.CHARSET;


public class RelayThread extends Thread {
    protected OutputStreamWriter writeStream;
    protected BufferedWriter writer;
    protected InputStreamReader readStream;
    private BufferedReader reader;
    protected Socket clientSocket;
    private Callback callback = null;
    static int restartCount = 0;

    public RelayThread(){
        this.callback = new Callback() {
            @Override
            public void onDisconnect() {
            };
        };
    }
    public RelayThread(Callback callback){
        this.callback = callback;
    }
    private static int getRelayPort(){
        if (!SPDSettings.useCustomRelay()){
            return SPDSettings.defaultRelayServerPort;
        }
        int port = SPDSettings.customRelayPort();
       return (port != 0)? port: SPDSettings.defaultRelayServerPort;
    }

    private static String getRelayAddress(){
        if (!SPDSettings.useCustomRelay()){
            return SPDSettings.defaultRelayServerAddress;
        }
        String address = SPDSettings.customRelayAddress();
        return (!"".equals(address))? address : SPDSettings.defaultRelayServerAddress;
    }

    public void run() {
        Socket socket = null;
        String relayServerAddress = getRelayAddress();
        try {
            socket = new Socket(relayServerAddress, getRelayPort());
        } catch (IOException e) {
            e.printStackTrace();
            this.callback.onDisconnect();
            return;
        }
        this.clientSocket = socket;
        try {
            writeStream = new OutputStreamWriter(
                    clientSocket.getOutputStream(),
                    Charset.forName(CHARSET).newEncoder()
            );
            readStream = new InputStreamReader(
                    clientSocket.getInputStream(),
                    Charset.forName(CHARSET).newDecoder()
            );
            reader = new BufferedReader(readStream);
            writer = new BufferedWriter(writeStream, 16384);


            JSONObject name = new JSONObject();
            name.put("action", "name");
            name.put("name", SPDSettings.serverName());
            writer.write(name.toString());
            writer.write('\n');
            writer.flush();
            while (true) {
                String json = reader.readLine();
                if (json == null){
                    // we silence relay related messages for the first three times. We do not want confused users.
                    if (restartCount > 3) {
                        GLog.h("relay thread stopped");
                    }
                    socket.close();
                    this.callback.onDisconnect();
                    if (restartCount < 10) {
                        if (restartCount > 3) {
                            System.out.println("Restarting relay");
                        }
                        new RelayThread().start();
                        restartCount++;
                    } else {
                        System.out.println("Starting relay failed");
                    }
                    return;
                }
                JSONObject port_obj = new JSONObject(json);
                int port = port_obj.getInt("port");
                Socket client = new Socket(relayServerAddress, port);
                Server.startClientThread(client);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            try {
                Thread.sleep((1000 * new java.util.Random().nextInt(10)));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
            GLog.h("relay thread stopped");
            this.callback.onDisconnect();
            restartCount = 0;
            new RelayThread().start();
            GLog.h("Relay thread restarted");
        }
    }

    public interface Callback {
         void onDisconnect();
    }
}
