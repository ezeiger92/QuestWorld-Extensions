package com.questworld.extension.extras;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.questworld.api.MissionType;
import com.questworld.api.QuestWorld;
import com.questworld.api.contract.ICategory;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IMissionState;
import com.questworld.api.contract.IQuest;
import com.questworld.api.contract.MissionEntry;
import com.questworld.api.event.QuestCompleteEvent;
import com.questworld.api.menu.Menu;
import com.questworld.api.menu.MissionButton;
import com.questworld.api.menu.PagedMapping;
import com.questworld.api.menu.QuestBook;
import com.questworld.util.ItemBuilder;

public class DoQuestMission extends MissionType implements Listener {

	public DoQuestMission() {
		super("DO_QUEST", true, new ItemStack(Material.MAP));
	}

	@Override
	protected String userInstanceDescription(IMission instance) {
		String name = "a quest";
		
		try {
			UUID uuid = UUID.fromString(instance.getCustomString());
			IQuest quest = QuestWorld.getFacade().getQuest(uuid);
			
			if(quest != null)
				name = "\"" + quest.getName() + "\"";
		}
		catch(IllegalArgumentException e) {
			// No quest found
			
			if(instance.getCustomString().length() != 0)
				name = "(unknown quest error)";
		}
		
		return "&7Complete " + name + "&7 " + instance.getAmount() + " times";
	}

	@Override
	public ItemStack userDisplayItem(IMission instance) {
		return getSelectorItem().clone();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuestComplete(QuestCompleteEvent event) {
		for(MissionEntry entry : QuestWorld.getMissionEntries(this, event.getPlayer())) {
			if(event.getQuest().getUniqueId().toString().equals(entry.getMission().getCustomString())) {
				entry.addProgress(1);
			}
		}
	}
	
	@Override
	protected void layoutMenu(IMissionState changes) {
		ItemStack item = new ItemStack(Material.MAP);
		IQuest quest;
		try {
			UUID uuid = UUID.fromString(changes.getCustomString());
			quest = QuestWorld.getFacade().getQuest(uuid);
			
			if(quest != null)
				item = new ItemBuilder(quest.getItem()).wrapText(quest.getName()).get();
		}
		catch(IllegalArgumentException e) {
			quest = null;
		}
		
		putButton(10, MissionButton.simpleButton(changes,
				new ItemBuilder(item).wrapText(
						"Quest: " + (quest != null ? quest.getName() : "&r&o-none-"),
						"",
						"&e> Click to select quest").get(),
				event -> {
					if(event.isRightClick())
						changes.setCustomString("");
					else {
						missionTargetCategories((Player) event.getWhoClicked(), changes);
					}
					
				}
		));
		
		putButton(17, MissionButton.amount(changes));
	}
	
	public static void missionTargetCategories(Player p, final IMissionState mission) {
		QuestWorld.getSounds().EDITOR_CLICK.playTo(p);

		Menu menu = new Menu(1, "&3Categories");

		PagedMapping pager = new PagedMapping(45, 9);
		for (ICategory category : QuestWorld.getFacade().getCategories()) {
			pager.addButton(category.getID(), new ItemBuilder(category.getItem())
					.wrapText(category.getName(), "", "&e> Click to open category").get(), event -> {
						Player p2 = (Player) event.getWhoClicked();
						PagedMapping.putPage(p2, 0);
						missionTargetQuests(p2, mission, category);
					}, true);
		}
		
		pager.setBackButton(" &3Mission editor", event -> QuestBook.openQuestMissionEditor(p, mission.getSource()));
		
		pager.build(menu, p);
		menu.openFor(p);
	}

	private static void missionTargetQuests(Player p, final IMissionState mission, ICategory category) {
		QuestWorld.getSounds().EDITOR_CLICK.playTo(p);

		Menu menu = new Menu(1, "&3Quests");

		String name = mission.getDisplayName();

		PagedMapping pager = new PagedMapping(45, 9);
		for (IQuest quest : category.getQuests()) {
			if(quest != mission.getQuest()) {
				pager.addButton(quest.getID(), new ItemBuilder(quest.getItem()).wrapText(quest.getName(), "",
						"&e> Click to pick quest for &f&o" + name).get(),
						event -> {
							Player p2 = (Player) event.getWhoClicked();
							PagedMapping.popPage(p2);
	
							mission.setCustomString(quest.getUniqueId().toString());
							
							if(mission.apply())
								QuestBook.openQuestMissionEditor(p, mission.getSource());
							
						}, false);
			}
			else {
				pager.addButton(quest.getID(), new ItemBuilder(Material.BARRIER)
						.wrapText(category.getName(), "", "&c> Requirement cycle").get(), null, false);
			}
		}
		
		pager.setBackButton(" &3Categories", event -> missionTargetCategories(p, mission));
		pager.build(menu, p);
		menu.openFor(p);
	}
}
