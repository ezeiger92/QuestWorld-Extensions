package com.questworld.extension.citizens;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.PlayerRunnable;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage.HoverAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.CustomBookOverlay;
import me.mrCookieSlime.QuestWorld.api.MissionSet;
import me.mrCookieSlime.QuestWorld.api.SinglePrompt;
import me.mrCookieSlime.QuestWorld.api.Translation;
import me.mrCookieSlime.QuestWorld.api.contract.IMission;
import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
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
	
	private void book(Player p, NPC npc, MissionSet.Result result) {
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
		lore.addHoverEvent(HoverAction.SHOW_TEXT, Text.colorize("&7Click to do this Quest later"));
		lore.addClickEvent(new PlayerRunnable(3) {
			
			@Override
			public void run(Player p) {
			}
		});
		new CustomBookOverlay("Quest", "TheBusyBiscuit", lore).open(p);
	}
	
	@Override
	@EventHandler
	public void onInteract(NPCRightClickEvent e) {
		Player p = e.getClicker();
		
		Iterator<MissionSet.Result> iterator = MissionSet.of(this, p).iterator();
		
		while(iterator.hasNext()) {
			MissionSet.Result r = iterator.next();
			
			if(r.getMission().getCustomInt() == e.getNPC().getId()) {
				book(p, e.getNPC(), r);
				break;
			}
		}
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		super.layoutMenu(changes);
		
		// Old way
		/*List<String> lore = new ArrayList<String>();
		lore.add("");

		// Could be done with .split("?<=\\G.{32}"), but why regex when we don't need to?
		for(int i = 0, len = changes.getDescription().length(); i < len; i += 32) {
			int end = Math.min(i + 32, len);
			lore.add(changes.getDescription().substring(i, end));
		}
		
		lore.add("");
		lore.add("&e> Edit the Quest's Description");
		lore.add("&7(Color Codes are not supported)");*/
		
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
					
					PlayerTools.closeInventoryWithEvent(p);
				}
		));
	}
}
