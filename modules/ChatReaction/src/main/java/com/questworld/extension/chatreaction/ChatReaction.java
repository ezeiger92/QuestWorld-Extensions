package com.questworld.extension.chatreaction;

import me.mrCookieSlime.QuestWorld.api.QuestExtension;

public class ChatReaction extends QuestExtension {
	public ChatReaction() {
		super("ChatReaction");
		setMissionTypes(new ChatReactMission());
	}
}
