
package me.Bleuzen.RPGHealthPlus;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {
	private static File file = new File(Main.getInstance().getDataFolder().getPath(), "messages.yml");
	private static FileConfiguration mcfg;

	public static String get(String path) {
		return mcfg.getString(path);
	}

	public static void reload() {
		mcfg = YamlConfiguration.loadConfiguration(file);

		mcfg.addDefault("update-available", "§bUpdate is available!");
		mcfg.addDefault("configuration-reloaded", "§a§lConfiguration reloaded!");
		mcfg.addDefault("gethp-command-hearts", "Hearts");
		mcfg.addDefault("invalid-player", "§c§lInvalid player!");

		//TODO
		mcfg.addDefault("no-permission", "No permission");
		mcfg.addDefault("invalid-arguments", "Invalid arguments");
		mcfg.addDefault("help-player", "player");
		mcfg.addDefault("help-number", "number");
		mcfg.addDefault("finished", "Finished");
		mcfg.addDefault("error", "ERROR");
		mcfg.addDefault("usage", "USAGE");
		mcfg.addDefault("statusbar-maximum", "Maximum");
		mcfg.addDefault("players-hp", "'s HP");
		mcfg.addDefault("scaled-to", "scaled to");
		mcfg.addDefault("hp-levelled-up", "HP levelled up");
		mcfg.addDefault("hp-and-melee-damage-levelled-up", "HP and melee damage levelled up");
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
