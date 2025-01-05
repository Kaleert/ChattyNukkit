package com.kaleert.chattynukkit;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;
import java.util.Arrays;

public class PrivateMessageCommand extends Command {
    private final ChattyNukkit plugin;

    public PrivateMessageCommand(ChattyNukkit plugin) {
        super("msg", "Send a direct message to a player", "/msg <player> <message>");
        this.plugin = plugin;

        String[] aliases = plugin.getConfig().getStringList("commands.dm.aliases").toArray(new String[0]);
        this.setAliases(aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("players_only"));
            return true;
        }

        Player fromPlayer = (Player) sender;
        if (!fromPlayer.hasPermission("chatty.command.msg")) {
            fromPlayer.sendMessage(plugin.getMessage("no_permission"));
            return true;
        }

        if (args.length < 2) {
            fromPlayer.sendMessage(plugin.getMessage("usage_msg"));
            return false;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            fromPlayer.sendMessage(plugin.getMessage("player_not_found"));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        String dmFormat = plugin.getConfig().getString("dm.format", "§6[ЛС] §6§lОт %from% для %to%: §r%message%");
        String formattedMessage = dmFormat
                .replace("%from%", fromPlayer.getName())
                .replace("%to%", target.getName())
                .replace("%message%", message);

        target.sendMessage(formattedMessage);
        fromPlayer.sendMessage(formattedMessage);

        for (Player spy : plugin.getServer().getOnlinePlayers().values()) {
            if (ChattyNukkit.spyMode.contains(spy.getName())) {
                String spyDMMessage = plugin.getConfig().getString("spy.dmformat", "%chat_prefix% §6§ldm: от %from% для %to%§6: §e%message%")
                        .replace("%chat_prefix%", plugin.getConfig().getString("spy.prefix", "§1(§b§lSpy§1)"))
                        .replace("%from%", fromPlayer.getName())
                        .replace("%to%", target.getName())
                        .replace("%message%", message);
                spy.sendMessage(spyDMMessage);
            }
        }

        return true;
    }
}
