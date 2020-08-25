package io.github.aphelia.hermes.bungee;

import io.github.aphelia.hermes.common.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class BungeeUtils implements Utils {
    static BungeeUtils instance = null;
    public static BungeeUtils getInstance() {
        if(instance == null) instance = new BungeeUtils();
        return instance;
    }
    @Override
    public void sendMessage(String string) {
        ProxyServer.getInstance().broadcast(new TextComponent(string));
    }
    @Override
    public void log(Level level, String string) {
        ProxyServer.getInstance().getLogger().log(level, string);
    }

    @Override
    public String getFormat() {
        return ChatColor.translateAlternateColorCodes('&', BungeePlugin.plugin.getConfig().getString("format"));
    }
}
