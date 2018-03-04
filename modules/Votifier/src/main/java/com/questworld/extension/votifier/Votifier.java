package com.questworld.extension.votifier;

import com.questworld.api.QuestExtension;

public class Votifier extends QuestExtension {
	public Votifier() {
		super("Votifier");
		setMissionTypes(new VoteMission());
	}
}
