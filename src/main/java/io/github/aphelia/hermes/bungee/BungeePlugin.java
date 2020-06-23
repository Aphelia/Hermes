package io.github.aphelia.hermes.bungee;

import io.github.aphelia.hermes.common.Antenna;
import io.github.aphelia.hermes.common.TokenStorage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BungeePlugin extends Plugin {
    public static BungeePlugin plugin;
    private Configuration config;
    @Override
    public void onEnable() {
        plugin = this;
        getProxy().getConsole().sendMessage(new TextComponent(ChatColor.AQUA + "\n" +
                "db   db d88888b d8888b. .88b  d88. d88888b .d8888. \n" +
                "88   88 88'     88  `8D 88'YbdP`88 88'     88'  YP \n" +
                "88ooo88 88ooooo 88oobY' 88  88  88 88ooooo `8bo.   \n" +
                "88~~~88 88~~~~~ 88`8b   88  88  88 88~~~~~   `Y8b. \n" +
                "88   88 88.     88 `88. 88  88  88 88.     db   8D \n" +
                "YP   YP Y88888P 88   YD YP  YP  YP Y88888P `8888Y' \n" +
                "\n" +
                "By Project Aphelia—BungeeCord edition"));
        getProxy().getPluginManager().registerListener(this, new EventWatcher());
        this.saveDefaultConfig();
        if(this.getConfig().getString("token").isEmpty()) {
            getLogger().info("Requesting a token from the server. If this takes over 10 seconds, something went wrong. Please report it to AwesomestGamer.");
            String token = null;
            try {
                token = Antenna.getInstance(BungeeUtils.getInstance()).requestToken();
                TokenStorage.setToken(token);
                getConfig().set("token", token);
                saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getLogger().info("Received token " + token + " from server. Continuing boot process.");
        } else {
            TokenStorage.setToken(this.getConfig().getString("token"));
        }
        try {
            Antenna.getInstance(BungeeUtils.getInstance()).ping();
        }
        catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Antenna.getInstance(BungeeUtils.getInstance()).start();
        getProxy().getPluginManager().registerCommand(this, new CommandListener());
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    Configuration getConfig() {
        saveDefaultConfig();
        if(config!=null) return config;
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Encountered IOException.");
        }
        return config;
    }
    void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Encountered IOException.");
        }
    }
}
