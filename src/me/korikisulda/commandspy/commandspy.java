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

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

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
			"korikisulda", "was", "here" });
	public List<String> blacklistedcommands = Arrays.asList(new String[] {
			"/auth", "/register", "/login" });
	public int weBlockID;

	// //////////////////////////////////////////////////////
	private WorldEditPlugin getWorldEdit() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
		// WorldEdit may not be loaded
		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			log.info("[Commandspy] Worldedit not loaded.");
			return null;
		} else {
			return (WorldEditPlugin) plugin;
		}

	}

	// //////////////////////////////////////////////////////

	public void onDisable() {
		saveConfig();
	}

	public void onEnable() {
		notifyOnUpdate();
		try{
		util.readConfig(true);
		
		if(getServer().getPluginManager().isPluginEnabled("WorldEdit")) worldedit = getWorldEdit().getWorldEdit();
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new playerListener(this), this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "SimpleNotice");
		}catch(Exception e){
			
		}
		
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
				
			}else if(args[0].equalsIgnoreCase("mode")){
				
				commands.mode(sender, args);
			}else if(args[0].equalsIgnoreCase("true")||args[0].equalsIgnoreCase("on")){
				args=new String[]{"set","c:*"};
				onCommand(sender, command, label, args);
				return true;
			}else if(args[0].equalsIgnoreCase("false")||args[0].equalsIgnoreCase("off")){
				if(spylist.containsKey(sender.getName().toLowerCase())){ spylist.remove(sender.getName().toLowerCase()); sender.sendMessage("Commandspy set to nothing.");}
				else sender.sendMessage(ChatColor.RED + "You don't have commanyspy on.");
			}else if(args[0].equalsIgnoreCase("ignore")){
				commands.ignore(sender, args);
			}else if(args[0].equalsIgnoreCase("reload")){
				if(!util.hasPerm(sender,new String[]{"reload"})) return true;
				util.readConfig(false);
				sender.sendMessage("Reloaded config.");
			}else if(args[0].equalsIgnoreCase("save")){
				if(!util.hasPerm(sender,new String[]{"save"})) return true;
				util.saveConfig();
				sender.sendMessage("Saved config");
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
	
	public boolean hasflag(char section, char flag, String player) {
		return util.hasflag(section, flag, player.toLowerCase());
	}


public void notifyOnUpdate(){
	//zanadema zrane pitamuri wuhanaje buukitdev nasma kofra -.-
	if(!getConfig().getBoolean("notifyOnUpdate",false)) return;
	try{
		
		URL filesFeed=new URL("http://dev.bukkit.org/server-mods/commandspy/files.rss");
		InputStream input = filesFeed.openConnection().getInputStream();
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

		Node latestFile = document.getElementsByTagName("item").item(0);
		NodeList children = latestFile.getChildNodes();

		String version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]","");
		String link = children.item(3).getTextContent();
if(!getDescription().getVersion().equals(version)){
	log.info("[Commandspy] This version of commandspy is outdated! latest version is " + version + " at: ");
	log.info(link);
	
}
		}catch(Exception e){
		//e.printStackTrace();
		}
}

public void notifyOnUpdate(Player p){
	try{
		URL filesFeed=new URL("http://dev.bukkit.org/server-mods/commandspy/files.rss");
		InputStream input = filesFeed.openConnection().getInputStream();
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

		Node latestFile = document.getElementsByTagName("item").item(0);
		NodeList children = latestFile.getChildNodes();

		String version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]","");
		String link = children.item(3).getTextContent();
if(!getDescription().getVersion().equals(version)){
	p.sendMessage(ChatColor.GRAY + "This version of commandspy is outdated! latest version is " + ChatColor.YELLOW + version + ChatColor.GRAY + " at: ");
	p.sendMessage(ChatColor.AQUA + link);
	
}
		}catch(Exception e){
		e.printStackTrace();
		}
}
	
}
