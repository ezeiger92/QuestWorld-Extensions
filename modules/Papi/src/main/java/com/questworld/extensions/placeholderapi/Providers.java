package com.questworld.extensions.placeholderapi;

import java.util.Locale;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.questworld.api.QuestStatus;
import com.questworld.api.QuestWorld;
import com.questworld.api.contract.ICategory;
import com.questworld.util.Log;

import me.clip.placeholderapi.PlaceholderHook;

public class Providers extends PlaceholderHook {

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if(player == null)
			return null;
		String[] args = identifier.split("_");
		
		// questworld_category_<uuid>
		// questworld_countquests[_<status-with-dashes>[_<category-uuid>]]
		switch(args[0]) {
			case "getcategoryprogress":
			case "getquestprogress":
			case "getmissionprogress":
				

			case "getcategorytotal":
			case "getquesttotal":
			case "getmissiontotal":
				
			case "countquests":
				QuestStatus status = null;
				ICategory category = null;
				if(args.length > 1) {
					try {
						status = QuestStatus.valueOf(args[1].replace('-',  '_').toUpperCase(Locale.ENGLISH));
					}
					catch(IllegalArgumentException e) {
						// Invalid status
						Log.warning("Invalid status in placeholder: " + identifier);
						break;
					}
					
					if(args.length > 2) {
						try {
							category = QuestWorld.getFacade().getCategory(UUID.fromString(args[2]));
						}
						catch(IllegalArgumentException e) {
							// Invalid uuid
							Log.warning("Invalid uuid in placeholder: " + identifier);
							break;
						}
					}
				}
				
				return String.valueOf(QuestWorld.getPlayerStatus(player).countQuests(category, status));
				
			default:
				break;
		}
		
		return null;
	}
}
