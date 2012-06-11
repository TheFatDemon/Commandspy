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

import java.sql.ResultSet;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

import lib.PatPeter.SQLibrary.MySQL;

public class statistics {
	commandspy plugin;

	public statistics(commandspy instance) {
		plugin = instance;
	}

	public MySQL mysql;
	public boolean useStats = false;

	public void startconnection(String host, Integer port, String username,
			String password, String database) {
		mysql = new MySQL(plugin.log, "[CommandSpy]", host, port.toString(),
				database, username, password);
		try {
			mysql.open();
			// "CREATE TABLE IF NOT EXISTS commandspy (id timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, point1x bigint(14) NOT NULL, point1y bigint(14) NOT NULL, point1z bigint(14) NOT NULL, point2x bigint(14) NOT NULL, point1y bigint(14) NOT NULL, point1z bigint(14) NOT NULL, PRIMARY KEY (id)) ENGINE=MyISAM;"
			mysql.query("CREATE TABLE IF NOT EXISTS commandspy (id int(11) NOT NULL auto_increment, cmdBase varchar(255) NOT NULL, cmdExt varchar(255) default NULL, username varchar(255) NOT NULL, usergroup varchar(255) NOT NULL, userDest varchar(255) NOT NULL, world varchar(255) NOT NULL, cmdTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, cmdStatus int(1) NOT NULL default '0', isWE int(1) NOT NULL default '0', PRIMARY KEY (id)) ENGINE=MyISAM;");
			mysql.query("CREATE TABLE IF NOT EXISTS commandspy_we (id int(11) NOT NULL, x1 int(5) NOT NULL, y1 int(5) NOT NULL, z1 int(5) NOT NULL, x2 int(5) NOT NULL, y2 int(5) NOT NULL, z2 int(5) NOT NULL,  PRIMARY KEY (id)) ENGINE=MyISAM;");
			useStats = true;
		} catch (Exception e) {
			plugin.log.info(e.getMessage());
		}

	}

	public void stopconnection() {
		useStats = false;
		mysql.close();
	}

	public void addcommand(loggedcommand Log) {
		int status = 0;
		int westatus = 0;
		if (Log.denied)
			status = 1;
		else
			status = 0;
		if (Log.isWorldedit)
			westatus = 1;
		else
			westatus = 0;
		if (plugin.getServer().getPluginCommand(Log.getCommandStripped()) == null)
			status = 2;
		if (!useStats)
			return;
		try {
			mysql.query(
					"INSERT INTO commandspy (cmdBase, cmdExt, username, usergroup, userDest, world, cmdStatus, isWE) VALUES	('"
							+ Log.command
							+ "','"
							+ Log.arguments
							+ "','"
							+ Log.playername
							+ "','"
							+ Log.groupname
							+ "','"
							+ Log.location.getBlockX()
							+ " "
							+ Log.location.getBlockY()
							+ " "
							+ Log.location.getBlockZ()
							+ "','"
							+ Log.world
							+ "'," + status + "," + westatus + ");").close();
			if (Log.isWorldedit)
				mysql.query(
						"INSERT INTO commandspy_we VALUES (LAST_INSERT_ID(),"
								+ Log.selection.getMinimumPoint().getBlockX()
								+ ","
								+ Log.selection.getMinimumPoint().getBlockY()
								+ ","
								+ Log.selection.getMinimumPoint().getBlockZ()
								+ ","
								+ Log.selection.getMinimumPoint().getBlockX()
								+ ","
								+ Log.selection.getMinimumPoint().getBlockY()
								+ ","
								+ Log.selection.getMinimumPoint().getBlockZ()
								+ ");").close();
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
	}

	public ArrayList<loggedcommand> getWorldEdits(Location loc) {
		ArrayList<loggedcommand> logs = new ArrayList<loggedcommand>();
		ResultSet rs = mysql
				.query("SELECT * FROM commandspy JOIN commandspy_we ON commandspy.id = commandspy_we.id WHERE IsWE = 1 AND ((x1 <= "
						+ loc.getBlockX()
						+ " <= x2) OR (x2 <= "
						+ loc.getBlockX()
						+ " <= x1)) AND ((y1 <= "
						+ loc.getBlockY()
						+ " <= y2) OR (y2 <= "
						+ loc.getBlockY()
						+ " <= y1)) AND ((z1 <= "
						+ loc.getBlockZ()
						+ " <= z2) OR (z2 <= "
						+ loc.getBlockZ() + " <= z1));");
		try {

			while (rs.next()) {
				loggedcommand l = new loggedcommand(plugin);
				// cmdBase, cmdExt, username, usergroup, userDest, world,
				// cmdStatus, isWE
				l.command = rs.getString("cmdExt");
				l.arguments = rs.getString("cmdExt");
				l.playername = rs.getString("username");
				l.groupname = rs.getString("usergroup");
				l.world = rs.getString("world");
				l.denied = true;
				World world = plugin.getServer()
						.getWorld(rs.getString("world"));
				l.DBselection = new CuboidSelection(world, new Location(world,
						0F, 0F, 0F), new Location(world, 0F, 0F, 0F));
				l.isWorldedit = true;
				l.location = null;
				l.isDatabaseEntry = true;
				l.timeExecuted = rs.getTimestamp("cmdTime");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			rs.close();
		} catch (Exception e) {
		}
		return logs;

	}

}
