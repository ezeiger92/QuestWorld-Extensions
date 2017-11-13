package com.questworld.extension.chatreaction;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.clip.chatreaction.events.ReactionWinEvent;
import me.mrCookieSlime.QuestWorld.api.MissionSet;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;

public class ChatReactMission extends MissionType implements Listener {
	public ChatReactMission() {
		super("CHATREACTION_WIN", true, new ItemStack(Material.DIAMOND));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return new ItemStack(Material.COMMAND);
	}
	
	@Override
	protected String userInstanceDescription(IMission instance) {
		String games = " Games";
		if(instance.getAmount() == 1)
			games = " Game";
		
		return "&7Win " + instance.getAmount() + games + " of ChatReaction";
	}
	
	@EventHandler
	public void onWin(ReactionWinEvent e) {
		for(MissionSet.Result r : MissionSet.of(this, e.getWinner()))
			r.addProgress(1);
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		super.layoutMenu(changes);
		putButton(17, MissionButton.amount(changes));
	}
}
