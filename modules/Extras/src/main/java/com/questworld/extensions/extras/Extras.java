package com.questworld.extensions.extras;

import org.bukkit.plugin.Plugin;

import me.mrCookieSlime.QuestWorld.api.MissionType;
import me.mrCookieSlime.QuestWorld.api.QuestExtension;

public class Extras extends QuestExtension {
	private MissionType[] missions;
	
	@Override
	public String[] getDepends() {
		return null;
	}

	@Override
	protected void initialize(Plugin parent) {
		missions = new MissionType[] {
			new ClickBlockMission(),
			new CommandMission(),
			new HarvestMission(),
			new SubmitAtMission(),
		};
	}

	@Override
	public MissionType[] getMissions() {
		return missions;
	}

}
