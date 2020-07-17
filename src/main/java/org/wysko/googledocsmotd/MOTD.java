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

import com.google.api.services.docs.v1.model.Document;
import net.md_5.bungee.api.ChatColor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MOTD {
	/**
	 * Converts a Google Docs RGB value set to a hex code.
	 *
	 * @param r the red value
	 * @param g the green value
	 * @param b the blue value
	 * @return the hex code
	 */
	private static String rgbToHex(double r, double g, double b) {
		return String.format("#%02x%02x%02x", (int) (r * 255), (int) (g * 255), (int) (b * 255));
	}
	
	/**
	 * Generates and returns a Minecraft-friendly string that closely represents the contents of the Google Doc.
	 *
	 * @param documentID the ID of the Google Doc
	 * @return the MOTD as a Minecraft formatted string
	 */
	public static String MOTDFromDoc(String documentID) throws GeneralSecurityException, IOException {
		Document document = GoogleDocsAPI.getDocument(documentID);
		JSONObject object = new JSONObject(document.getBody().toPrettyString());
		
		JSONArray content = object.getJSONArray("content");
		StringBuilder MOTD = new StringBuilder();
		
		for (int i = 1; i < content.length(); i++) {
			final JSONArray pElements = ((JSONObject) content.get(i)).getJSONObject("paragraph").getJSONArray("elements");
			for (int j = 0; j < pElements.length(); j++) {
				String elementText;
				final JSONObject textRun = ((JSONObject) pElements.get(j)).getJSONObject("textRun");
				elementText = textRun.getString("content");
				final JSONObject textStyle = textRun.getJSONObject("textStyle");
				boolean bold = textStyle.has("bold");
				boolean italic = textStyle.has("italic");
				boolean underline = textStyle.has("underline");
				boolean strikeout = textStyle.has("strikethrough");
				JSONObject color = textStyle.getJSONObject("foregroundColor").getJSONObject("color").getJSONObject("rgbColor");
				double r, g, b;
				if (color.has("red"))
					r = color.getDouble("red");
				else
					r = 0;
				
				if (color.has("green"))
					g = color.getDouble("green");
				else
					g = 0;
				
				if (color.has("blue"))
					b = color.getDouble("blue");
				else
					b = 0;
				String hex = rgbToHex(r, g, b);
				MOTD.append(ChatColor.RESET);
				if (bold) MOTD.append(ChatColor.BOLD);
				if (italic) MOTD.append(ChatColor.ITALIC);
				if (underline) MOTD.append(ChatColor.UNDERLINE);
				if (strikeout) MOTD.append(ChatColor.STRIKETHROUGH);
				MOTD.append(ChatColor.of(hex).toString());
				MOTD.append(elementText);
			}
			MOTD.append("\n");
		}
		return MOTD.toString()
				.replaceAll("\n{2,}", "\n");
	}
}
