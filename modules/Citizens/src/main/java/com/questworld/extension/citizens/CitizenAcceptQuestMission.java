package com.questworld.extension.citizens;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.PlayerRunnable;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage.HoverAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.CustomBookOverlay;
import me.mrCookieSlime.QuestWorld.api.QuestWorld;
import me.mrCookieSlime.QuestWorld.api.SinglePrompt;
import me.mrCookieSlime.QuestWorld.api.Translation;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.contract.MissionEntry;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.api.menu.QuestBook;
import me.mrCookieSlime.QuestWorld.util.ItemBuilder;
import me.mrCookieSlime.QuestWorld.util.PlayerTools;
import me.mrCookieSlime.QuestWorld.util.Text;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizenAcceptQuestMission extends CitizenInteractMission {
	public CitizenAcceptQuestMission() {
		setName("ACCEPT_QUEST_FROM_NPC");
		this.setSelectorItem(new ItemStack(Material.BOOK_AND_QUILL));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		String name = "N/A";
		NPC npc = Citizens.npcFrom(instance);
		if(npc != null)
			name = npc.getName();
		
		return "&7Accept this Quest by talking to " + name;
	}
	
	private void book(Player p, NPC npc, MissionEntry result) {
		book(p, npc, result);
	}
	
	private void book(Player p, NPC npc, MissionEntry result, boolean back) {
		TellRawMessage lore = new TellRawMessage();
		lore.addText(npc.getName() + ":\n\n");
		lore.addText(Text.colorize(result.getMission().getDescription()));
		lore.color(ChatColor.DARK_AQUA);
		lore.addText("\n\n    ");
		lore.addText(Text.colorize("&7( &a&l\u2714 &7)"));
		lore.addHoverEvent(HoverAction.SHOW_TEXT, Text.colorize("&7Click to accept this Quest"));
		lore.addClickEvent(new PlayerRunnable(3) {
			
			@Override
			public void run(Player p) {
				result.addProgress(1);
			}
		});
		lore.addText("      ");
		lore.addText(Text.colorize("&7( &4&l\u2718 &7)"));
		if(back) {
			lore.addHoverEvent(HoverAction.SHOW_TEXT, Text.colorize("&7Click to go back"));
			lore.addClickEvent(new PlayerRunnable(3) {
				@Override
				public void run(Player p) {
					list(p, npc);
				}
			});
		}
		else {
			lore.addHoverEvent(HoverAction.SHOW_TEXT, Text.colorize("&7Click to do this Quest later"));
			lore.addClickEvent(new PlayerRunnable(3) {
				@Override
				public void run(Player p) {
				}
			});
		}
		
		new CustomBookOverlay("Quest", "TheBusyBiscuit", lore).open(p);
	}
	
	private void list(Player p, NPC npc) {
		ArrayList<MissionEntry> available = new ArrayList<>();
		
		for(MissionEntry result : QuestWorld.getMissionEntries(this, p))
			if(result.getMission().getCustomInt() == npc.getId())
				available.add(result);
		
		if(available.isEmpty())
			return;
		
		if(available.size() == 1) {
			book(p, npc, available.get(0));
			return;
		}
		
		TellRawMessage lore = new TellRawMessage();
		lore.addText(npc.getName() + ":\n");
		lore.addText("  Available Quests:\n\n");
		
		for(MissionEntry entry : available) {
			lore.addText("    " + "" + "\n");
			lore.addHoverEvent(HoverAction.SHOW_TEXT, String.join("\n", Text.wrap(32, 
					Text.colorize(entry.getMission().getDescription())
					)));
			lore.addClickEvent(new PlayerRunnable(3) {
				
				@Override
				public void run(Player p) {
					book(p, npc, entry, true);
				}
			});
		}
		new CustomBookOverlay("Quest", "TheBusyBiscuit", lore).open(p);
	}
	
	@Override
	@EventHandler
	public void onInteract(NPCRightClickEvent e) {
		list(e.getClicker(), e.getNPC());
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(11, MissionButton.simpleButton(changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"&rQuest Description",
						"",
						changes.getDescription(),
						"",
						"&e> Edit the Quest's Description",
						"&7(Color Codes supported)").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					
					p.closeInventory();
					PlayerTools.promptInput(p, new SinglePrompt(
							PlayerTools.makeTranslation(true, Translation.MISSION_DESC_EDIT),
							(c,s) -> {
								changes.setDescription(Text.decolor(Text.colorize(s)));
								if(changes.apply()) {
									PlayerTools.sendTranslation(p, true, Translation.MISSION_DESC_SET, changes.getText(), changes.getDescription());
								}
								QuestBook.openQuestMissionEditor((Player) c.getForWhom(), changes.getSource());
								return true;
							}
					));
				}
		));
	}
}
