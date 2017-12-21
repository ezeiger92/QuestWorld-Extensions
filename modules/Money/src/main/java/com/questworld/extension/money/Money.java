package com.questworld.extension.money;

import org.bukkit.plugin.Plugin;

import me.mrCookieSlime.QuestWorld.api.QuestExtension;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.util.Log;
import net.milkbowl.vault.economy.Economy;

public class Money extends QuestExtension {
	private static Economy economy = null;
	
	public static Economy getEcon() {
		return economy;
	}
	
	public static String formatCurrency(String format, int amount) {
		String[] segments = format.split(",");
		String backup = "$";
		
		for(String s : segments) {
			String[] parts = s.split(":", 2);
			if(parts.length == 1)
				backup = parts[0];
			else {
				try{
					int i = Integer.valueOf(parts[0]);
					if(i == amount)
						return parts[1];
				}
				catch(NumberFormatException e) {
					Log.warning("Unknown currency format: \"" + format + "\", expected \"default[,#:override[, ..]]");
				}
			}
		}
		
		return backup;
	}
	
	public Money() {
		super("Vault");
		setMissionTypes(
			new BalanceMission(),
			new PayMission());
	}

	@Override
	protected void initialize(Plugin parent) {
		economy = QuestWorld.getEconomy().orElseThrow(() ->
			new NullPointerException("Economy is required for this extension!"));
	}
}
