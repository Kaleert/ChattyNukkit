package com.kaleert.chattynukkit;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

public class ChattyNukkit extends PluginBase {

    public NotificationManager notificationManager;
    static final Set<String> spyMode = new HashSet<>();
    private Config languageConfig;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        File localeDir = new File(getDataFolder(), "locale");
        if (!localeDir.exists()) {
            localeDir.mkdirs();
        }

        reloadLanguage();

        notificationManager = new NotificationManager(this);
        notificationManager.start();

        ChatListener chatListener = new ChatListener(this);
        this.getServer().getPluginManager().registerEvents(chatListener, this);

        VanillaMessagesListener vanillaMessagesListener = new VanillaMessagesListener(this);
        this.getServer().getPluginManager().registerEvents(vanillaMessagesListener, this);

        this.getServer().getCommandMap().register("chatty", new ChattyCommand(this));

        if (getConfig().getBoolean("commands.spy.enable", true)) {
            this.getServer().getCommandMap().register("spy", new SpyCommand(this));
        }

        if (getConfig().getBoolean("commands.clearchat.enable", true)) {
            this.getServer().getCommandMap().register("clearchat", new ClearChatCommand(this));
        }

        if (getConfig().getBoolean("commands.dm.enable", true)) {
            this.getServer().getCommandMap().register("dm", new PrivateMessageCommand(this));
        }

        if (getConfig().getBoolean("commands.chatignore.enable", true)) {
            this.getServer().getCommandMap().register("chatignore", new ToggleNotificationCommand(this, chatListener));
        }

        this.getLogger().info(getMessage("enabled"));
    }

    @Override
    public void onDisable() {
        if (notificationManager != null) notificationManager.stop();
        this.getLogger().info(getMessage("disabled"));
    }

    public void reloadLanguage() {
        String lang = getConfig().getString("language", "eng");
        loadLanguage(lang);
    }

    private void loadLanguage(String lang) {
        File langFile = new File(getDataFolder(), "locale/" + lang + ".yml");

        if (!langFile.exists()) {
            try (InputStream in = getResource("locale/" + lang + ".yml")) {
                if (in != null) {
                    Files.copy(in, langFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    getLogger().warning("Файл локализации для " + lang + " не найден в ресурсах!");
                }
            } catch (Exception e) {
                getLogger().warning("Не удалось загрузить файл локализации: " + e.getMessage());
            }
        }

        languageConfig = new Config(langFile, Config.YAML);
    }

    public String getMessage(String key) {
        return languageConfig.getString("messages." + key, key);
    }

    public String resolveGroupFormat(String group) {
        Object groupFormat = getConfig().get("groups." + group);

        if (groupFormat == null) {
            return getConfig().getString("groups.default");
        }

        if (groupFormat instanceof String && getConfig().get("groups." + groupFormat) != null) {
            return resolveGroupFormat((String) groupFormat);
        }

        return (String) groupFormat;
    }
}
