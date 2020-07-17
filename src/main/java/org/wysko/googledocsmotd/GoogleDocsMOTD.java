package org.wysko.googledocsmotd;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.logging.Level;

/**
 * Sets your server's MOTD to the contents of a Google Doc.
 * @author Jacob Wysko
 */
public final class GoogleDocsMOTD extends JavaPlugin implements Listener {
	/**
	 * The ID of the Google Doc to read.
	 */
	static String documentID;
	
	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		try {
			documentID = new String(Files.readAllBytes(Paths.get("plugins/GoogleDocsMOTD/doc.txt")));
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not read document ID file.", e);
		}
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent event) {
		try {
			event.setMotd(MOTD.MOTDFromDoc(documentID));
		} catch (IOException | GeneralSecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, "The MOTD could not be set.", e);
		}
	}
}
