package io.github.aphelia.hermes.common;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class Antenna extends Thread {
    private static String token;
    private final static Gson json = new Gson();
    private static Antenna instance;
    private static Utils utils;
    private Socket socket;
    private Antenna(){}
    private final static String ip = "localhost";
    private final static int port = 2564;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private final Queue<HashMap<String, String>> backlog = new ConcurrentLinkedQueue<>();
    AtomicBoolean attemptingToReconnect = new AtomicBoolean();

    public static Antenna getInstance(Utils utilsInstance) {
        utils = utilsInstance;
        if(instance == null) {
            try {
                instance = new Antenna();
                instance.socket = new Socket(ip, port);
                instance.inputStream = new DataInputStream(instance.socket.getInputStream());
                instance.outputStream = new DataOutputStream(instance.socket.getOutputStream());
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }


    public void run() {
        while(true) {
            try {
                String in = inputStream.readUTF();
                HashMap<String, String> content = json.fromJson(in, HashMap.class);
                utils.sendMessage(utils.getFormat().replace("%name%", content.get("user")).replace("%message%", content.get("content")));
            } catch (IOException e) {
                try {
                    reconnect();
                }
                catch(Exception severe) {
                    severe.printStackTrace();
                    e.printStackTrace();
                    utils.log(Level.SEVERE, "A severe exception was encountered. Hermes by Aphelia is disabling its listener in order to preserve the integrity of the server. Try restarting.");
                    return;
                }
            }
        }
    }

    public void ping() throws IOException, InterruptedException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "HEARTBEAT");
        dataMap.put("token", token);
        dataMap.put("protocol", "H1");
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e){
            TimeUnit.SECONDS.sleep(5);
            this.socket.close();
            this.socket = new Socket(ip, port);
        }
        token = TokenStorage.getToken();
    }

    public void passChatMessage(String username, String message) throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "CHAT");
        dataMap.put("content", message);
        dataMap.put("user", username);
        dataMap.put("token", token);
        dataMap.put("protocol", "H1");
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e) {
            backlog.add(dataMap);
            e.printStackTrace();
            reconnect();
        }
    }
    public void passDeathMessage(String username) throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "DEATH");
        dataMap.put("user", username);
        dataMap.put("token", token);
        dataMap.put("protocol", "H1");
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e){
            backlog.add(dataMap);
            e.printStackTrace();
            reconnect();
        }
    }
    public void passJoinMessage(String username, int online, int max) throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "JOIN");
        addDefaults(username, online, max, dataMap);
    }

    public void passLeaveMessage(String username, int online, int max) throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "LEAVE");
        addDefaults(username, online, max, dataMap);
    }

    private void addDefaults(String username, int online, int max, HashMap<String, String> dataMap) throws IOException {
        dataMap.put("user", username);
        dataMap.put("online", Integer.toString(online));
        dataMap.put("max", Integer.toString(max));
        dataMap.put("token", token);
        dataMap.put("protocol", "H1");
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e) {
            backlog.add(dataMap);
            e.printStackTrace();
            reconnect();
        }
    }

    public void passConnectMessage() throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "CONNECT");
        dataMap.put("token", token);
        dataMap.put("protocol", "H1");
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e) {
            backlog.add(dataMap);
            e.printStackTrace();
            reconnect();
        }
    }

    public void passDisconnectMessage() throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "DISCONNECT");
        dataMap.put("token", token);
        dataMap.put("protocol", "H1");
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e) {
            backlog.add(dataMap);
            e.printStackTrace();
            reconnect();
        }
    }

    public String requestToken() throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        String newToken;
        dataMap.put("type", "TOKENREQUEST");
        dataMap.put("protocol", "H1");
        outputStream.writeUTF(json.toJson(dataMap));
        newToken = json.fromJson(inputStream.readUTF(), HashMap.class).get("response").toString();
        token = newToken;
        return newToken;
    }

    void reconnect() {
        if(attemptingToReconnect.get()) return;
        attemptingToReconnect.set(true);
        utils.log(Level.WARNING, "Lost connection to Aphelia. Will attempt reconnection.");
        try {
            int wait = 5;
            while (true) {
                TimeUnit.SECONDS.sleep(wait);
                try {
                    utils.log(Level.INFO, "Attempting to reconnect!");
                    this.socket.close();
                    this.socket = new Socket(ip, port);
                    inputStream = new DataInputStream(this.socket.getInputStream());
                    outputStream = new DataOutputStream(this.socket.getOutputStream());
                    utils.log(Level.INFO, "Reconnected; processing backlog.");
                    while(!backlog.isEmpty()) {
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataOutputStream.writeUTF(json.toJson(backlog.remove()));
                    }
                    utils.log(Level.INFO, "Backlog processed.");
                    break;
                } catch(IOException e) {
                    wait *= 2;
                    if(wait > 300) wait = 300;
                    utils.log(Level.WARNING, "Reconnection failed. Attempting to reconnect in " + wait + " seconds.");
                }
            }
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }

    }
}
