package com.questworld.extensions.placeholderapi;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.Ticking;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.MissionEntry;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderMission extends MissionType implements Ticking {

	public PlaceholderMission() {
		super("PLACEHOLDER", false, new ItemStack(Material.STONE));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		return null;
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return null;
	}

	@Override
	public void onManual(Player player, MissionEntry entry) {
		String value = PlaceholderAPI.setPlaceholders(player, entry.getMission().getCustomString());
		
		if(value != null)
			try {
				entry.setProgress((int) Double.parseDouble(value));
			}
			catch(NumberFormatException e) {
			}
	}

}
