package com.questworld.extension.askyblock;

import com.questworld.api.QuestExtension;

public class ASkyBlock extends QuestExtension {
	public ASkyBlock() {
		super("ASkyBlock");
		setMissionTypes(new ASkyBlockLevelMission());
	}
}