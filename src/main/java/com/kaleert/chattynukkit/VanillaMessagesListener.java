package com.kaleert.chattynukkit;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.cacheddata.CachedMetaData;

public class VanillaMessagesListener implements Listener {

    private final ChattyNukkit plugin;
    private final LuckPerms luckPerms;

    public VanillaMessagesListener(ChattyNukkit plugin) {
        this.plugin = plugin;
        this.luckPerms = LuckPermsProvider.get();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean isFirstJoin = !player.hasPlayedBefore();

        if (isFirstJoin) {
            String firstJoinMessage = plugin.getConfig().getString("vanilla.join.first-join.message", "");
            sendJoinMessage(player, firstJoinMessage);
        } else if (plugin.getConfig().getBoolean("vanilla.join.enable", true)) {
            String joinMessage = plugin.getConfig().getString("vanilla.join.message", "");
            sendJoinMessage(player, joinMessage);
        }

        event.setJoinMessage((String) null);
    }

    private void sendJoinMessage(Player player, String message) {
        if (!message.isEmpty()) {
            String prefix = getPlayerPrefix(player);
            String suffix = getPlayerSuffix(player);

            message = message.replace("%prefix%", prefix)
                             .replace("%player%", player.getName())
                             .replace("%suffix%", suffix);
            player.getServer().broadcastMessage(message);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getConfig().getBoolean("vanilla.quit.enable", true)) {
            String quitMessage = plugin.getConfig().getString("vanilla.quit.message", "");
            if (!quitMessage.isEmpty()) {
                String prefix = getPlayerPrefix(player);
                String suffix = getPlayerSuffix(player);

                quitMessage = quitMessage.replace("%prefix%", prefix)
                                         .replace("%player%", player.getName())
                                         .replace("%suffix%", suffix);
                event.setQuitMessage(quitMessage);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (plugin.getConfig().getBoolean("vanilla.death.enable", true)) {
            String deathMessage;
            EntityDamageEvent lastDamageCause = player.getLastDamageCause();

            if (lastDamageCause instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) lastDamageCause;
                String killerName = damageEvent.getDamager().getName();
                deathMessage = plugin.getConfig().getString("vanilla.death.killed_by_message", "")
                                     .replace("%killer%", killerName);
            } else {
                deathMessage = plugin.getConfig().getString("vanilla.death.message", "");
            }

            if (!deathMessage.isEmpty()) {
                String prefix = getPlayerPrefix(player);
                String suffix = getPlayerSuffix(player);

                deathMessage = deathMessage.replace("%prefix%", prefix)
                                           .replace("%player%", player.getName())
                                           .replace("%suffix%", suffix);
                event.setDeathMessage(deathMessage);
            }
        }
    }

    private String getPlayerPrefix(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            CachedMetaData metaData = user.getCachedData().getMetaData();
            String prefix = metaData.getPrefix();
            return prefix != null ? prefix : "";
        }
        return "";
    }

    private String getPlayerSuffix(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            CachedMetaData metaData = user.getCachedData().getMetaData();
            String suffix = metaData.getSuffix();
            return suffix != null ? suffix : "";
        }
        return "";
    }
}
