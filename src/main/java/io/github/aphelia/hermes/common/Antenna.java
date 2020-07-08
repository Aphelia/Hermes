package io.github.aphelia.hermes.common;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class Antenna extends Thread {
    private static String token;
    private final static Gson json = new Gson();
    private static Antenna instance;
    private static Utils utils;
    private Socket socket;
    private Antenna(){}
    private final static String ip = "awesomest.us.to";
    private final static int port = 2565;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

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
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void passDeathMessage(String username) throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "DEATH");
        dataMap.put("user", username);
        dataMap.put("token", token);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void passJoinMessage(String username) throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "JOIN");
        dataMap.put("user", username);
        dataMap.put("token", token);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void passLeaveMessage(String username) throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "LEAVE");
        dataMap.put("user", username);
        dataMap.put("token", token);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try {
            dataOutputStream.writeUTF(json.toJson(dataMap));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String requestToken() throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();
        String newToken;
        dataMap.put("type", "TOKENREQUEST");
        outputStream.writeUTF(json.toJson(dataMap));
        newToken = json.fromJson(inputStream.readUTF(), HashMap.class).get("response").toString();
        token = newToken;
        return newToken;
    }

    void reconnect() {
        utils.log(Level.WARNING, "Lost connection to Aphelia. Will attempt reconnection.");

        try {
            int wait = 15;
            while (true) {
                TimeUnit.SECONDS.sleep(wait);
                try {
                    utils.log(Level.INFO, "Attempting to reconnect!");
                    this.socket.close();
                    this.socket = new Socket(ip, port);
                    inputStream = new DataInputStream(this.socket.getInputStream());
                    outputStream = new DataOutputStream(this.socket.getOutputStream());
                    break;
                } catch(IOException e) {
                    wait *= 2;
                    utils.log(Level.WARNING, "Reconnection failed. Attempting to reconnect in " + wait + "seconds.");
                }
            }
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }

    }
}
