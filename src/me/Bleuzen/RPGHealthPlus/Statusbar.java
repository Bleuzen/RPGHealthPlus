package me.Bleuzen.RPGHealthPlus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Statusbar implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        if (label.equalsIgnoreCase("hp")) {
            if (!sender.hasPermission("rpghealth.hp")) {
                sender.sendMessage(Messages.get("no-permission"));
            } else {
                Player tp = null;

                if (args.length == 0) {
                    if (sender instanceof Player) {
                        tp = (Player) sender;
                    }
                } else {
                    if (sender.hasPermission("rpghealth.hp.others")) {
                        tp = Bukkit.getPlayer(args[0]);
                    } else {
                        sender.sendMessage(Messages.get("no-permission"));
                        return false;
                    }
                }

                if (tp != null) {
                    UUID u = tp.getUniqueId();

                    int playerhp = Main.getInstance().getplayers().getInt(u + ".hp");

                    int getmaxhp = Main.getInstance().getGroupsMaxHP(tp);

                    if (playerhp >= getmaxhp) {

                        sender.sendMessage(Messages.get("statusbar-maximum"));

                    } else {

                        int percent = (int) (Main.getInstance().getplayers().getDouble(
                                u + ".xp")
                                / Main.getInstance().getplayers().getDouble(
                                u + ".needed") * 100.0D);
                        String display;
                        try {
                            display = Main.getInstance().getplayers().getString(u + ".xp").replace(".0", "");
                        } catch (NullPointerException e) {
                            return false;
                        }


                        int red = (int) (percent / 2.5);
                        if (red > 40) {
                            red = 40;
                        }
                        int gray = 40 - red;

                        StringBuilder xpBar = new StringBuilder();

                        if (red > 0) {
                            xpBar.append(ChatColor.RED + "" + ChatColor.BOLD);
                            for (int i = 0; i < red; i++) {
                                xpBar.append("▍");
                            }
                        }

                        if (gray > 0) {
                            xpBar.append(ChatColor.GRAY + "" + ChatColor.BOLD);
                            for (int i = 0; i < gray; i++) {
                                xpBar.append("▍");
                            }
                        }

                        sender.sendMessage(ChatColor.RED
                                + "       ➠   "
                                + ChatColor.BOLD
                                + display
                                + " / "
                                + Main.getInstance().getplayers().getInt(u + ".needed") + "  "
                                + percent + "%" + ChatColor.RED);

                        sender.sendMessage(xpBar.toString());

                    }
                } else {
                    sender.sendMessage(Messages.get("invalid-player"));
                }

            }
        }
        return false;
    }
}