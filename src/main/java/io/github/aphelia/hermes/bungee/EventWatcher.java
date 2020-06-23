package io.github.aphelia.hermes.bungee;

import io.github.aphelia.hermes.common.Antenna;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.IOException;


public class EventWatcher implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChatEvent(ChatEvent event){
        if(event.getMessage().startsWith("/")) return;
        try {
            Antenna.getInstance(BungeeUtils.getInstance()).passChatMessage(((ProxiedPlayer)event.getSender()).getName(), event.getMessage());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PostLoginEvent event){
        try {
            Antenna.getInstance(BungeeUtils.getInstance()).passJoinMessage(event.getPlayer().getName());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(PlayerDisconnectEvent event){
        try {
            Antenna.getInstance(BungeeUtils.getInstance()).passLeaveMessage(event.getPlayer().getName());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}

