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
    private final static String ip = "localhost";
    private final static int port = 2564;

    public static Antenna getInstance(Utils utilsInstance) {
        utils = utilsInstance;
        if(instance == null) {
            try {
                instance = new Antenna();
                instance.socket = new Socket(ip, port);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }


    public void run() {
        DataInputStream inputStream = null;
        while(true) {
            try {
                if(inputStream == null) inputStream = new DataInputStream(socket.getInputStream());
                String in = inputStream.readUTF();
                HashMap<String, String> content = json.fromJson(in, HashMap.class);
                utils.sendMessage("§bFrom " + content.get("user").toUpperCase() + " via Discord: §1" + content.get("content"));
            } catch (IOException e) {
                try {
                    System.out.println("Failed to send message to Aphelia servers. Waiting 30 seconds to reconnect.");
                    TimeUnit.SECONDS.sleep(30);
                    System.out.println("Attempting to reconnect!");
                    this.socket.close();
                    this.socket = new Socket(ip, port);
                    inputStream = new DataInputStream(socket.getInputStream());
                    System.out.println("Reconnected!");
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
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream.writeUTF(json.toJson(dataMap));
        newToken = json.fromJson(dataInputStream.readUTF(), HashMap.class).get("response").toString();
        token = newToken;
        return newToken;
    }
}
