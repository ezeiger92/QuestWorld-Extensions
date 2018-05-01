package com.questworld.extension.extras;

import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.Ticking;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.MissionEntry;
import com.questworld.util.Text;

public class StatisticMission extends MissionType implements Ticking {

	public StatisticMission() {
		super("STATISTIC", false, new ItemStack(Material.WATCH));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		return "Increment " + Text.niceName(instance.getCustomString()) + " to " + instance.getAmount();
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onManual(Player player, MissionEntry entry) {
		Statistic stat = Statistic.valueOf(entry.getMission().getCustomString().toUpperCase(Locale.ENGLISH));
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
		try {
			Statistic.valueOf(missionState.getCustomString().toUpperCase(Locale.ENGLISH));
		}
		catch(IllegalArgumentException e) {
			missionState.setCustomString(Statistic.TIME_SINCE_DEATH.name());
			missionState.apply();
		}
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		
	}
}
