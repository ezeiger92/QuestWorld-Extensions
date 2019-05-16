package com.questworld.extension.extras;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.Statistic;
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
import com.questworld.util.Text;
import com.questworld.util.version.ObjectMap.VDMaterial;

public class StatisticMission extends MissionType implements Ticking {

	public StatisticMission() {
		super("STATISTIC", false, new ItemStack(VDMaterial.CLOCK));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		return "&7Increment " + Text.niceName(instance.getCustomString()) + " to " + instance.getAmount();
	}
	
	private Statistic getStatistic(IMission instance) {
		try {
			return Statistic.valueOf(instance.getCustomString().toUpperCase(Locale.ENGLISH));
		}
		catch(IllegalArgumentException e) {}
		
		return Statistic.JUMP;
	}
	
	private static boolean statisticExists(String statisticName) {
		try {
			Statistic.valueOf(statisticName.toUpperCase(Locale.ENGLISH));
			return true;
		}
		catch(IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}

	@Override
	public void onManual(Player player, MissionEntry entry) {
		Statistic stat = getStatistic(entry.getMission());
		Statistic.Type type = stat.getType();
		
		int result;
		if(type == Statistic.Type.BLOCK || type == Statistic.Type.ITEM) {
			result = player.getStatistic(stat, entry.getMission().getItem().getType());
		}
		else if(type == Statistic.Type.ENTITY) {
			result = player.getStatistic(stat, entry.getMission().getEntity());
		}
		else {
			result = player.getStatistic(stat);
		}
		
		entry.setProgress(result);
	}

	@Override
	public String getLabel() {
		return "&r> Click to check stats";
	}
	
	@Override
	public void validate(IMissionState missionState) {
		if(!statisticExists(missionState.getCustomString())) {
			missionState.setCustomString(Statistic.JUMP.name());
			missionState.apply();
		}
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		Statistic.Type type = getStatistic(changes).getType();
		
		putButton(10, MissionButton.simpleButton(changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"&7Statistic: &r" + changes.getCustomString(),
						"",
						"&e> Click to change the Name").get(),
				event -> {
					Player p = (Player) event.getWhoClicked();

					p.closeInventory();
					PlayerTools.promptInput(p, new SinglePrompt(
							"&aEnter a statistic name (cancel() to abort):",
							(c, s) -> {
								if(!s.equalsIgnoreCase("cancel()")) {
									if(!statisticExists(s)) {
										SinglePrompt.setNextDisplay(c, "&cInvalid statistic: &r" + s);
										return false;
									}
									
									changes.setCustomString(s.toUpperCase(Locale.ENGLISH));
									p.sendMessage(Text.colorize("&aSuccessfully changed statistic"));
								}
								else {
									p.sendMessage(Text.colorize("&7Aborting..."));
								}
								QuestBook.openQuestMissionEditor(p, changes);
								return true;
							}
					));
				}
		));
		
		if(type == Statistic.Type.BLOCK || type == Statistic.Type.ITEM) {
			putButton(11, MissionButton.item(changes));
		}
		else if(type == Statistic.Type.ENTITY) {
			putButton(11, MissionButton.entity(changes));
		}
		
		putButton(17, MissionButton.amount(changes));
	}
}
