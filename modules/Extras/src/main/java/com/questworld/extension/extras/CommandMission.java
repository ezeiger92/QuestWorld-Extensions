package com.questworld.extension.extras;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.QuestWorld;
import com.questworld.api.SinglePrompt;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.MissionEntry;
import com.questworld.api.menu.MissionButton;
import com.questworld.api.menu.QuestBook;
import com.questworld.util.ItemBuilder;
import com.questworld.util.PlayerTools;
import com.questworld.util.Text;
import com.questworld.util.version.ObjectMap.VDMaterial;

public class CommandMission extends MissionType implements Listener {

	public CommandMission() {
		super("COMMAND", false, new ItemStack(VDMaterial.COMMAND_BLOCK));
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
				if(r.getMission().getCustomInt() == 0)
					event.setCancelled(true);
				
				r.addProgress(1);
			}
	}
	
	@Override
	public void validate(IMissionState state) {
		if(!state.getCustomString().startsWith("/")) {
			state.setCustomString("/command");
			state.apply();
		}
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
							new SinglePrompt("&aEnter a command (/exit to abort):", (c,s) -> {
								
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
		
		putButton(11, MissionButton.simpleButton(changes,
				new ItemBuilder(VDMaterial.COMMAND_BLOCK).wrapText(
						"&7Consume command: " + Text.booleanBadge(changes.getCustomInt() == 0),
						"",
						"&e> Will the command be consumed and not run?"
						).get(),
				
				event -> {
					changes.setCustomInt(changes.getCustomInt() == 0 ? 1 : 0);
				}
		));
	}

}
