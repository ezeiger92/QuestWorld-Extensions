package com.questworld.extension.extras;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.api.MissionSet;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.SinglePrompt;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.api.menu.QuestBook;
import me.mrCookieSlime.QuestWorld.util.ItemBuilder;
import me.mrCookieSlime.QuestWorld.util.PlayerTools;

public class CommandMission extends MissionType implements Listener {

	public CommandMission() {
		super("COMMAND", false, new ItemStack(Material.COMMAND));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		return "Use " + (instance.getCustomString().length() == 0 ? " a command" : instance.getCustomString());
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}

	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		
		for(MissionSet.Result r : MissionSet.of(this, event.getPlayer()))
			if(event.getMessage().equalsIgnoreCase(r.getMission().getCustomString())) {
				event.setCancelled(true);
				r.addProgress(1);
			}
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		super.layoutMenu(changes);
		
		putButton(10, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"&dCommand",
						changes.getCustomString().length()==0 ? "&cNo command set" : "&a"+changes.getCustomString(),
						"",
						"&eLeft Click to Set Command",
						"&eRight Click to Clear Command"
				).get(),
				event -> {
					if(event.isRightClick()) {
						changes.setCustomString("");
					}
					else {
						Player p = (Player)event.getWhoClicked();
						PlayerTools.closeInventoryWithEvent(p);
						PlayerTools.promptCommand(
								p,
								new SinglePrompt("&aEnter a command (/# to cancel):", (c,s) -> {
									
									if(!s.startsWith("/#")) {
										p.sendMessage("Setting command to "+s);
										changes.setCustomString(s);
										if(changes.apply()) {
											
										}
									}
									else
										p.sendMessage("Cancelling...");
									
									QuestBook.openQuestMissionEditor(p, changes.getSource());
									return true;
								}
						));
					}
				}
		));
	}

}
