package com.questworld.extension.votifier;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.QuestWorld;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.MissionEntry;
import com.questworld.api.menu.MissionButton;
import com.questworld.util.PlayerTools;
import com.questworld.util.version.ObjectMap.VDMaterial;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteMission extends MissionType implements Listener {
	public VoteMission() {
		super("VOTIFIER_VOTE", true, new ItemStack(Material.PAPER));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return new ItemStack(VDMaterial.COMMAND_BLOCK);
	}
	
	@Override
	protected String userInstanceDescription(IMission instance) {
		return "&7Vote " + instance.getAmount() + " times";
	}
	
	@EventHandler
	public void onVote(VotifierEvent e) {
		Player p = PlayerTools.getPlayer(e.getVote().getUsername());
		if (p != null)
			for(MissionEntry r : QuestWorld.getMissionEntries(this, p))
				r.addProgress(1);
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		super.layoutMenu(changes);
		putButton(17, MissionButton.amount(changes));
	}
}
