package io.github.aphelia.hermes.spigot;

import io.github.aphelia.hermes.common.Antenna;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;


public class EventWatcher implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event){
        if(event.isCancelled()) return;
        try {
            Antenna.getInstance(SpigotUtils.getInstance()).passChatMessage(event.getPlayer().getName(), event.getMessage());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        try {
            Antenna.getInstance(SpigotUtils.getInstance()).passJoinMessage(event.getPlayer().getName(), Bukkit.getServer().getOnlinePlayers().size(), Bukkit.getServer().getMaxPlayers());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        try {
            Antenna.getInstance(SpigotUtils.getInstance()).passLeaveMessage(event.getPlayer().getName(), Bukkit.getServer().getOnlinePlayers().size() - 1, Bukkit.getServer().getMaxPlayers());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        try {
            Antenna.getInstance(SpigotUtils.getInstance()).passDeathMessage(event.getEntity().getName());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
