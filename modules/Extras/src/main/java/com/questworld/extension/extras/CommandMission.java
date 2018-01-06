package com.questworld.extension.extras;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.SinglePrompt;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
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
		return "Use " + instance.getCustomString();
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}

	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		
		for(MissionEntry r : QuestWorld.getMissionEntries(this, event.getPlayer()))
			if(event.getMessage().equalsIgnoreCase(r.getMission().getCustomString())) {
				event.setCancelled(true);
				r.addProgress(1);
			}
	}
	
	@Override
	public void validate(IMissionState state) {
		if(!state.getCustomString().startsWith("/"))
			state.setCustomString("/command");
		
		state.apply();
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		String command = changes.getCustomString();
		putButton(10, MissionButton.simpleButton(
				changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"&7Command: &r&o" + (command.length() > 0 ? "&a" + command : "&r&o-none-"),
						"",
						"&e> Click to set command").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					p.closeInventory();
					PlayerTools.promptCommand(
							p,
							new SinglePrompt("&aEnter a command (/exit to cancel):", (c,s) -> {
								
								if(!s.equalsIgnoreCase("/exit")) {
									p.sendMessage("Setting command to "+s);
									changes.setCustomString(s);
									changes.apply();
								}
								else
									p.sendMessage("Cancelling...");
								
								QuestBook.openQuestMissionEditor(p, changes.getSource());
								return true;
							}
					));
				}
		));
	}

}
