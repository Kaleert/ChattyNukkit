package com.kaleert.chattynukkit;

import cn.nukkit.Player;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Config;

import java.util.*;

public class NotificationManager {

    private final ChattyNukkit plugin;
    private final Map<String, List<String>> chatMessages = new HashMap<>();
    private final Map<String, List<String>> titleMessages = new HashMap<>();
    private final List<String> actionBarMessages = new ArrayList<>();
    private final Map<String, Integer> chatCounters = new HashMap<>();
    private final Map<String, Integer> titleCounters = new HashMap<>();
    private int actionBarCounter = 0;

    private TaskHandler chatTask;
    private TaskHandler titleTask;
    private TaskHandler actionBarTask;

    public NotificationManager(ChattyNukkit plugin) {
        this.plugin = plugin;
        loadNotifications();
    }

    public void loadNotifications() {
        Config config = plugin.getConfig();

        if (config.getBoolean("notifications.chat.enable", false)) {
            for (String list : config.getSection("notifications.chat.lists").getKeys(false)) {
                List<String> messages = new ArrayList<>();
                for (String messageKey : config.getStringList("notifications.chat.lists." + list + ".messages")) {
                    messages.add(plugin.getMessage(messageKey));
                }
                chatMessages.put(list, messages);
                chatCounters.put(list, 0);
            }
        }

        if (config.getBoolean("notifications.title.enable", false)) {
            for (String list : config.getSection("notifications.title.lists").getKeys(false)) {
                List<String> messages = new ArrayList<>();
                for (String messageKey : config.getStringList("notifications.title.lists." + list + ".messages")) {
                    messages.add(plugin.getMessage(messageKey));
                }
                titleMessages.put(list, messages);
                titleCounters.put(list, 0);
            }
        }

        if (config.getBoolean("notifications.actionbar.enable", false)) {
            for (String messageKey : config.getStringList("notifications.actionbar.messages")) {
                actionBarMessages.add(plugin.getMessage(messageKey));
            }
        }
    }

    public void start() {
        Config config = plugin.getConfig();

        if (config.getBoolean("notifications.chat.enable", false)) {
            int chatInterval = config.getInt("notifications.chat.lists.default.time", 60) * 20;
            chatTask = plugin.getServer().getScheduler().scheduleRepeatingTask(plugin, new NukkitRunnable() {
                @Override
                public void run() {
                    sendChatNotifications();
                }
            }, chatInterval);
        }

        if (config.getBoolean("notifications.title.enable", false)) {
            int titleInterval = config.getInt("notifications.title.lists.default.time", 60) * 20;
            titleTask = plugin.getServer().getScheduler().scheduleRepeatingTask(plugin, new NukkitRunnable() {
                @Override
                public void run() {
                    sendTitleNotifications();
                }
            }, titleInterval);
        }

        if (config.getBoolean("notifications.actionbar.enable", false)) {
            int actionBarInterval = config.getInt("notifications.actionbar.time", 60) * 20;
            actionBarTask = plugin.getServer().getScheduler().scheduleRepeatingTask(plugin, new NukkitRunnable() {
                @Override
                public void run() {
                    sendActionBarNotifications();
                }
            }, actionBarInterval);
        }
    }

    public void stop() {
        if (chatTask != null) chatTask.cancel();
        if (titleTask != null) titleTask.cancel();
        if (actionBarTask != null) actionBarTask.cancel();
    }

    private void sendChatNotifications() {
        for (Map.Entry<String, List<String>> entry : chatMessages.entrySet()) {
            String list = entry.getKey();
            List<String> messages = entry.getValue();

            if (messages.isEmpty()) continue;

            int index = chatCounters.get(list);
            String message = messages.get(index);

            for (Player player : plugin.getServer().getOnlinePlayers().values()) {
                if (plugin.getConfig().getBoolean("notifications.chat.lists." + list + ".permission", false) &&
                    !player.hasPermission("chatty.notification.chat." + list)) {
                    continue;
                }
                player.sendMessage(message);
            }

            index = (index + 1) % messages.size();
            chatCounters.put(list, index);
        }
    }

    private void sendTitleNotifications() {
        for (Map.Entry<String, List<String>> entry : titleMessages.entrySet()) {
            String list = entry.getKey();
            List<String> messages = entry.getValue();

            if (messages.isEmpty()) continue;

            int index = titleCounters.get(list);
            String[] parts = messages.get(index).split("\n", 2);
            String title = parts[0];
            String subtitle = parts.length > 1 ? parts[1] : "";

            for (Player player : plugin.getServer().getOnlinePlayers().values()) {
                if (plugin.getConfig().getBoolean("notifications.title.lists." + list + ".permission", false) &&
                    !player.hasPermission("chatty.notification.title." + list)) {
                    continue;
                }
                player.sendTitle(title, subtitle);
            }

            index = (index + 1) % messages.size();
            titleCounters.put(list, index);
        }
    }

    private void sendActionBarNotifications() {
        if (actionBarMessages.isEmpty()) return;

        String message = actionBarMessages.get(actionBarCounter);
        for (Player player : plugin.getServer().getOnlinePlayers().values()) {
            if (plugin.getConfig().getBoolean("notifications.actionbar.permission", false) &&
                !player.hasPermission("chatty.notification.actionbar")) {
                continue;
            }
            player.sendActionBar(message);
        }

        actionBarCounter = (actionBarCounter + 1) % actionBarMessages.size();
    }
}
