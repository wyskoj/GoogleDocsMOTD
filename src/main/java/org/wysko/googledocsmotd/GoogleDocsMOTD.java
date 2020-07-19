/*
 *
 * MIT License
 *
 * Copyright (c) 2020 Jacob Wysko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.wysko.googledocsmotd;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		String motd;
		try {
			motd = MOTD.MOTDFromDoc(documentID);
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "The MOTD could not be set.", e);
			return;
		}
		event.setMotd(motd);
	}
}
