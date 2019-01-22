package com.questworld.extension.chatreaction;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.QuestWorld;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.MissionEntry;
import com.questworld.api.menu.MissionButton;
import com.questworld.util.version.ObjectMap.VDMaterial;

import me.clip.chatreaction.events.ReactionWinEvent;

public class ChatReactMission extends MissionType implements Listener {
	public ChatReactMission() {
		super("CHATREACTION_WIN", true, new ItemStack(Material.DIAMOND));
	}
	
	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return new ItemStack(VDMaterial.COMMAND_BLOCK);
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
		for(MissionEntry r : QuestWorld.getMissionEntries(this, e.getWinner()))
			r.addProgress(1);
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(17, MissionButton.amount(changes));
	}
}
