package io.github.aphelia.hermes.spigot;

import io.github.aphelia.hermes.bungee.BungeeUtils;
import io.github.aphelia.hermes.common.Antenna;
import io.github.aphelia.hermes.common.TokenStorage;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class SpigotPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(
                ChatColor.AQUA + "\n" +
                        "db   db d88888b d8888b. .88b  d88. d88888b .d8888. \n" +
                        "88   88 88'     88  `8D 88'YbdP`88 88'     88'  YP \n" +
                        "88ooo88 88ooooo 88oobY' 88  88  88 88ooooo `8bo.   \n" +
                        "88~~~88 88~~~~~ 88`8b   88  88  88 88~~~~~   `Y8b. \n" +
                        "88   88 88.     88 `88. 88  88  88 88.     db   8D \n" +
                        "YP   YP Y88888P 88   YD YP  YP  YP Y88888P `8888Y' \n" +
                        "\n" +
                        "By Project Aphelia—Spigot edition");
        getServer().getPluginManager().registerEvents(new EventWatcher(), this);
        this.saveDefaultConfig();
        if(this.getConfig().getString("token").isEmpty()) {
            getLogger().info("Requesting a token from the server. If this takes over 10 seconds, something went wrong. Please report it to AwesomestGamer.");
            String token = null;
            try {
                token = Antenna.getInstance(SpigotUtils.getInstance()).requestToken();
                TokenStorage.setToken(token);
                getConfig().set("token", token);
                saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getLogger().info("Received token " + token + " from server. Continuing boot process.");
        } else {
            TokenStorage.setToken(getConfig().getString("token"));
        }
        try {
            Antenna.getInstance(SpigotUtils.getInstance()).ping();
            Antenna.getInstance(BungeeUtils.getInstance()).passConnectMessage();
        }
        catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Antenna.getInstance(SpigotUtils.getInstance()).start();
        getCommand("token").setExecutor(new CommandListener());
    }
    @Override
    public void onDisable() {
        try {
            Antenna.getInstance(SpigotUtils.getInstance()).passDisconnectMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
