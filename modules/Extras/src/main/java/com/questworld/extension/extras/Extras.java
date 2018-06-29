package com.questworld.extension.extras;

import org.bukkit.plugin.Plugin;

import com.questworld.api.QuestExtension;

public class Extras extends QuestExtension {
	@Override
	protected void initialize(Plugin plugin) {
		setMissionTypes(
			new ClickBlockMission(),
			new CommandMission(),
			new DoQuestMission(),
			new HarvestMission(),
			new StatisticMission(),
			new SubmitAtMission());
	}
}
