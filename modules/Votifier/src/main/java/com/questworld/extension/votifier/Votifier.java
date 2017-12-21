package com.questworld.extension.votifier;

import me.mrCookieSlime.QuestWorld.api.QuestExtension;

public class Votifier extends QuestExtension {
	public Votifier() {
		super("Votifier");
		setMissionTypes(new VoteMission());
	}
}
