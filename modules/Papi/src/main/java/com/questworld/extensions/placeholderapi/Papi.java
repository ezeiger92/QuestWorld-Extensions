package com.questworld.extensions.placeholderapi;

import org.bukkit.plugin.Plugin;

import com.questworld.api.QuestExtension;

import me.clip.placeholderapi.PlaceholderAPI;

public class Papi extends QuestExtension {
	public Papi() {
		super("PlaceholderAPI");
		setMissionTypes(new PlaceholderMission());
	}
	
	@Override
	protected void initialize(Plugin parent) {
		PlaceholderAPI.registerPlaceholderHook(parent, new Providers());
	}
}
