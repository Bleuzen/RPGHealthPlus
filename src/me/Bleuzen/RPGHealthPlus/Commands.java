package me.Bleuzen.RPGHealthPlus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	  if(label.equalsIgnoreCase("rpghp")) {
		  if(args.length == 0) {
			  
			  sender.sendMessage("§3---------------------------------------------");
			  
			  sender.sendMessage("§6/" + label + " reload §e" + Messages.get("help-command-reload"));
			  sender.sendMessage("§6/" + label + " gethp §e" + Messages.get("help-command-gethp"));
			  sender.sendMessage("§6/" + label + " addhp §e" + Messages.get("help-command-addhp"));
			  sender.sendMessage("§6/" + label + " sethp §e" + Messages.get("help-command-sethp"));
			  sender.sendMessage("§6/" + label + " tmphp §e" + Messages.get("help-command-tmphp"));
			  sender.sendMessage("§6/" + label + " addxp §e" + Messages.get("help-command-addxp"));
			  sender.sendMessage("§6/" + label + " version §e" + Messages.get("help-command-version"));
			  
			  sender.sendMessage("§3---------------------------------------------");
			  
		  } else {	
			  
			  
			    if (args[0].equalsIgnoreCase("reload")) {
			    	if ((sender.hasPermission("rpghealth.reload")) || (sender.isOp())) {
			    		Main.getInstance().waitSave();
			    		Main.getInstance().saveplayers();
				        Main.getInstance().cfgReload();
				        sender.sendMessage(ChatColor.GREEN +""+ ChatColor.BOLD + Messages.get("configuration-reloaded"));
			    	} else {
				        sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("no-permission"));	
			    	} 	
			      } else
			    if (args[0].equalsIgnoreCase("gethp"))
			      {
			        if ((sender.hasPermission("rpghealth.gethp")) || (sender.isOp()))
			        {
			          if (args.length > 1)
			          {
			        	Player p = Bukkit.getPlayer(args[1]);
			            if (p != null)
			            {
			            	if(!Main.getInstance().getplayers().contains(""+ p.getUniqueId())) {
			            		sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-player") + "!");
			            		return false;
			            	}
			            	int hp = Main.getInstance().getplayers().getInt(p.getUniqueId() + ".hp");
			            	sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + p.getName() + Messages.get("players-hp") + " ➠" + ChatColor.RED + " " + hp + " (" + String.valueOf((Double.valueOf(hp) / 2)).replace(".0", "") + (Main.getInstance().scaleHP ? " (" + Messages.get("scaled-to") + " " + String.valueOf((p.getHealthScale() / 2)).replace(".0", "") + ") " : " ") + Messages.get("gethp-command-hearts") + ")");
			            }
			            else {
				          sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-player") + "!");
			            }
			          }
			          else
			          {
			            sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
			            sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("usage") + " ➠" + ChatColor.RED + "/" + label + " gethp <" + Messages.get("help-player") + ">");
			          }
			        }
			        else
			          sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("no-permission"));
			      }
			      else if (args[0].equalsIgnoreCase("addhp"))
			      {
			        if ((sender.hasPermission("rpghealth.addhp")) || (sender.isOp()))
			        {
			          if (args.length > 2)
			          {
			        	Player p = Bukkit.getPlayer(args[1]);
			            if (p != null)
			            {
			            	if(!Main.getInstance().getplayers().contains(""+ p.getUniqueId())) {
			            		sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-player") + "!");
			            		return false;
			            	}
			              try
			              {
			            	int set = Main.getInstance().getplayers().getInt(p.getUniqueId() + ".hp") + Integer.valueOf(args[2]);
			                int starting = Main.getInstance().cfg.getInt("configuration.starting-hp");
			                int maxhp = Main.getInstance().getGroupsMaxHP(p);
			                
			                if(set < starting) {
			                	set = starting;
			                }
			                  
			                if(set > maxhp) {
			              	  set = maxhp;
			                }
			                
			                Main.getInstance().getplayers().set(p.getUniqueId() + ".hp", set);
			                Main.getInstance().setNeeded(p, Utils.evaluate(Main.getInstance().cfg.getString("configuration.needed-xp-formula").toLowerCase().replace(",", ".").replace("hp", String.valueOf(set))));
			                Main.getInstance().updatehp(p, false);
			                if(sender instanceof Player) {
			                	sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "➠ " + Messages.get("finished") + "!");
			                }
			              }
			              catch (NumberFormatException x) {
			                  sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
			              }
			            }
			            else
			              sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-player") + "!");
			          }
			          else
			          {
			            sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
			            sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("usage") + " ➠" + ChatColor.RED + "/" + label + " addhp <" + Messages.get("help-player") + "> <" + Messages.get("help-number") + ">");
			          }
			        }
			        else {
			          sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("no-permission"));
			        }
			      }
			      else if (args[0].equalsIgnoreCase("sethp"))
			      {
			        if ((sender.hasPermission("rpghealth.sethp")) || (sender.isOp()))
			        {
			          if (args.length > 2)
			          {
			        	Player p = Bukkit.getPlayer(args[1]);
			            if (p != null)
			            {
			            	if(!Main.getInstance().getplayers().contains(""+ p.getUniqueId())) {
			            		sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-player") + "!");
			            		return false;
			            	}
			              try
			              {
			                int set = Integer.valueOf(args[2]);
			                int groupMax = Main.getInstance().getGroupsMaxHP(p);
			                int starting = Main.getInstance().cfg.getInt("configuration.starting-hp");
			                if(set > groupMax) {
			                	set = groupMax;
			                }
			                if(set < starting) {
			                	set = starting;
			                }
			                Main.getInstance().getplayers().set(p.getUniqueId() + ".hp", set);
			                Main.getInstance().setNeeded(p, Utils.evaluate(Main.getInstance().cfg.getString("configuration.needed-xp-formula").toLowerCase().replace(",", ".").replace("hp", String.valueOf(set))));
			                Main.getInstance().setXP(p, 0);
			                Main.getInstance().updatehp(p, false);
			                if(sender instanceof Player) {
				                sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "➠ " + Messages.get("finished") + "!");	
			                }
			              }
			              catch (NumberFormatException x) {
			                  sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
			              }
			            }
			            else
			              sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-player") + "!");
			          }
			          else
			          {
			            sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
			            sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("usage") + " ➠" + ChatColor.RED + "/" + label + " sethp <" + Messages.get("help-player") + "> <" + Messages.get("help-number") + ">");
			          }
			        }
			        else {
			          sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("no-permission"));
			        }
			      }
			      else  
			      if (args[0].equalsIgnoreCase("addxp")) {
			      	
	                  if ((sender.hasPermission("rpghealth.addxp")) || (sender.isOp())) {
				          if (args.length > 2) {
				        	  Player p = Bukkit.getPlayer(args[1]);
				              if(p != null) {
					            	if(!Main.getInstance().getplayers().contains(""+ p.getUniqueId())) {
					            		sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-player") + "!");
					            		return false;
					            	}
				                      try
				                      {
				                    	int groupsMax = Main.getInstance().getGroupsMaxHP(p);
				                    	int hp = Main.getInstance().getplayers().getInt(p.getUniqueId() + ".hp");
				                    	
				                    	if(hp < groupsMax) {
				                    		double toAdd = Double.parseDouble(args[2].replace(",", "."));
					                    	double set = Main.getInstance().getplayers().getDouble(p.getUniqueId() + ".xp") + toAdd;
					                    	double needed = Main.getInstance().getplayers().getDouble(p.getUniqueId() + ".needed");
					                    	
					                    	if(set < 0) {
					                    		set = 0;
					                    	}
					                    	
					                    	if(set >= needed) {
					                    		Main.getInstance().levelup(p, toAdd);
					                    	} else {
						                        Main.getInstance().setXP(p, set);	
					                    	}
				                    	}
				                    	
				                        if(sender instanceof Player) {
					                        sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "➠ " + Messages.get("finished") + "!");	
				                        }
				                      }
				                      catch (NumberFormatException x) {
				                        sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
				                      }
				                  	
				              } else {
				              	sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-player") + "!");
				              }
				          } else {
				              sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
				              sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("usage") + " ➠" + ChatColor.RED + "/" + label + " addxp <" + Messages.get("help-player") + "> <" + Messages.get("help-number") + ">");
				          }  
	                  } else {
	                	  sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("no-permission"));
	                  }
			      	
			      } else
			     if (args[0].equalsIgnoreCase("tmphp")) {
				        if ((sender.hasPermission("rpghealth.tmphp")) || (sender.isOp()))
				        {
				          if (args.length > 2)
				          {
				        	Player p = Bukkit.getPlayer(args[1]);
				            if (p != null)
				            {
				              try
				              {
				                int hp = Integer.valueOf(args[2]);
				                if(hp <= 0) {
				                	sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
				                	return false;
				                }
				                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
				                if(sender instanceof Player) {
					                sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "➠ " + Messages.get("finished") + "!");	
				                }
				              }
				              catch (NumberFormatException x) {
				                  sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
				              }
				            }
				            else
				              sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-player") + "!");
				          }
				          else
				          {
				            sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("invalid-arguments") + "!");
				            sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("usage") + " ➠" + ChatColor.RED + "/" + label + " tmphp <" + Messages.get("help-player") + "> <" + Messages.get("help-number") + ">");
				          }
				        }
				        else {
				          sender.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + Messages.get("error") + " ➠" + ChatColor.RED + " " + Messages.get("no-permission"));
				        }
			     } else
			    	  
			    if(args[0].equalsIgnoreCase("version")) {
			    	sender.sendMessage("§b" + Main.getInstance().getDescription().getFullName());
			    }
			    else {
			    	sender.sendMessage("§4§l" + Messages.get("invalid-arguments"));
			    }
			  
			  
			  
			  
			  
		  }	 
		  
	  }
	  
		  return false;
	}
}