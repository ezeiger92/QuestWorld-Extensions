package com.questworld.extensions.placeholderapi;

import java.util.Locale;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.questworld.api.QuestStatus;
import com.questworld.api.QuestWorld;
import com.questworld.api.contract.ICategory;
import com.questworld.api.contract.IMission;
import com.questworld.api.contract.IPlayerStatus;
import com.questworld.api.contract.IQuest;
import com.questworld.util.Log;

import me.clip.placeholderapi.PlaceholderHook;

public class Providers extends PlaceholderHook {
	
	private static UUID uuidOrNull(String in) {
		try {
			return UUID.fromString(in);
		}
		catch(IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if(player == null)
			return null;
		
		String[] args = identifier.split("_");
		
		IPlayerStatus playerStatus = QuestWorld.getPlayerStatus(player);
		
		// questworld_category_<uuid>
		// questworld_countquests[_<status-with-dashes>[_<category-uuid>]]
		switch(args[0]) {
			case "getcategoryprogress":
				if(args.length > 1) {
					ICategory category = QuestWorld.getFacade().getCategory(uuidOrNull(args[1]));
					
					if(category != null) {
						return String.valueOf(playerStatus.getProgress(category));
					}
				}
				break;
				
			case "getquestprogress":
				if(args.length > 1) {
					IQuest quest = QuestWorld.getFacade().getQuest(uuidOrNull(args[1]));
					
					if(quest != null) {
						return String.valueOf(playerStatus.getProgress(quest));
					}
				}
				break;
				
			case "getmissionprogress":
				if(args.length > 1) {
					IMission mission = QuestWorld.getFacade().getMission(uuidOrNull(args[1]));
					
					if(mission != null) {
						return String.valueOf(playerStatus.getProgress(mission));
					}
				}
				break;

			case "getcategorytotal":
				if(args.length > 1) {
					ICategory category = QuestWorld.getFacade().getCategory(uuidOrNull(args[1]));
					
					if(category != null) {
						return String.valueOf(category.getQuests().size());
					}
				}
				break;
				
			case "getquesttotal":
				if(args.length > 1) {
					IQuest quest = QuestWorld.getFacade().getQuest(uuidOrNull(args[1]));
					
					if(quest != null) {
						return String.valueOf(quest.getMissions().size());
					}
				}
				break;
				
			case "getmissiontotal":
				if(args.length > 1) {
					IMission mission = QuestWorld.getFacade().getMission(uuidOrNull(args[1]));
					
					if(mission != null) {
						return String.valueOf(mission.getAmount());
					}
				}
				break;
				
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
				
				return String.valueOf(playerStatus.countQuests(category, status));
				
			default:
				break;
		}
		
		return null;
	}
}
