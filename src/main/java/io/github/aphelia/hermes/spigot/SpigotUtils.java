package io.github.aphelia.hermes.spigot;

import io.github.aphelia.hermes.common.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public class SpigotUtils implements Utils {
    static SpigotUtils instance = null;
    public static SpigotUtils getInstance() {
        if(instance == null) instance = new SpigotUtils();
        return instance;
    }
    @Override
    public void sendMessage(String string) {
        Bukkit.getServer().broadcastMessage(string);
    }
    @Override
    public void log(Level level, String string) {
        Bukkit.getLogger().log(level, string);
    }
    @Override
    public String getFormat() {
        return ChatColor.translateAlternateColorCodes('&', SpigotPlugin.getPlugin(SpigotPlugin.class).getConfig().getString("format").replace('&', '§'));
    }
}
