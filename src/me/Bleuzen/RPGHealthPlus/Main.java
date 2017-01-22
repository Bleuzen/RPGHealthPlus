package me.Bleuzen.RPGHealthPlus;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class Main extends JavaPlugin implements Listener {
	
  private static Main instance;
  
  //TODO: Update
  private static final String buildVersion = "v1_11_R1";
  static final String runningVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
  
  private List<Entity> monsterspawner;
  
  private boolean updateAvailable;
  private UpdateChecker updateChecker;
  
  private static boolean useGroupsPermissions = false;
 
  private boolean sounds;
  private boolean damageMultiplier;
  private boolean enableNamesXp;
  boolean scaleHP;
  private int xpPercentageFromMobspawners;
  
  private FileConfiguration players = null;
  private File Storage = null;
  
  private static boolean savingAsync;
  
  FileConfiguration cfg;
  
  boolean useHolographicDisplays;
  
  public void setXP(Player p, double xp) {
	  getplayers().set(p.getUniqueId() + ".xp", Utils.formatDouble(xp));
  }
  
  public void setNeeded(Player p, double needed) {
	  getplayers().set(p.getUniqueId() + ".needed", Utils.toInt(needed));
  }
  
  public void onDisable() {
	  // wait for save thread
	waitSave();
	  // save secure in main thread
	saveplayers();
	
    Bukkit.getConsoleSender().sendMessage("[" + getDescription().getName() + "] has been disabled!");
  }
  
  public void onEnable() {
	  	if(buildVersion.equals(runningVersion)) {
			if (getplayers().contains("player-storage")) {
				Bukkit.getConsoleSender().sendMessage("[" + getDescription().getName() + "] §cYou are using an old database version. You can update/convert your 'players.yml' using this application: http://goo.gl/oZSSg8");
				setEnabled(false);
			} else {
				
			  	instance = this;
				  
			    getCommand("rpghp").setExecutor(new Commands());
			    getCommand("hp").setExecutor(new Statusbar());
			    
			    getServer().getPluginManager().registerEvents(this, this);
			    
			    useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
			    
			    cfgReload();
			    saveConfig();
			    
			    try {
			        MetricsLite metrics = new MetricsLite(this);
			        metrics.start();
			    } catch (IOException e) {}
				
			}
	  	} else {
			Bukkit.getConsoleSender().sendMessage("[" + getDescription().getName() + "] §cThis version only works on server version " + buildVersion + ". You are running " + runningVersion + ".");
			setEnabled(false);
	  	}
  }
  
	public static Main getInstance() {
		return instance;
	}
  
  public void updatehp(Player p, boolean heal) {
    if(!cfg.getList("nonaffected-worlds").contains(p.getWorld().getName())) {
    	double gethp = getplayers().getInt(p.getUniqueId() + ".hp");
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(gethp);
        if(scaleHP) {
        	p.setHealthScaled(true);
        	int tmp = (int) (gethp * Double.parseDouble(cfg.getString("configuration.hp-display-scale").replace(",", ".")));
        	double scaleTo = (double) tmp;
        	p.setHealthScale(scaleTo);
        }
    } else {
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        if(scaleHP) {
        	p.setHealthScaled(false);
        }
    }
    if(heal) {
    	p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }
  }
  
  @EventHandler
  public void SpawnCreature(CreatureSpawnEvent e) {
    if(xpPercentageFromMobspawners != 100 && e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {	
        this.monsterspawner.add(e.getEntity());
    }
  }

  @EventHandler
  public void Death(PlayerDeathEvent e) {
	Player p = e.getEntity().getPlayer();
	
	if(!cfg.getList("nonaffected-worlds").contains(p.getWorld().getName()) && getplayers().contains(""+ p.getUniqueId())) {
	    if(cfg.getBoolean("configuration.reset-hp-on-death")) {
	    	register(p, false);
	    } else {
	    	
	    	double set = getplayers().getDouble(e.getEntity().getPlayer().getUniqueId() + ".xp") - (Utils.evaluate(cfg.getString("configuration.xp-lost-on-death-formula").toLowerCase().replace(",", ".").replace("hp", getplayers().getString(e.getEntity().getPlayer().getUniqueId() + ".hp"))));

	        if(set < 0) {
	        	set = 0;
	        }
	        
	        setXP(p, set);
	        saveplayersAsync(null, false);
	    }
	}
    
  }
  
  private void register(Player p, boolean heal) {
      getplayers().set(p.getUniqueId() + ".hp", cfg.getInt("configuration.starting-hp"));
      setXP(p, 0);
      setNeeded(p, Utils.evaluate(cfg.getString("configuration.needed-xp-formula").toLowerCase().replace(",", ".").replace("hp", getplayers().getString(p.getUniqueId() + ".hp"))));
      saveplayersAsync(p, heal);
  }
  
  @EventHandler
  public void World(PlayerChangedWorldEvent e) {
	  if(getplayers().contains(""+e.getPlayer().getUniqueId())) {
		    updatehp(e.getPlayer(), cfg.getBoolean("configuration.heal-after-world-change"));
	  }
  }

  public void levelup(Player p, double mobxp) {
	double xp = getplayers().getDouble(p.getUniqueId() + ".xp");
	int needed = getplayers().getInt(p.getUniqueId() + ".needed");
	int hp = getplayers().getInt(p.getUniqueId() + ".hp");
    getplayers().set(p.getUniqueId() + ".hp", hp + 1);
    setXP(p, mobxp - (needed - xp));
    setNeeded(p, Utils.evaluate(cfg.getString("configuration.needed-xp-formula").toLowerCase().replace(",", ".").replace("hp", getplayers().getString(p.getUniqueId() + ".hp"))));
    
    // check for NOT tmphp
    if(hp == p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
    	saveplayersAsync(p, cfg.getBoolean("configuration.heal-after-level-up"));
    } else {
    	saveplayersAsync(null, false);
    }
    	
        if(sounds) {
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.5F);
        }
    	
    	/* Particles */
        if(cfg.getBoolean("configuration.levelup-particles")) {
        	Utils.showParticles(p);
        }
        
    p.sendMessage(ChatColor.RED +""+  ChatColor.BOLD + "➠ " + (damageMultiplier ? (p.hasPermission("rpghealth.damagemultiplier") ? Messages.get("hp-and-melee-damage-levelled-up") : Messages.get("hp-levelled-up")) : Messages.get("hp-levelled-up")) + "!");
  }

	public void expMessage(Location loc, double gained, double needed, double current) {
		String gd = Utils.formatDoubleToString(gained);
		String nd = Utils.formatDoubleToString(needed);
		String cd = Utils.formatDoubleToString(current);
		Utils.showHologram(loc, ChatColor.RED + "+ " + gd + " XP "
				+ ChatColor.GRAY + "[" + cd + "/" + nd + "]");
	}
  
  @EventHandler
  public void onKill(EntityDeathEvent e) {
	  
  	boolean fromSpawner = monsterspawner.contains(e.getEntity());
  	
  	if(fromSpawner) {
    	monsterspawner.remove(e.getEntity());
  	}
	  
	  if(e.getEntity().getKiller() instanceof Player) {
		  Player p = e.getEntity().getKiller();
		  
		  if (!cfg.getList("nonaffected-worlds").contains(p.getWorld().getName())) {
			  
			  if(p.hasPermission("rpghealth.hp") && getplayers().contains(""+p.getUniqueId())) {
					
				  int level = p.getLevel();
				  
				  if(level >= cfg.getInt("configuration.minimum-level-to-earn-xp")) {
					  
						  boolean kill = true;
					      
							if (cfg.getBoolean("configuration.disable-in-creative")) {
								if (p.getGameMode() == GameMode.CREATIVE) {
									kill = false;
								}
							}
				      
				      if(kill) {
				
					            int gethp = getplayers().getInt(p.getUniqueId() + ".hp");
					            double getxp = getplayers().getDouble(p.getUniqueId() + ".xp");
					            double getneededxp = getplayers().getDouble(p.getUniqueId() + ".needed");
					            double getmobxp;
					            try {
					            	getmobxp = Double.parseDouble(cfg.getString("mobs-xp." + e.getEntity().getType()).replace(",", "."));
					            	if(enableNamesXp) {
						            	for(String s : cfg.getStringList("names-xp")) {
						            		String[] b = s.split(" = ");
						            		if(b[0].equalsIgnoreCase(ChatColor.stripColor(e.getEntity().getCustomName()))) {
						            			getmobxp += Double.parseDouble(b[1]);
						            			break;
						            		}
						            	}
					            	}
					            } catch(Exception ex) {
					            	getmobxp = 0.0D;
					            }
					            
					            if(getmobxp == 0.0D) {
					            	return;
					            }
					            
					            if(cfg.getBoolean("configuration.multiply-gained-xp-with-levels")) {
					            	getmobxp *= level;
					            }
					            
					            int getmaxhp = getGroupsMaxHP(p);
					            
					            if (fromSpawner)
					            {
					              getmobxp = getmobxp * xpPercentageFromMobspawners / 100;
					            }
					            
					            if (gethp < getmaxhp)
					            {
					              if (getmobxp >= getneededxp - getxp)
					              {
					                levelup(p, getmobxp);
					              }
					              else {
					                setXP(p, Utils.formatDouble(getxp + getmobxp));
					                
					                if(sounds) {
					                	p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1.0F);
					                }
					                
										if (useHolographicDisplays && cfg.getBoolean("configuration.show-xp-holograms")) {
											double c = getxp + getmobxp;
											if(c < getneededxp) {
												expMessage(e.getEntity().getLocation().add(0, e.getEntity().getEyeHeight(), 0), getmobxp, getneededxp, c);		
											}	
										}
										
					              }
					            }
				          
				      }
				}
				  
			  }  
			  
		  }
		  
    }
  }
  
  public int getGroupsMaxHP(Player p) {
      int getmaxhp = cfg.getInt("configuration.max-hp");
      int getstartinghp = cfg.getInt("configuration.starting-hp");
      int gethp = (int) p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
      if(useGroupsPermissions) {
      	if(!p.hasPermission("rpghealth.lvl.max")) {
      		if(p.hasPermission("rpghealth.lvl.med")) {
          		getmaxhp = (getstartinghp + getmaxhp) / 2;
          	} else if(p.hasPermission("rpghealth.lvl.low")) {
          		int set = (int) ((getstartinghp + getmaxhp) / 2.5);
          		if(set > getstartinghp) {
                  	getmaxhp = set;		
          		} else {
                  	getmaxhp = gethp;
          		}
          	} else {
          		getmaxhp = getstartinghp;
          	}
      	}
      }
      return getmaxhp;
  }
  

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(damageMultiplier && e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			
			if(p.hasPermission("rpghealth.damagemultiplier")) {
				e.setDamage(e.getDamage() * ((getplayers().getDouble(p.getUniqueId() + ".hp") / cfg.getDouble("configuration.starting-hp")) * Double.parseDouble(cfg.getString("configuration.melee-damage-multiplier").replace(",", "."))));
			}
		
		}
	}


  public void reloadplayers() {
    if (this.Storage == null) {
      this.Storage = new File(getDataFolder(), "players.yml");
    }
    this.players = YamlConfiguration.loadConfiguration(this.Storage);
  }

  public FileConfiguration getplayers() {
    if (this.players == null) {
      reloadplayers();
    }
    return this.players;
  }

	public void saveplayers() {
			if ((this.players == null) || (this.Storage == null))
				return;
			try {
				getplayers().save(this.Storage);
			} catch (IOException ex) {
				getLogger().log(Level.SEVERE, "Could not save to " + this.Storage, ex);
			}
	}
  
  public void saveplayersAsync(final Player up, final boolean heal) {
  	if(!savingAsync) {
			  savingAsync = true;
			  Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
					@Override
					public void run() {
						saveplayers();
						if(up != null) {
							updatehp(up, heal);
						}
						savingAsync = false;
					}
				});
  	}
  }
  
  
  
	public void cfgReload() {
		monsterspawner = new ArrayList<Entity>();
		
		reloadConfig();
		cfg = getConfig();

		boolean firstStart = cfg.options().header() == null;
		
		cfg.options().copyDefaults(true);
		cfg.options().header(getDescription().getFullName());
		
	    Messages.reload();
		
		useGroupsPermissions = cfg.getBoolean("configuration.use-groups-permissions");
		sounds = cfg.getBoolean("configuration.enable-sounds");
		damageMultiplier = cfg.getBoolean("configuration.use-hp-based-damage-multiplier");
		enableNamesXp = cfg.getBoolean("configuration.enable-names-xp");
		scaleHP = cfg.getDouble("configuration.hp-display-scale") != 1.0;
		xpPercentageFromMobspawners = cfg.getInt("configuration.xp-percentage-from-mobspawners");
		
        if(Utils.evaluate(cfg.getString("configuration.needed-xp-formula").toLowerCase().replace(",", ".").replace("hp", "20")) <= 0) {
        	getLogger().log(Level.WARNING, "'configuration.needed-xp-formula' is less than or equal to 0.");
        }
		
		if(!firstStart && cfg.getBoolean("configuration.check-for-updates")) {
			checkForUpdates();
		    if(updateAvailable) {
		    	Bukkit.getConsoleSender().sendMessage("[" + getDescription().getName() + "] §3" + Messages.get("update-available"));
		    }
		}
	}
  
  
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
	  final Player p = e.getPlayer();
	  
	    if(!getplayers().contains(""+p.getUniqueId())) {
	    	if(p.hasPermission("rpghealth.hp")) {
	        	register(p, true);
	    	}
	    } else {
	    	updatehp(p, false);
	    }
	  
	  if(p.isOp() && updateAvailable) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					p.sendMessage("§b===== §3" + getDescription().getName() + "§b " + Messages.get("update-available")
							+ " =====");
				}
			}, 20);
	  }
  }
  
  
  void waitSave() {
	  while(savingAsync) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	  }
  }
  
	private void checkForUpdates() {
		if(!updateAvailable) {
			if(updateChecker == null) {
				updateChecker = new UpdateChecker();
			}
			updateAvailable = updateChecker.updateAvailable();	
		}
	}
  
}