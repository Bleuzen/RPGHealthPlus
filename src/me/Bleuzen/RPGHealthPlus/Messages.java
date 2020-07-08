package me.Bleuzen.RPGHealthPlus;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Messages {
    private static File file = new File(Main.getInstance().getDataFolder().getPath(), "messages.yml");
    private static FileConfiguration mcfg;

    public static String get(String path) {
        return ChatColor.translateAlternateColorCodes('&', mcfg.getString(path));
    }

    public static void reload() {
        mcfg = YamlConfiguration.loadConfiguration(file);

        mcfg.addDefault("update-available", "&bUpdate is available!");
        mcfg.addDefault("configuration-reloaded", "&a&lConfiguration reloaded!");
        mcfg.addDefault("gethp-command-hearts", "Hearts");
        mcfg.addDefault("invalid-player", "&c&lError: &cInvalid player!");
        mcfg.addDefault("no-permission", "&c&lError: &cNo permission!");
        mcfg.addDefault("invalid-arguments", "&c&lError: &cInvalid arguments!");
        mcfg.addDefault("help-usage", "&c&lUsage:");
        mcfg.addDefault("help-player", "player");
        mcfg.addDefault("help-number", "number");
        mcfg.addDefault("finished", "&c&lFinished!");
        mcfg.addDefault("statusbar-maximum", "&4&lMaximum");
        mcfg.addDefault("players-hp", "&c&l%s's HP:");
        mcfg.addDefault("scaled-to", "scaled to");
        mcfg.addDefault("hp-levelled-up", "&c&lHP levelled up");
        mcfg.addDefault("hp-and-melee-damage-levelled-up", "&c&lHP and melee damage levelled up");
        mcfg.addDefault("help-command-reload", "Reloads the configuration file");
        mcfg.addDefault("help-command-gethp", "Shows the HP of the targeted player");
        mcfg.addDefault("help-command-addhp", "Adds HP to the targeted player");
        mcfg.addDefault("help-command-sethp", "Sets the HP of the targeted player");
        mcfg.addDefault("help-command-tmphp", "Sets HP temporary");
        mcfg.addDefault("help-command-addxp", "Adds XP to the targeted player");
        mcfg.addDefault("help-command-version", "Shows the version of the plugin");

        mcfg.options().copyDefaults(true);

        try {
            mcfg.save(file);
        } catch (IOException e) {
            Main.getInstance().getLogger().log(Level.SEVERE, "Could not save messages to " + file.getAbsolutePath(), e);
        }
    }

}
