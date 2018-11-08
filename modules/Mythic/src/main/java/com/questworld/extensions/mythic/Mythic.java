package com.questworld.extensions.mythic;

import org.bukkit.plugin.Plugin;

import com.questworld.api.QuestExtension;

public class Mythic extends QuestExtension {
	public Mythic() {
		super("MythicMobs");
	}
	
	@Override
	public void initialize(Plugin parent) {
		setMissionTypes(new MythicKill());
	}
}
