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
