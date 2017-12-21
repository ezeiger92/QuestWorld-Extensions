package com.questworld.extension.askyblock;

import me.mrCookieSlime.QuestWorld.api.QuestExtension;

public class ASkyBlock extends QuestExtension {
	public ASkyBlock() {
		super("ASkyBlock");
		setMissionTypes(new ASkyBlockLevelMission());
	}
}