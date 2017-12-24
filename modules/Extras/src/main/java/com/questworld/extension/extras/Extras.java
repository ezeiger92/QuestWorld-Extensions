package com.questworld.extension.extras;

import org.bukkit.plugin.Plugin;

import me.mrCookieSlime.QuestWorld.api.QuestExtension;

public class Extras extends QuestExtension {
	@Override
	protected void initialize(Plugin plugin) {
		setMissionTypes(
			new ClickBlockMission(),
			new CommandMission(),
			new HarvestMission(),
			new SubmitAtMission());
	}
}
