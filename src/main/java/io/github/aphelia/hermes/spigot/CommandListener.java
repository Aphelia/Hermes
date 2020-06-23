package io.github.aphelia.hermes.spigot;

import io.github.aphelia.hermes.common.TokenStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "Command printed to console.");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "Please run !connectMC " + TokenStorage.getToken());
        return true;
    }
}
