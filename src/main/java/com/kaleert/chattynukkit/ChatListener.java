package com.kaleert.chattynukkit;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.utils.Config;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final ChattyNukkit plugin;
    private final LuckPerms luckPerms;
    private final Map<UUID, Set<String>> playerIgnoredChats = new HashMap<>();

    private static final Pattern IP_PATTERN = Pattern.compile(
        "\\b((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\b"
    );
    private static final Pattern URL_PATTERN = Pattern.compile(
        "(?i)\\b(https?:\\/\\/)?[\\w\\.а-яА-Я-]+\\.([a-z]{2,4}|[рР][фФ]|[уУ][кК][рР])\\b(:\\d{2,7})?(\\/\\S+)?"
    );

    public ChatListener(ChattyNukkit plugin) {
        this.plugin = plugin;
        this.luckPerms = LuckPermsProvider.get();
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String originalMessage = event.getMessage();

        if (plugin.getConfig().getBoolean("moderation.caps.enable", true) &&
                !hasModerationBypassPermission(player, "caps") &&
                originalMessage.length() >= plugin.getConfig().getInt("moderation.caps.length", 6)) {

            int uppercaseCount = (int) originalMessage.chars().filter(Character::isUpperCase).count();
            int upperCasePercent = (int) ((uppercaseCount / (double) originalMessage.length()) * 100);

            if (upperCasePercent >= plugin.getConfig().getInt("moderation.caps.percent", 80)) {
                if (plugin.getConfig().getBoolean("moderation.caps.block", true)) {
                    player.sendMessage(plugin.getMessage("caps_warning"));
                    event.setCancelled(true);
                    return;
                } else {
                    originalMessage = originalMessage.toLowerCase(); // Переводим сообщение в нижний регистр
                }
            }
        }

        if (plugin.getConfig().getBoolean("moderation.badwords.enable", true) &&
                !hasModerationBypassPermission(player, "badwords") &&
                containsBannedWords(originalMessage)) {
            player.sendMessage(plugin.getMessage("banned_words"));
            event.setCancelled(true);
            return;
        }

        if (plugin.getConfig().getBoolean("moderation.advertisement.enable", true) &&
                !hasModerationBypassPermission(player, "advertisement")) {
            if (containsLinks(originalMessage) || containsIPs(originalMessage)) {
                player.sendMessage(plugin.getMessage("advertising_blocked"));
                event.setCancelled(true);
                return;
            }
        }

        Config config = plugin.getConfig();
        String chatType = "local";
        String symbol = "";

        for (String key : config.getSection("chats").getKeys(false)) {
            symbol = config.getString("chats." + key + ".symbol", "");
            if (!symbol.isEmpty() && originalMessage.startsWith(symbol)) {
                chatType = key;
                originalMessage = originalMessage.substring(symbol.length());
                break;
            }
        }

        if (!config.getBoolean("chats." + chatType + ".enable", true)) {
            player.sendMessage(plugin.getMessage("command_disabled"));
            chatType = "local";
        }

        String permission = config.getString("chats." + chatType + ".permission", "chat." + chatType);
        if (!player.hasPermission(permission) && !player.hasPermission("chat.*")) {
            player.sendMessage(plugin.getMessage("no_permission"));
            event.setCancelled(true);
            return;
        }

        double chatRange = config.getDouble("chats." + chatType + ".range", -1);

        boolean enableSeePermission = plugin.getConfig().getBoolean("enable-see-permission", false);

        event.setCancelled(true);
        for (Player target : plugin.getServer().getOnlinePlayers().values()) {
            if ((enableSeePermission && !target.hasPermission("chat.see." + chatType) && !target.hasPermission("chat.see.*")) ||
                getIgnoredChats(target).contains(chatType)) {
                continue;
            }

            if (chatRange > 0 && player.distance(target) > chatRange) {
                continue;
            }

            String prefix = config.getString("chats." + chatType + ".prefix", "");
            String groupFormat = plugin.resolveGroupFormat(getPrimaryGroup(player));
            String formattedMessage = groupFormat.replace("%chat_prefix%", prefix)
                                                 .replace("%prefix%", getPlayerPrefix(player))
                                                 .replace("%player%", player.getName())
                                                 .replace("%message%", originalMessage);

            target.sendMessage(formattedMessage);
        }

        if (chatType.equals("local")) {
            String prefix = config.getString("chats." + chatType + ".prefix", "");
            String groupFormat = plugin.resolveGroupFormat(getPrimaryGroup(player));
            String formattedMessage = groupFormat.replace("%chat_prefix%", prefix)
                                                 .replace("%prefix%", getPlayerPrefix(player))
                                                 .replace("%player%", player.getName())
                                                 .replace("%message%", originalMessage);

            for (Player spy : plugin.getServer().getOnlinePlayers().values()) {
                if (ChattyNukkit.spyMode.contains(spy.getName())) {
                    String spyPrefix = plugin.getConfig().getString("spy.prefix", "§b[Шпион] ");
                    spy.sendMessage(spyPrefix + formattedMessage);
                }
            }
        }

        plugin.getLogger().info("[" + chatType.toUpperCase() + "] " + player.getName() + ": " + originalMessage);
    }

    private boolean containsBannedWords(String message) {
        for (String word : plugin.getConfig().getStringList("moderation.badwords.words")) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsLinks(String message) {
        for (String word : message.split("\\s+")) {
            if (URL_PATTERN.matcher(word).find()) {
                for (String domain : plugin.getConfig().getStringList("moderation.advertisement.whitelist")) {
                    if (word.contains(domain)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean containsIPs(String message) {
        for (String word : message.split("\\s+")) {
            if (IP_PATTERN.matcher(word).find()) {
                for (String ip : plugin.getConfig().getStringList("moderation.advertisement.whitelist")) {
                    if (word.equals(ip)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean hasModerationBypassPermission(Player player, String type) {
        return player.hasPermission("chatty.moderation.bypass." + type) || player.hasPermission("chatty.moderation.*");
    }

    public Set<String> getIgnoredChats(Player player) {
        return playerIgnoredChats.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    public void toggleChatIgnore(Player player, String chatType) {
        UUID playerUUID = player.getUniqueId();
        Set<String> ignoredChats = getIgnoredChats(player);

        if (ignoredChats.contains(chatType)) {
            ignoredChats.remove(chatType);
            player.sendMessage(plugin.getMessage("messages.chat_visible").replace("{chat}", chatType));
        } else {
            ignoredChats.add(chatType);
            player.sendMessage(plugin.getMessage("messages.chat_ignored").replace("{chat}", chatType));
        }

        playerIgnoredChats.put(playerUUID, ignoredChats);
    }

    private String getPrimaryGroup(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        return user != null ? user.getPrimaryGroup() : "default";
    }

    private String getPlayerPrefix(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        return user != null ? user.getCachedData().getMetaData().getPrefix() : "";
    }
}
