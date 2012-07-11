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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.regions.*;

public class loggedcommand {
	public String command;
	public String arguments;
	public String playername;
	public String groupname;
	public String world;
	public boolean denied;
	public Region selection;
	public CuboidSelection DBselection;
	public boolean isWorldedit;
	public Location location;
	public List<String> WorldEditcommands = Arrays.asList(new String[] {
			"//replace", "//set", "//regen", "//walls" });
	public boolean isDatabaseEntry = false;
	public Timestamp timeExecuted;

	public String getCommandStripped() {
		return command.substring(1);
	}

	private commandspy plugin;

	public loggedcommand(String Command, String Args, String username,
			String usergroup, String World, boolean wasdenied,
			Region isSelected, Location loc, commandspy Cspy) {

		command = Command;
		arguments = Args;
		playername = username;
		groupname = usergroup;
		world = World;
		denied = wasdenied;
		location = loc;
		selection = isSelected;

		plugin = Cspy;
		if (WorldEditcommands.contains(command.toLowerCase())
				&& isSelected != null)
			isWorldedit = true;
	}

	public loggedcommand(loggedcommand Log, commandspy Cspy) {
		command = Log.command;
		arguments = Log.arguments;
		playername = Log.playername;
		groupname = Log.groupname;
		world = Log.world;
		denied = Log.denied;
		selection = Log.selection;
		isWorldedit = Log.isWorldedit;
		location = Log.location;
		plugin = Cspy;
	}

	public loggedcommand(commandspy Cspy) {
		plugin = Cspy;
	}

	public void tellPlayer(Player user) {
		plugin.log.info(String.valueOf(plugin.getConfig().getBoolean("IgnoreSelf")));
		if(playername.equalsIgnoreCase(user.getName())&&plugin.getConfig().getBoolean("IgnoreSelf")) return;
		if (plugin.spylist.containsKey(playername.toLowerCase())) {
			plugin.util.SendUserMessage(user, (getPrefix() + ChatColor.AQUA + playername + ": "
					+ command + " " + arguments));
		} else if (plugin.getServer().getPlayerExact(playername)
				.hasPermission("commandspy.toggle")
				|| plugin.getServer().getPlayerExact(playername)
						.hasPermission("commandspy.set")
				|| plugin.debugUsers.contains(playername)) {
			plugin.util.SendUserMessage(user, (getPrefix() + ChatColor.DARK_AQUA + playername
					+ ": " + command + " " + arguments));
		} else {
			plugin.util.SendUserMessage(user, (getPrefix() + ChatColor.YELLOW + playername + ": "
					+ command + " " + arguments));
		}
	}

	public void tellPlayerDB(Player user) {
		user.sendMessage(ChatColor.GRAY
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(timeExecuted) + ChatColor.WHITE + playername
				+ ": " + command + " " + arguments);
	}

	public String getPrefix() {
		try {
			if (command.startsWith("//")) {
				return ChatColor.YELLOW + "[WorldEdit][" + ChatColor.RED
						+ selection.getArea() + ChatColor.YELLOW + " blocks]"
						+ ChatColor.WHITE;
			}
		} catch (Exception e) {
			if (plugin.sv_debug)
				e.printStackTrace();
		}
		return "";
	}

	public static boolean getDeniedbool(int status) {
		if (status == 0)
			return true;
		else
			return false;
	}

}
