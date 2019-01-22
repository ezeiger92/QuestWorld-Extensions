package com.questworld.extension.askyblock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.QuestWorld;
import com.questworld.api.Ticking;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.MissionEntry;
import com.questworld.api.menu.MissionButton;
import com.questworld.util.version.ObjectMap.VDMaterial;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.events.IslandPostLevelEvent;

public class ASkyBlockLevelMission extends MissionType implements Listener, Ticking {
	public ASkyBlockLevelMission() {
		super("ASKYBLOCK_REACH_ISLAND_LEVEL", false, new ItemStack(Material.GRASS));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return new ItemStack(VDMaterial.COMMAND_BLOCK);
	}
	
	@Override
	protected String userInstanceDescription(IMission instance) {
		return "&7Reach Island Level " + instance.getAmount();
	}
	
	@EventHandler
	public void onWin(final IslandPostLevelEvent e) {
		for(MissionEntry r : QuestWorld.getMissionEntries(this, Bukkit.getOfflinePlayer(e.getPlayer())))
			r.setProgress((int)e.getLongLevel());
	}

	@Override
	public void onManual(Player player, MissionEntry entry) {
		entry.setProgress((int)ASkyBlockAPI.getInstance().getLongIslandLevel(player.getUniqueId()));
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(17, MissionButton.amount(changes));
	}
}
