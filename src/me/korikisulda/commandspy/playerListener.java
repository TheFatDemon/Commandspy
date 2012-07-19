package me.korikisulda.commandspy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class playerListener implements Listener {
	private commandspy plugin;

	public playerListener(commandspy Cspy) {
		plugin = Cspy;
	}

	@EventHandler()
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (plugin.blacklistedcommands.contains(plugin.util.sit(event.getMessage(), ' ', 0)))
			return;
		loggedcommand cmd = new loggedcommand(plugin.util.sit(
				event.getMessage(), ' ', 0),
				plugin.util.sit(event.getMessage(), ' ', 1), event.getPlayer()
						.getName(), "unimplemented", event.getPlayer()
						.getWorld().getName(), false,
				plugin.util.getPlayerRegion(event.getPlayer()), event.getPlayer()
						.getLocation(), plugin);
		plugin.statistics.addcommand(cmd);
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (plugin.spylist.containsKey(p.getName().toLowerCase())) {
				// ///////////////////////////////////////
				if (plugin.spylist.containsKey(event.getPlayer().getName()
						.toLowerCase())) {
					if (plugin.hasflag('c', 'a', p)
							|| plugin.hasflag('c', '*', p)
							|| (event.getMessage().startsWith("//") && plugin.hasflag(
									'c', 'w', p)))
						cmd.tellPlayer(p);
				} else if (event.getPlayer().hasPermission(
						"commandspy.toggle")) {
					if (plugin.hasflag('c', 'p', p)
							|| plugin.hasflag('c', '*', p)
							|| (event.getMessage().startsWith("//") && plugin.hasflag(
									'c', 'w', p)))
						cmd.tellPlayer(p);
				} else {
					if (plugin.hasflag('c', 'u', p)
							|| plugin.hasflag('c', '*', p)
							|| (event.getMessage().startsWith("//") && plugin.hasflag(
									'c', 'w', p)))
						cmd.tellPlayer(p);
				}
				// ///////////////////////////////////////
			}
		}
		if (plugin.spylist.containsKey("console")) {
			// ///////////////////////////////////////
			if (plugin.spylist.containsKey(event.getPlayer().getName()
					.toLowerCase())) {
				if (plugin.hasflag('c', 'a', "console")
						|| plugin.hasflag('c', '*', "console")
						|| (event.getMessage().startsWith("//") && plugin.hasflag(
								'c', 'w', "console")))
					cmd.tellServer();
			} else if (event.getPlayer().hasPermission(
					"commandspy.toggle")) {
				if (plugin.hasflag('c', 'p', "console")
						|| plugin.hasflag('c', '*', "console")
						|| (event.getMessage().startsWith("//") && plugin.hasflag(
								'c', 'w', "console")))
					cmd.tellServer();
			} else {
				if (plugin.hasflag('c', 'u', "console")
						|| plugin.hasflag('c', '*',"console")
						|| (event.getMessage().startsWith("//") && plugin.hasflag(
								'c', 'w', "console")))
					cmd.tellServer();
			}
			// ///////////////////////////////////////
		}
	}

	@EventHandler()
	public void onservercommandevent(ServerCommandEvent event) {
		if (plugin.blacklistedcommands.contains(plugin.util.sit(event.getCommand(), ' ', 0)))
			return;
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (plugin.spylist.containsKey(p.getName().toLowerCase())) {
				if (plugin.hasflag('c', 's', p) || plugin.hasflag('c', '*', p))
					p.sendMessage(ChatColor.DARK_PURPLE + "[Server]"
							+ event.getCommand());
			}
		}
	}

	@EventHandler()
	public void onsignwritten(org.bukkit.event.block.SignChangeEvent event) {
		if (plugin.util.join(event.getLines(), "|", 0).length() < 4)
			return;
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (plugin.spylist.containsKey(p.getName().toLowerCase())) {
				if (plugin.spylist.containsKey(event.getPlayer().getName()
						.toLowerCase())) {
					if (plugin.hasflag('s', 'a', p) || plugin.hasflag('s', '*', p))
						p.sendMessage("[Sign]" + ChatColor.AQUA
								+ event.getPlayer().getName() + ": "
								+ plugin.util.join(event.getLines(), "|", 0));
				} else if (event.getPlayer().hasPermission(
						"commandspy.toggle")) {
					if (plugin.hasflag('s', 'p', p) || plugin.hasflag('s', '*', p))
						p.sendMessage("[Sign]" + ChatColor.DARK_AQUA
								+ event.getPlayer().getName() + ": "
								+ plugin.util.join(event.getLines(), "|", 0));
				} else {
					if (plugin.hasflag('s', 'u', p) || plugin.hasflag('s', '*', p))
						p.sendMessage("[Sign]" + ChatColor.YELLOW
								+ event.getPlayer().getName() + ": "
								+ plugin.util.join(event.getLines(), "|", 0));
				}
			}
		}
		
		if (plugin.spylist.containsKey("console")) {
			if (plugin.spylist.containsKey(event.getPlayer().getName()
					.toLowerCase())) {
				if (plugin.hasflag('s', 'a', "console") || plugin.hasflag('s', '*', "console"))
					plugin.log.info("[Sign]" 
							+ event.getPlayer().getName() + ": "
							+ plugin.util.join(event.getLines(), "|", 0));
			} else if (event.getPlayer().hasPermission(
					"commandspy.toggle")) {
				if (plugin.hasflag('s', 'p', "console") || plugin.hasflag('s', '*', "console"))
					plugin.log.info("[Sign]"
							+ event.getPlayer().getName() + ": "
							+ plugin.util.join(event.getLines(), "|", 0));
			} else {
				if (plugin.hasflag('s', 'u', "console") || plugin.hasflag('s', '*', "console"))
					plugin.log.info("[Sign]" 
							+ event.getPlayer().getName() + ": "
							+ plugin.util.join(event.getLines(), "|", 0));
			}
		}
	}

	// /////////////////////////
	@EventHandler()
	public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
		if (!plugin.weBlock.containsKey(event.getPlayer().getName().toLowerCase()))
			return;
		if (plugin.weBlock.get(event.getPlayer().getName().toLowerCase())
				&& event.getPlayer().getItemInHand().getTypeId() == plugin.weBlockID) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("Checking for worldedits...");
			for (loggedcommand l : plugin.statistics.getWorldEdits(event
					.getBlock().getLocation())) {
				l.tellPlayerDB(event.getPlayer());
			}
		}
	}

	@EventHandler()
	public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
		if (!plugin.weBlock.containsKey(event.getPlayer().getName().toLowerCase()))
			return;
		if (plugin.weBlock.get(event.getPlayer().getName().toLowerCase())
				&& event.getPlayer().getItemInHand().getTypeId() == plugin.weBlockID) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("Checking for worldedits...");
			for (loggedcommand l : plugin.statistics.getWorldEdits(event
					.getBlock().getLocation())) {
				l.tellPlayerDB(event.getPlayer());
			}
		}
	}

	public void search(Location l) {

	}

	

	// ////////////////////////////

}
