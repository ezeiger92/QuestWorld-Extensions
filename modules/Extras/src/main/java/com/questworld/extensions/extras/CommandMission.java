package com.questworld.extensions.extras;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.MissionChange;
import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.SinglePrompt;
import me.mrCookieSlime.QuestWorld.api.interfaces.IMission;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.quests.QuestBook;
import me.mrCookieSlime.QuestWorld.utils.ItemBuilder;
import me.mrCookieSlime.QuestWorld.utils.PlayerTools;

public class CommandMission extends MissionType implements Listener {

	public CommandMission() {
		super("COMMAND", false, false, new ItemStack(Material.COMMAND));
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
		// Remove first character ('/')
		String command = event.getMessage().substring(1);
		
		QuestWorld.getInstance().getManager(event.getPlayer()).forEachTaskOf(this, mission -> {
			// Trim leading '/' here as well before check
			boolean matches = mission.getCustomString().startsWith(command, 1);
			
			if(matches)
				event.setCancelled(true);
			
			return matches;
		});
	}
	
	@Override
	protected void layoutMenu(MissionChange changes) {
		super.layoutMenu(changes);
		
		putButton(10, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.NAME_TAG).display("&dCommand").lore(
						changes.getCustomString().length()==0 ? "&cNo command set" : "&a"+changes.getCustomString(),
						"",
						"&eLeft Click to Set Command",
						"&eRight Click to Clear Command"
				).get(),
				event -> {
					if(event.isRightClick()) {
						changes.setCustomString("");
						if(changes.sendEvent())
							changes.apply();
					}
					else {
						Player p = (Player)event.getWhoClicked();
						p.closeInventory();
						PlayerTools.promptCommand(
								p,
								new SinglePrompt("&aEnter a command (/# to cancel):", (c,s) -> {
									
									if(!s.startsWith("/#")) {
										p.sendMessage("Setting command to "+s);
										changes.setCustomString(s);
										if(changes.sendEvent())
											changes.apply();
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
