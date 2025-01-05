package com.kaleert.chattynukkit;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class ChattyCommand extends Command {

    private final ChattyNukkit plugin;

    public ChattyCommand(ChattyNukkit plugin) {
        super("chatty", "Chatty command", "/chatty", new String[]{});
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextFormat.YELLOW + plugin.getMessage("chatty_info"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("chatty.reload")) {
                sender.sendMessage(TextFormat.RED + plugin.getMessage("no_permission"));
                return true;
            }
            plugin.notificationManager.stop();
            plugin.reloadConfig();
            plugin.notificationManager.start();
            plugin.reloadLanguage();
            sender.sendMessage(TextFormat.GREEN + plugin.getMessage("config_reloaded"));
            return true;
        }

        sender.sendMessage(TextFormat.RED + plugin.getMessage("unknown_command"));
        return false;
    }
}
