package com.questworld.extension.citizens;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import static me.mrCookieSlime.QuestWorld.util.json.Prop.*;

import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawString;
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
import me.mrCookieSlime.QuestWorld.util.json.JsonBlob;
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
		book(p, npc, result, false);
	}
	
	private void book(Player p, NPC npc, MissionEntry result, boolean back) {
		String legacy = Text.colorize(npc.getName() + "&r: " +
				result.getMission().getQuest().getName() + "&r\n\n" +
				result.getMission().getDescription() + "\n\n    ").replace("\\n", "\n");
		
		JsonBlob blob = JsonBlob.fromLegacy(legacy, BLACK)
				.addLegacy(Text.colorize("&7( &a&l\u2714 &7)"), BLACK, 
						HOVER_TEXT("Click to accept this Quest", GRAY),
						CLICK_RUN(p, () -> result.addProgress(1) ))
				.add("      ")
				.addLegacy(Text.colorize("&7( &4&l\u2718 &7)"), BLACK, 
						HOVER_TEXT(back ? "Click to go back" : "Click to do this Quest later", GRAY),
						CLICK_RUN(p, () -> {
							if(back)
								list(p, npc);
						}));
		
		new CustomBookOverlay("Quest", "TheBusyBiscuit", new TellRawString(blob.toString())).open(p);
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
		
		JsonBlob blob = new JsonBlob("Available Quests:\n", BLACK);
		
		for(MissionEntry entry : available) {
			blob.add("+ ", DARK_BLUE)
				.addLegacy(Text.colorize(entry.getMission().getQuest().getName()).replace("\\n", "\n") + "\n", BLACK,
					HOVER_TEXT(String.join("\n", Text.wrap(32, 
							Text.colorize(entry.getMission().getDescription())
					))),
					CLICK_RUN(p, () -> {
						book(p, npc, entry, true);
					}));
		}
		new CustomBookOverlay("Quest", "TheBusyBiscuit", new TellRawString(blob.toString())).open(p);
	}
	
	@Override
	@EventHandler
	public void onInteract(NPCRightClickEvent e) {
		list(e.getClicker(), e.getNPC());
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		putButton(10, CitizenButton.select(changes));
		putButton(11, MissionButton.simpleButton(changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"&rQuest Description",
						"",
						changes.getDescription().replace("\n", "\\n"),
						"",
						"&e> Edit the Quest's Description",
						"&7(Color Codes supported)").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					
					p.closeInventory();
					PlayerTools.promptInput(p, new SinglePrompt(
							PlayerTools.makeTranslation(true, Translation.MISSION_DESC_EDIT),
							(c,s) -> {
								changes.setDescription(Text.deserializeNewline(Text.colorize(s)));
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
