package com.questworld.extension.askyblock;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.wasteofplastic.askyblock.events.IslandLevelEvent;

import me.mrCookieSlime.QuestWorld.api.MissionSet;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;

public class ASkyBlockLevelMission extends MissionType implements Listener {
	public ASkyBlockLevelMission() {
		super("ASKYBLOCK_REACH_ISLAND_LEVEL", false, new ItemStack(Material.GRASS));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return new ItemStack(Material.COMMAND);
	}
	
	@Override
	protected String userInstanceDescription(IMission instance) {
		return "&7Reach Island Level " + instance.getAmount();
	}
	
	@EventHandler
	public void onWin(final IslandLevelEvent e) {
		for(MissionSet.Result r : MissionSet.of(this, e.getPlayer()))
			r.setProgress(e.getLevel());
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		super.layoutMenu(changes);
		putButton(17, MissionButton.amount(changes));
	}
}
