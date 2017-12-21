package com.questworld.extension.extras;

import me.mrCookieSlime.QuestWorld.api.QuestExtension;

public class Extras extends QuestExtension {
	public Extras() {
		setMissionTypes(
			new ClickBlockMission(),
			new CommandMission(),
			new HarvestMission(),
			new SubmitAtMission());
	}
}
