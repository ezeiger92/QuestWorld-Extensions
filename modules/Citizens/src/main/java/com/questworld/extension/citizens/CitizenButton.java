package com.questworld.extension.citizens;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.mrCookieSlime.QuestWorld.api.contract.IMissionState;
import me.mrCookieSlime.QuestWorld.api.menu.MenuData;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.util.ItemBuilder;
import me.mrCookieSlime.QuestWorld.util.PlayerTools;
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
						Citizens.link.put(p.getUniqueId(), changes);
						PlayerTools.closeInventoryWithEvent(p);
					}
				}
		);
	}
}
