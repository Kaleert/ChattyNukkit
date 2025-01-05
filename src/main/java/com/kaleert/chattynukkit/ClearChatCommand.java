package com.kaleert.chattynukkit;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;

public class ClearChatCommand extends Command {
    private final ChattyNukkit plugin;

    public ClearChatCommand(ChattyNukkit plugin) {
        super("clearchat", "Clear chat for all players", "/clearchat", new String[]{"cc"});
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("chatty.clearchat")) {
            sender.sendMessage(plugin.getMessage("no_permission"));
            return true;
        }

        for (Player player : plugin.getServer().getOnlinePlayers().values()) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage("");
            }
        }

        sender.sendMessage(plugin.getMessage("chat_cleared"));
        return true;
    }
}
