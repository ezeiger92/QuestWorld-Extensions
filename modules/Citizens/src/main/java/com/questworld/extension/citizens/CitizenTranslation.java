package com.questworld.extension.citizens;

import me.mrCookieSlime.QuestWorld.api.Translator;

public enum CitizenTranslation implements Translator {
	CITIZEN_NPC_EDIT("editor.link-citizen"),
	CITIZEN_NPC_SET ("editor.link-citizen-finished"),
	;
	private String path;
	private String[] placeholders;
	CitizenTranslation(String path, String... placeholders) {
		this.path = path;
		this.placeholders = wrap(placeholders);
	}
	
	@Override
	public String path() {
		return path;
	}
	
	@Override
	public String[] placeholders() {
		return placeholders;
	}
}
