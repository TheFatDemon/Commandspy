/*
    Commandspy: A minecraft bukkit plugin to ensure that your users have no privacy at all.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class commandspy extends JavaPlugin {
	// declarations
	public Logger log = Logger.getLogger("Minecraft");
	public WorldEdit worldedit;
	public statistics statistics = new statistics(this);
	public Commands commands=new Commands(this);
	public Util util=new Util(this);
	public boolean sv_debug = false;
	public Map<String, String> spylist = new HashMap<String, String>();
	public Map<String,Integer> modes=new HashMap<String,Integer>();
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
		getConfig().set("dontlog", blacklistedcommands);
		saveConfig();
		if (getConfig().getBoolean("useMySQL"))
			statistics.stopconnection();
	}

	public void onEnable() {
		
		util.readConfig();
		
		worldedit = getWorldEdit().getWorldEdit();
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new playerListener(this), this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "SimpleNotice");
		
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		try {
			if (args.length == 0)
				args = new String[] { "help" };

			if (args[0].equalsIgnoreCase("set")) {
commands.set(sender, args);
				
			} else if (args[0].equalsIgnoreCase("help")) {
				
commands.help(sender, args);
				
			} else if (args[0].equalsIgnoreCase("version")) {
				sender.sendMessage(ChatColor.DARK_GRAY
						+ "CommandSpy version is "
						+ getDescription().getVersion());
			} else if (args[0].equalsIgnoreCase("tool")) {
				
				return commands.tool(sender, args);
				
			}else if(args[0].equalsIgnoreCase("mode")){
				
				commands.mode(sender, args);
					
			} else {
				return false;
			}
		} catch (Exception e) {
			if (sv_debug)
				e.printStackTrace();
			sender.sendMessage(ChatColor.RED + "An error occured.");
		}
		return true;
	}



	public boolean hasflag(char section, char flag, Player player) {
		return util.hasflag(section, flag, player.getName().toLowerCase());
	}



	
}
