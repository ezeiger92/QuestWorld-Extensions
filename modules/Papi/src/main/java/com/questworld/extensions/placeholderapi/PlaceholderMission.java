package com.questworld.extensions.placeholderapi;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.SinglePrompt;
import com.questworld.api.Ticking;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.MissionEntry;
import com.questworld.api.menu.MissionButton;
import com.questworld.api.menu.QuestBook;
import com.questworld.util.ItemBuilder;
import com.questworld.util.PlayerTools;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderMission extends MissionType implements Ticking {

	public PlaceholderMission() {
		super("PLACEHOLDER", false, new ItemStack(Material.NAME_TAG));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		return "Have " + instance.getCustomInt() + " be at least " + instance.getAmount();
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
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
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		String placeholder = changes.getCustomString();
		putButton(10, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"&7Placeholder: &r&o" + (placeholder.length() > 0 ? "&a" + placeholder : "&r&o-none-"),
						"",
						"&e> Click to set placeholder").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					p.closeInventory();
					PlayerTools.promptInput(
							p,
							new SinglePrompt("&aEnter a placeholder (exit() to cancel):", (c,s) -> {
								
								if(!s.equalsIgnoreCase("exit()")) {
									p.sendMessage("Setting placeholder to "+s);
									changes.setCustomString(s);
									changes.apply();
								}
								else
									p.sendMessage("Cancelling...");
								
								QuestBook.openQuestMissionEditor(p, changes.getSource());
								return true;
							}
					));
				}
		));
		putButton(17, MissionButton.amount(changes));
	}

}
