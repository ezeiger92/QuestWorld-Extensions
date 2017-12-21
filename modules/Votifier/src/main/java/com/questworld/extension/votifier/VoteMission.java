package com.questworld.extension.votifier;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.vexsoftware.votifier.model.VotifierEvent;

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.util.PlayerTools;

public class VoteMission extends MissionType implements Listener {
	public VoteMission() {
		super("VOTIFIER_VOTE", true, new ItemStack(Material.PAPER));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return new ItemStack(Material.COMMAND);
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
