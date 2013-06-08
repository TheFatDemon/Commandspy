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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {
	commandspy plugin;
	public Commands(commandspy instance) {
		plugin = instance;
	}
	
public void help(CommandSender sender,String[] args){
	if (args.length < 2) {
		sender.sendMessage("/commandspy set args " + ChatColor.GRAY
				+ " - Turn commandspying on for you.");
		sender.sendMessage("   - '/commandspy help set' for more information.");
		sender.sendMessage("/commandspy on " + ChatColor.GRAY
				+ " - Turn commandspying on (c:*).");
		sender.sendMessage("/commandspy off " + ChatColor.GRAY
				+ " - Clear your commandspying arguments.");
		sender.sendMessage("/commandspy help " + ChatColor.GRAY
				+ " - Display this help message.");
		sender.sendMessage("/commandspy version " + ChatColor.GRAY
				+ " - Gets version information.");
		sender.sendMessage("/commandspy tool" + ChatColor.GRAY
				+ " - Toggles worldedit history tool");
		sender.sendMessage("/commandspy ignore <add|remove> /command" + ChatColor.GRAY
				+ " - Adds or removes a command from the ignore list.");
		sender.sendMessage("/commandspy mode simplenotice|chat" + ChatColor.GRAY 
				+ " - Changes command display mode between in-game chat, and simplenotice.");
		sender.sendMessage("/commandspy reload" + ChatColor.GRAY 
				+ " - Reloads settings from config: this includes all changed flags and modes.");
		sender.sendMessage("/commandspy save" + ChatColor.GRAY 
				+ " - Saves config to file.");
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
	
}

	
	public void mode(CommandSender sender,String[] args){
		if ((!plugin.util.hasPerm((Player)sender, new String[]{"set","toggle"})))	return;
		if(args[1].equalsIgnoreCase("chat")){plugin.modes.remove(sender.getName().toLowerCase()); sender.sendMessage("Set your command notifying mode to " + args[1] + ".");} 
		else if(args[1].equalsIgnoreCase("simplenotice")||args[1].equalsIgnoreCase("notice")){
			plugin.modes.put(sender.getName().toLowerCase(), 1);
			sender.sendMessage("Set your command notifying mode to " + args[1] + ".");
		}else{
			sender.sendMessage("Mode '" + args[1]  + "' not recognised.");
		}
	}
	
	public void set(CommandSender sender,String[] args){
		if ((!plugin.util.hasPerm(sender, new String[]{"set","toggle"}))
				&& !plugin.debugUsers.contains(sender.getName()))  return;
			plugin.spylist.put(sender.getName().toLowerCase(),
					plugin.util.join(args, " ", 1));
			sender.sendMessage("Set CommandSpy to "
					+ plugin.util.join(args, " ", 1));
		
	}
	
	public void ignore(CommandSender sender,String[] args){
		if(args[1].equalsIgnoreCase("add")){
			if(!plugin.util.hasPerm(sender, new String[]{"ignore.add"})) return;
			plugin.blacklistedcommands.add(args[2]);
			sender.sendMessage("'" + args[2] +"' added.");
		}else if(args[1].equalsIgnoreCase("remove")){
			if(!plugin.util.hasPerm(sender, new String[]{"ignore.remove"})) return;
			plugin.blacklistedcommands.remove(args[2]);
			sender.sendMessage("'" + args[2] +"' removed.");
		}else if(args[1].equalsIgnoreCase("list")){
			if(!plugin.util.hasPerm(sender, new String[]{"ignore.list"})) return;
			sender.sendMessage("==========================");
			for(String c:plugin.blacklistedcommands){
				sender.sendMessage("- " + ChatColor.GREEN + c);
			}
			sender.sendMessage("==========================");
					}
	}
}
