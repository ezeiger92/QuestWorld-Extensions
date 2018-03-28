package com.questworld.extensions.placeholderapi;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderHook;

public class Providers extends PlaceholderHook {

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if(player == null)
			return null;
		String[] args = identifier.split("_");
		
		switch(args[0]) {
			case "category":
			case "quest":
			case "mission":

			default: break;
		}
		
		return null;
	}
}
