package com.kaleert.chattynukkit;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.Set;
import java.util.UUID;

public class ToggleNotificationCommand extends Command {

    private final ChatListener chatListener;
    private final ChattyNukkit plugin;

    public ToggleNotificationCommand(ChattyNukkit plugin, ChatListener chatListener) {
        super("chatignore", "Ignore messages from a specific chat", "/chatignore <chat>");
        this.plugin = plugin;
        this.chatListener = chatListener;
        
        String[] aliases = plugin.getConfig().getStringList("commands.chatignore.aliases").toArray(new String[0]);
        this.setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("players_only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("usage_chatignore"));
            return true;
        }

        String chatType = args[0];
        chatListener.toggleChatIgnore(player, chatType);  // Использование метода для управления игнором

        return true;
    }
}
