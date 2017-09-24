package com.questworld.extensions.citizens;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.mrCookieSlime.QuestWorld.api.MissionChange;
import me.mrCookieSlime.QuestWorld.api.SinglePrompt;
import me.mrCookieSlime.QuestWorld.api.menu.MenuData;
import me.mrCookieSlime.QuestWorld.api.menu.MissionButton;
import me.mrCookieSlime.QuestWorld.quests.QuestBook;
import me.mrCookieSlime.QuestWorld.utils.ItemBuilder;
import me.mrCookieSlime.QuestWorld.utils.PlayerTools;
import me.mrCookieSlime.QuestWorld.utils.Text;
import net.citizensnpcs.api.npc.NPC;

public class CitizenButton {
	public static MenuData rename(MissionChange changes) {
		NPC npc = Citizens.npcFrom(changes);
		return MissionButton.simpleButton(changes,
				new ItemBuilder(Material.NAME_TAG).display("&dCitizen &f#" + changes.getCustomInt()).lore(
						"&7Name: &r" + (npc != null ? npc.getName(): "&4N/A"),
						"",
						"&e> Click to change the selected NPC").get(),
				event -> {
					Player p = (Player)event.getWhoClicked();
					
					PlayerTools.promptInput(p, new SinglePrompt(
							PlayerTools.makeTranslation(true, CitizenTranslation.citizen_l),
							(c,s) -> {
								changes.setCustomString(Text.colorize(s));
								if(changes.sendEvent()) {
									PlayerTools.sendTranslation(p, true, CitizenTranslation.citizen_rename);
									changes.apply();
								}

								QuestBook.openQuestMissionEditor(p, changes.getSource());
								return true;
							}
					));

					PlayerTools.closeInventoryWithEvent(p);
				}
		);
	}
}
