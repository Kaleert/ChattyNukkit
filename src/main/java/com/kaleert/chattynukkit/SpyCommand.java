package com.kaleert.chattynukkit;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;

public class SpyCommand extends Command {

    private final ChattyNukkit plugin;

    public SpyCommand(ChattyNukkit plugin) {
        super("spy", "Toggle spy mode", "/spy");
        this.plugin = plugin;

        String[] aliases = plugin.getConfig().getStringList("commands.spy.aliases").toArray(new String[0]);
        this.setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("players_only"));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("chatty.command.spy")) {
            player.sendMessage(plugin.getMessage("no_permission"));
            return true;
        }

        if (ChattyNukkit.spyMode.contains(player.getName())) {
            ChattyNukkit.spyMode.remove(player.getName());
            player.sendMessage(plugin.getMessage("spy_disabled"));
        } else {
            ChattyNukkit.spyMode.add(player.getName());
            player.sendMessage(plugin.getMessage("spy_enabled"));
        }

        return true;
    }
}
