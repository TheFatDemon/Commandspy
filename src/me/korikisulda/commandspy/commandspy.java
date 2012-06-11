/*
<one line to give the program's name and a brief idea of what it does.>
    Copyright (C) 2012 korikisulda

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package me.korikisulda.commandspy;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;

public class commandspy extends JavaPlugin {
	// declarations
	public Logger log = Logger.getLogger("Minecraft");
	public WorldEdit worldedit;
	public statistics statistics = new statistics(this);
	public boolean sv_debug = false;
	public Map<String, String> spylist = new HashMap<String, String>();
	public Map<String, Boolean> weBlock = new HashMap<String, Boolean>();
	public List<String> debugUsers = Arrays.asList(new String[] {
			"korikisulda", "example2", "changethis" });
	public List<String> blacklistedcommands = Arrays.asList(new String[] {
			"/auth", "/register", "/login" });
	public int weBlockID;

	// //////////////////////////////////////////////////////
	private WorldEditPlugin getWorldEdit() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");

		// WorldEdit may not be loaded
		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			log.severe("[Commandspy] Could not load WorldEdit! disabling.");
			getPluginLoader().disablePlugin(this);
			return null;
		} else {
			return (WorldEditPlugin) plugin;
		}

	}

	// //////////////////////////////////////////////////////

	public void onDisable() {
		getConfig().createSection("users", spylist);
		saveConfig();
		getConfig().set("dontlog", blacklistedcommands);
		if (getConfig().getBoolean("useMySQL"))
			statistics.stopconnection();
	}

	public void onEnable() {
		// ///////////////////////////////////////////////////////
		if (!getConfig().isSet("users"))
			getConfig().createSection("users", spylist);
		for (String s : getConfig().getConfigurationSection("users").getKeys(
				false)) {
			spylist.put(s, getConfig().getConfigurationSection("users")
					.getString(s));
		}
		if (!getConfig().isSet("useMySQL"))
			getConfig().set("useMySQL", false);
		if (!getConfig().isSet("SQLhostname"))
			getConfig().set("SQLhostname", "");
		if (!getConfig().isSet("SQLusername"))
			getConfig().set("SQLusername", "");
		if (!getConfig().isSet("SQLpassword"))
			getConfig().set("SQLpassword", "12345");
		if (!getConfig().isSet("SQLdatabase"))
			getConfig().set("SQLdatabase", "");
		if (!getConfig().isSet("SQLport"))
			getConfig().set("SQLport", 3306);
		if (!getConfig().isSet("dontlog"))
			getConfig().set("dontlog", blacklistedcommands);
		blacklistedcommands = getConfig().getStringList("dontlog");

		if (!getConfig().isSet("debugUsers"))
			getConfig().set("debugUsers", debugUsers);
		debugUsers = getConfig().getStringList("debugUsers");
		if (!getConfig().isSet("weBlockID"))
			getConfig().set("weBlockID", 46);
		saveConfig();
		weBlockID = getConfig().getInt("weBlockID");
		// ///////////////////////////////////////////////////////
		if (getConfig().isSet("SQLhostname")
				&& getConfig().isSet("SQLusername")
				&& getConfig().isSet("SQLpassword")
				&& getConfig().getBoolean("useMySQL")
				&& getConfig().isSet("SQLdatabase")) {
			statistics.startconnection(getConfig().getString("SQLhostname"),
					getConfig().getInt("SQLport"),
					getConfig().getString("SQLusername"), getConfig()
							.getString("SQLpassword"),
					getConfig().getString("SQLdatabase"));
		}
		worldedit = getWorldEdit().getWorldEdit();
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new playerListener(this), this);
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		if (args.length == 0)
			args = new String[] { "help" };

		if (args[0].equalsIgnoreCase("set")) {
			// //////////////////////////////
			if (hasPerm(sender, "toggle") || hasPerm(sender, "set")
					|| debugUsers.contains(sender.getName())) {
				spylist.put(sender.getName().toLowerCase(), join(args, " ", 1));
				sender.sendMessage("Set CommandSpy to " + join(args, " ", 1));
			}
			// /////////////////////////////////
		} else if (args[0].equalsIgnoreCase("help")) {
			// //////////////////////////////
			if (args.length < 2) {
				sender.sendMessage("/commandspy set args " + ChatColor.GRAY
						+ " - Turn commandspying on for you.");
				sender.sendMessage("   - '/commandspy help set' for more information.");
				sender.sendMessage("/commandspy help " + ChatColor.GRAY
						+ " - Display this help message.");
				sender.sendMessage("/commandspy version " + ChatColor.GRAY
						+ " - gets version information.");
				sender.sendMessage("/commandspy tool" + ChatColor.GRAY
						+ " - toggles worldedit history tool");
			} else if (args[1].equalsIgnoreCase("set")) {
				sender.sendMessage("/commandspy [c:{apuws|*}] [s:{apu|*}]");

				sender.sendMessage("where for c (commands):");
				sender.sendMessage("a - anyone who has arguments set.");
				sender.sendMessage("p - anyone with commandspy.set.");
				sender.sendMessage("u - normal users.");
				sender.sendMessage("w - worldedit");
				sender.sendMessage("s - server executed commands.");
				sender.sendMessage("* - all commands.");
				sender.sendMessage("and for s (signs):");
				sender.sendMessage("a - anyone who has arguments set");
				sender.sendMessage("p - anyone with commandspy.set.");
				sender.sendMessage("u - normal users.");
				sender.sendMessage("");
				sender.sendMessage("So, for instance, if you wanted to see user commands, server commands and worldedit commands and all sign changes, you would type:");
				sender.sendMessage("commandspy c:usw s:*");
				// /////////////////////////////
			}

		} else if (args[0].equalsIgnoreCase("version")) {
			sender.sendMessage(ChatColor.DARK_GRAY + "CommandSpy version is "
					+ getDescription().getVersion());
		} else if (args[0].equalsIgnoreCase("tool")) {
			if (!statistics.useStats) {
				sender.sendMessage(ChatColor.RED + "Not connected to database.");
				return true;
			}
			if (!hasPerm(sender, "tool"))
				return true;
			if (!weBlock.containsKey(sender.getName().toLowerCase()))
				weBlock.put(sender.getName(), false);
			weBlock.put(sender.getName(), !weBlock.get(sender.getName()));
			sender.sendMessage("Tool(" + weBlockID + "):"
					+ weBlock.get(sender.getName().toLowerCase()));
		} else if (args[0].equalsIgnoreCase("true")) {
			if(!hasPerm(sender,"toggle")) return true;
			spylist.put(sender.getName().toLowerCase(), "c:* s:*");
		} else if (args[0].equalsIgnoreCase("false")) {
			if(!hasPerm(sender,"toggle")&&!hasPerm(sender,"set")) return true;
			spylist.remove(sender.getName().toLowerCase());
		} else if (args[0].equalsIgnoreCase("debug")) {
			if(!hasPerm(sender,"debug")) return true;
			sv_debug = !sv_debug;
		} else if (args[0].equalsIgnoreCase("ignore")) {
			if(!hasPerm(sender,"ignore")) return true;
			blacklistedcommands.add(args[1]);
		} else if (args[0].equalsIgnoreCase("unignore")) {
			if(!hasPerm(sender,"unignore")) return true;
			blacklistedcommands.remove(args[1]);
		} else {
			return false;
		}

		return true;
	}

	public boolean hasflag(char section, char flag, String player) {
		try {
			char csection = 'c';
			boolean isinsection = false;
			if (!spylist.get(player.toLowerCase()).contains(" ")
					&& !spylist.get(player.toLowerCase()).contains(":"))
				isinsection = true;
			for (char c : spylist.get(player.toLowerCase()).toCharArray()) {
				if (isinsection && c != ' ' && c != ':') {
					if (csection == section && c == flag)
						return true;
				} else if (!isinsection && c != ' ' && c != ':') {
					csection = c;
					isinsection = true;
				}
				if (c == ' ') {
					isinsection = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean hasflag(char section, char flag, Player player) {
		return hasflag(section, flag, player.getName().toLowerCase());
	}

	public static String join(String[] a, String delimiter, Integer startIndex) {
		try {
			Collection<String> s = Arrays.asList(a);
			StringBuffer buffer = new StringBuffer();
			Iterator<String> iter = s.iterator();

			while (iter.hasNext()) {
				if (startIndex == 0) {
					buffer.append(iter.next());
					if (iter.hasNext()) {
						buffer.append(delimiter);
					}
				} else {
					startIndex--;
					iter.next();
				}
			}

			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String sit(String iStr, char delimiter, int part) {
		if (part == 0) {
			if (!iStr.contains(String.valueOf(delimiter)))
				return iStr;
		} else {
			if (!iStr.contains(String.valueOf(delimiter)))
				return "";
		}
		if (part == 0)
			return iStr.substring(0, (iStr.indexOf(delimiter, 0)));
		return iStr.substring(iStr.indexOf(delimiter, 0) + 1, iStr.length());
	}

	public Boolean hasPerm(Player p, String perm) {
		if (p.hasPermission(getDescription().getName() + "." + perm))
			return true;
		else {
			p.sendMessage(ChatColor.RED
					+ "You do not have permission to do this.");
			return false;
		}

	}

	public Boolean hasPerm(CommandSender p, String perm) {
		if (p.hasPermission(getDescription().getName() + "." + perm))
			return true;
		else {
			p.sendMessage(ChatColor.RED
					+ "You do not have permission to do this.");
			return false;
		}

	}

	// /////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////
	public class playerListener implements Listener {
		private commandspy plugin;

		public playerListener(commandspy Cspy) {
			plugin = Cspy;
		}

		@EventHandler()
		public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
			if(blacklistedcommands.contains(sit(event.getMessage(),' ',0))) return;
			loggedcommand cmd = new loggedcommand(commandspy.sit(
					event.getMessage(), ' ', 0),
					sit(event.getMessage(), ' ', 1), event.getPlayer()
							.getName(), "unimplemented", event.getPlayer()
							.getWorld().getName(), false,
					getPlayerRegion(event.getPlayer()), event.getPlayer()
							.getLocation(), plugin);
			statistics.addcommand(cmd);
			for (Player p : getServer().getOnlinePlayers()) {
				if (spylist.containsKey(p.getName().toLowerCase())) {
					// ///////////////////////////////////////
					if (spylist.containsKey(event.getPlayer().getName()
							.toLowerCase())) {
						if (hasflag('c', 'a', p)
								|| hasflag('c', '*', p)
								|| (event.getMessage().startsWith("//") && hasflag(
										'c', 'w', p)))
							cmd.tellPlayer(p);
					} else if (event.getPlayer().hasPermission(
							"commandspy.toggle")) {
						if (hasflag('c', 'p', p)
								|| hasflag('c', '*', p)
								|| (event.getMessage().startsWith("//") && hasflag(
										'c', 'w', p)))
							cmd.tellPlayer(p);
					} else {
						if (hasflag('c', 'u', p)
								|| hasflag('c', '*', p)
								|| (event.getMessage().startsWith("//") && hasflag(
										'c', 'w', p)))
							cmd.tellPlayer(p);
					}
					// ///////////////////////////////////////
				}
			}
		}

		@EventHandler()
		public void onservercommandevent(ServerCommandEvent event) {
			if(blacklistedcommands.contains(sit(event.getCommand(),' ',0))) return;
			for (Player p : getServer().getOnlinePlayers()) {
				if (spylist.containsKey(p.getName().toLowerCase())) {
					if (hasflag('c', 's', p) || hasflag('c', '*', p))
						p.sendMessage(ChatColor.DARK_PURPLE + "[Server]"
								+ event.getCommand());
				}
			}
		}

		@EventHandler()
		public void onsignwritten(org.bukkit.event.block.SignChangeEvent event) {
			if (join(event.getLines(), "|", 0).length() < 4)
				return;
			for (Player p : getServer().getOnlinePlayers()) {
				if (spylist.containsKey(p.getName().toLowerCase())) {
					if (spylist.containsKey(event.getPlayer().getName()
							.toLowerCase())) {
						if (hasflag('s', 'a', p) || hasflag('s', '*', p))
							p.sendMessage("[Sign]" + ChatColor.AQUA
									+ event.getPlayer().getName() + ": "
									+ join(event.getLines(), "|", 0));
					} else if (event.getPlayer().hasPermission(
							"commandspy.toggle")) {
						if (hasflag('s', 'p', p) || hasflag('s', '*', p))
							p.sendMessage("[Sign]" + ChatColor.DARK_AQUA
									+ event.getPlayer().getName() + ": "
									+ join(event.getLines(), "|", 0));
					} else {
						if (hasflag('s', 'u', p) || hasflag('s', '*', p))
							p.sendMessage("[Sign]" + ChatColor.YELLOW
									+ event.getPlayer().getName() + ": "
									+ join(event.getLines(), "|", 0));
					}
				}
			}
		}

		// /////////////////////////
		@EventHandler()
		public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
			if (!weBlock.containsKey(event.getPlayer().getName().toLowerCase()))
				return;
			if (weBlock.get(event.getPlayer().getName().toLowerCase())
					&& event.getPlayer().getItemInHand().getTypeId() == weBlockID) {
				event.setCancelled(true);
				event.getPlayer().sendMessage("Checking for worldedits...");
				for (loggedcommand l : statistics.getWorldEdits(event
						.getBlock().getLocation())) {
					l.tellPlayerDB(event.getPlayer());
				}
			}
		}

		@EventHandler()
		public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
			if (!weBlock.containsKey(event.getPlayer().getName().toLowerCase()))
				return;
			if (weBlock.get(event.getPlayer().getName().toLowerCase())
					&& event.getPlayer().getItemInHand().getTypeId() == weBlockID) {
				event.setCancelled(true);
				event.getPlayer().sendMessage("Checking for worldedits...");
				for (loggedcommand l : statistics.getWorldEdits(event
						.getBlock().getLocation())) {
					l.tellPlayerDB(event.getPlayer());
				}
			}
		}

		public void search(Location l) {

		}

		public Region getPlayerRegion(Player p) {
			try {
				return plugin.worldedit.getSession(p.getName()).getSelection(
						plugin.worldedit.getSession(p.getName())
								.getSelectionWorld());
			} catch (Exception e) {
				// e.printStackTrace();
				return null;
			}
		}

		// ////////////////////////////

	}

}
