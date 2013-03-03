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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.regions.Region;

public class Util {
	commandspy plugin;

	public Util(commandspy instance) {
		plugin = instance;
	}
	
	public void SendUserMessage(Player user,String message){
		if(!plugin.modes.containsKey(user.getName())){user.sendMessage(message); return;}
		if(plugin.modes.get(user.getName())==1) user.sendPluginMessage(plugin, "SimpleNotice", message.getBytes());
	}
	

	
	public void readConfig(boolean save){
		plugin.reloadConfig();
		if (!plugin.getConfig().isSet("users"))
			plugin.getConfig().createSection("users", plugin.spylist);
		for (String s : plugin.getConfig().getConfigurationSection("users").getKeys(
				false)) {
			plugin.spylist.put(s, plugin.getConfig().getConfigurationSection("users")
					.getString(s));
		}
			
			if (!plugin.getConfig().isSet("modes"))
				plugin.getConfig().createSection("modes", plugin.modes);
			for (String m : plugin.getConfig().getConfigurationSection("modes").getKeys(
					false)) {
				plugin.modes.put(m, plugin.getConfig().getConfigurationSection("modes")
						.getInt(m));
		}
			
			if (!plugin.getConfig().isSet("commandUserPrefix"))
				plugin.getConfig().set("commandUserPrefix", "&e");
			
			if (!plugin.getConfig().isSet("commandPermissionPrefix"))
				plugin.getConfig().set("commandPermissionPrefix", "&3");
			
			if (!plugin.getConfig().isSet("commandAdminPrefix"))
				plugin.getConfig().set("commandAdminPrefix", "&b");
			
			
			if (!plugin.getConfig().isSet("signUserPrefix"))
				plugin.getConfig().set("signUserPrefix", "&e");
			
			if (!plugin.getConfig().isSet("signPermissionPrefix"))
				plugin.getConfig().set("signPermissionPrefix", "&3");
			
			if (!plugin.getConfig().isSet("signAdminPrefix"))
				plugin.getConfig().set("signAdminPrefix", "&b");
			
			
			
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
		if (!plugin.getConfig().isSet("IgnoreSelf"))
			plugin.getConfig().set("IgnoreSelf", false);
		if (!plugin.getConfig().isSet("dontlog"))
			plugin.getConfig().set("dontlog", plugin.blacklistedcommands);
		plugin.blacklistedcommands = plugin.getConfig().getStringList("dontlog");

		if (!plugin.getConfig().isSet("debugUsers"))
			plugin.getConfig().set("debugUsers", plugin.debugUsers);
		plugin.debugUsers = plugin.getConfig().getStringList("debugUsers");
		if (!plugin.getConfig().isSet("weBlockID"))
			plugin.getConfig().set("weBlockID", 46);
		
		if(save) plugin.saveConfig();
		
		plugin.weBlockID = plugin.getConfig().getInt("weBlockID");
		// ///////////////////////////////////////////////////////
		/*
		 * Disabled for security reasons, source kept for those who understand the implications of use.
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
		*/
	}
	
	public void saveConfig(){
		plugin.getConfig().createSection("users", plugin.spylist);
		plugin.getConfig().createSection("modes", plugin.modes);
		plugin.getConfig().set("dontlog", plugin.blacklistedcommands);
		plugin.saveConfig();
		if (plugin.getConfig().getBoolean("useMySQL"))
			plugin.statistics.stopconnection();
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
	
	public String[] split(String iStr,char delimiter){
		ArrayList<String> sl=new ArrayList<String>();
		String current="";
		for(char c:iStr.toCharArray()){
			if(c==delimiter){
				sl.add(current);
				current="";
			}else{
				current+=c;
			}
		}
		sl.add(current);
		return (String[]) sl.toArray();
	}

	public Boolean hasPerm(Player sender, String[] string) {
		for(String sp:string){
			if (sender.hasPermission(plugin.getDescription().getName() + "." + sp))
				return true;
		}
		
			sender.sendMessage(ChatColor.RED
					+ "You do not have permission to do that:");
			for(String sp:string){
sender.sendMessage("-" + ChatColor.GREEN +plugin.getDescription().getName() + "." + sp);
					
			}
			return false;
		

	}
	
	public Boolean hasPerm(CommandSender p,String[] perm){
		for(String sp:perm){
			if (p.hasPermission(plugin.getDescription().getName() + "." + sp))
				return true;
		}
		
			p.sendMessage(ChatColor.RED
					+ "You do not have permission to do that:");
			for(String sp:perm){
p.sendMessage("-" + ChatColor.GREEN +plugin.getDescription().getName() + "." + sp);
					
			}
			return false;
		

		}
	
	public Region getPlayerRegion(Player p) {
		if(!plugin.getServer().getPluginManager().isPluginEnabled("WorldEdit")) return null;
		try {
			return plugin.worldedit.getSession(p.getName()).getSelection(
					plugin.worldedit.getSession(p.getName())
							.getSelectionWorld());
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}
	
	
}
