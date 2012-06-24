package me.korikisulda.commandspy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Util {
	commandspy plugin;

	public Util(commandspy instance) {
		plugin = instance;
	}
	
	public void SendUserMessage(Player user,String message){
		if(!plugin.modes.containsKey(user.getName())){user.sendMessage(message); return;}
		if(plugin.modes.get(user.getName())==1) user.sendPluginMessage(plugin, "SimpleNotice", message.getBytes());
	}
	
	public void readConfig(){
		if (!plugin.getConfig().isSet("users"))
			plugin.getConfig().createSection("users", plugin.spylist);
		for (String s : plugin.getConfig().getConfigurationSection("users").getKeys(
				false)) {
			plugin.spylist.put(s, plugin.getConfig().getConfigurationSection("users")
					.getString(s));
		}
		if (!plugin.getConfig().isSet("useMySQL"))
			plugin.getConfig().set("useMySQL", false);
		if (!plugin.getConfig().isSet("SQLhostname"))
			plugin.getConfig().set("SQLhostname", "");
		if (!plugin.getConfig().isSet("SQLusername"))
			plugin.getConfig().set("SQLusername", "");
		if (!plugin.getConfig().isSet("SQLpassword"))
			plugin.getConfig().set("SQLpassword", "12345");
		if (!plugin.getConfig().isSet("SQLdatabase"))
			plugin.getConfig().set("SQLdatabase", "");
		if (!plugin.getConfig().isSet("SQLport"))
			plugin.getConfig().set("SQLport", 3306);
		if (!plugin.getConfig().isSet("dontlog"))
			plugin.getConfig().set("dontlog", plugin.blacklistedcommands);
		plugin.blacklistedcommands = plugin.getConfig().getStringList("dontlog");

		if (!plugin.getConfig().isSet("debugUsers"))
			plugin.getConfig().set("debugUsers", plugin.debugUsers);
		plugin.debugUsers = plugin.getConfig().getStringList("debugUsers");
		if (!plugin.getConfig().isSet("weBlockID"))
			plugin.getConfig().set("weBlockID", 46);
		plugin.saveConfig();
		plugin.weBlockID = plugin.getConfig().getInt("weBlockID");
		// ///////////////////////////////////////////////////////
		if (plugin.getConfig().isSet("SQLhostname")
				&& plugin.getConfig().isSet("SQLusername")
				&& plugin.getConfig().isSet("SQLpassword")
				&& plugin.getConfig().getBoolean("useMySQL")
				&& plugin.getConfig().isSet("SQLdatabase")) {
			plugin.statistics.startconnection(plugin.getConfig().getString("SQLhostname"),
					plugin.getConfig().getInt("SQLport"),
					plugin.getConfig().getString("SQLusername"), plugin.getConfig()
							.getString("SQLpassword"),
							plugin.getConfig().getString("SQLdatabase"));
		}
	}
	
	public boolean hasflag(char section, char flag, String player) {
		try {
			char csection = 'c';
			boolean isinsection = false;
			if (!plugin.spylist.get(player.toLowerCase()).contains(" ")
					&& !plugin.spylist.get(player.toLowerCase()).contains(":"))
				isinsection = true;
			for (char c : plugin.spylist.get(player.toLowerCase()).toCharArray()) {
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
	
	public String join(String[] a, String delimiter, Integer startIndex) {
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
	
	public String sit(String iStr, char delimiter, int part) {
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
		if (p.hasPermission(plugin.getDescription().getName() + "." + perm))
			return true;
		else {
			p.sendMessage(ChatColor.RED
					+ "You do not have permission to do this.");
			return false;
		}

	}
	
	public Boolean hasPerm(CommandSender p,String perm){
		if (p.hasPermission(plugin.getDescription().getName() + "." + perm))
			return true;
		else {
			p.sendMessage(ChatColor.RED
					+ "You do not have permission to do this.");
			return false;
		}
	}
	
}
