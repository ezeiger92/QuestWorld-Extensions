package com.questworld.extension.chatreaction;

import com.questworld.api.QuestExtension;

public class ChatReaction extends QuestExtension {
	public ChatReaction() {
		super("ChatReaction");
		setMissionTypes(new ChatReactMission());
	}
}
