package com.questworld.extensions.placeholderapi;

import org.bukkit.plugin.Plugin;

import com.questworld.api.QuestExtension;

public class Papi extends QuestExtension {
	public Papi() {
		super("PlaceholderAPI");
		setMissionTypes(new PlaceholderMission());
	}
	
	@Override
	protected void initialize(Plugin parent) {
		new Providers(parent);
	}
}
