package io.github.aphelia.hermes.bungee;

import io.github.aphelia.hermes.common.TokenStorage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CommandListener extends Command {
    public CommandListener() {
        super("token");
    }
    @Override
    public void execute(CommandSender sender, String[] strings) {
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Command printed to console."));
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(ChatColor.AQUA + "Please run !connectMC " + TokenStorage.getToken()));
    }
}