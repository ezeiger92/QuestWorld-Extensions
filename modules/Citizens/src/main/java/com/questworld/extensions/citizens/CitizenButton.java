package com.questworld.extensions.citizens;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.mrCookieSlime.QuestWorld.api.MissionChange;
import me.mrCookieSlime.QuestWorld.api.menu.MenuData;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.utils.ItemBuilder;
import me.mrCookieSlime.QuestWorld.utils.PlayerTools;
import net.citizensnpcs.api.npc.NPC;

public class CitizenButton {
	public static MenuData select(MissionChange changes) {
		NPC npc = Citizens.npcFrom(changes);
		return MissionButton.simpleButton(changes,
				new ItemBuilder(Material.NAME_TAG).display("&dCitizen &f#" + changes.getCustomInt()).lore(
						"&7Name: &r" + (npc != null ? npc.getName(): "&4N/A"),
						"",
						"&e> Click to change the selected NPC").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					
					if(event.isRightClick()) {
						changes.setCustomInt(0);
						if(changes.sendEvent())
							changes.apply();
					}
					else {
						PlayerTools.sendTranslation(p, true, CitizenTranslation.citizen_l);
						Citizens.link.put(p.getUniqueId(), changes);
						PlayerTools.closeInventoryWithEvent(p);
					}
				}
		);
	}
}
