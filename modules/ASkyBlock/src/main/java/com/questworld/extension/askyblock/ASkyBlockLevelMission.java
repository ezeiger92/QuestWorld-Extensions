package com.questworld.extension.askyblock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.events.IslandLevelEvent;

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.Ticking;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;

public class ASkyBlockLevelMission extends MissionType implements Listener, Ticking {
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
		for(MissionEntry r : QuestWorld.getMissionEntries(this, Bukkit.getOfflinePlayer(e.getPlayer())))
			r.setProgress(e.getLevel());
	}

	@Override
	public void onManual(Player player, MissionEntry entry) {
		entry.setProgress(ASkyBlockAPI .getInstance().getIslandLevel(player.getUniqueId()));
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(17, MissionButton.amount(changes));
	}
}
