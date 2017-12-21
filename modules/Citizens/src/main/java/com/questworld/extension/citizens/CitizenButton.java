package com.questworld.extension.citizens;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.event.GenericPlayerLeaveEvent;
import me.mrCookieSlime.QuestWorld.api.menu.MenuData;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.api.menu.QuestBook;
import me.mrCookieSlime.QuestWorld.util.ItemBuilder;
import me.mrCookieSlime.QuestWorld.util.PlayerTools;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizenButton {
	public static MenuData select(IMissionState changes) {
		NPC npc = Citizens.npcFrom(changes);
		return MissionButton.simpleButton(changes,
				new ItemBuilder(Material.NAME_TAG).wrapText(
						"&dCitizen &f#" + changes.getCustomInt(),
						"&7Name: &r" + (npc != null ? npc.getName(): "&4N/A"),
						"",
						"&e> Click to change the selected NPC").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					
					if(event.isRightClick()) {
						changes.setCustomInt(0);
						if(changes.apply()) {
							
						}
					}
					else {
						PlayerTools.sendTranslation(p, true, CitizenTranslation.CITIZEN_NPC_EDIT);
						link.put(p.getUniqueId(), changes);
						p.closeInventory();
					}
				}
		);
	}

	private static Map<UUID, IMissionState> link = new HashMap<>();
	public static class Listener implements org.bukkit.event.Listener {
		@EventHandler
		public void onInteract(NPCRightClickEvent e) {
			Player p = e.getClicker();
			IMissionState changes = link.remove(p.getUniqueId());
			if (changes != null) {
				changes.setCustomInt(e.getNPC().getId());
				
				if(changes.apply()) {
					PlayerTools.sendTranslation(p, true, CitizenTranslation.CITIZEN_NPC_SET);
				}
				
				QuestBook.openQuestMissionEditor(p, changes.getSource());
			}
		}
		
		@EventHandler
		public void onLeave(GenericPlayerLeaveEvent event) {
			link.remove(event.getPlayer().getUniqueId());
		}
	}
}
